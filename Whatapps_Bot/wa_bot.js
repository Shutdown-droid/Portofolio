const { makeWASocket, useMultiFileAuthState, DisconnectReason } = require('@whiskeysockets/baileys');
const pino = require('pino');
const qrcode = require('qrcode-terminal');
require('dotenv').config({ path: ['Key.env', '.env'] });

// Impor SDK secara aman
let GoogleGenAI, Groq, OpenAI;
try { GoogleGenAI = require('@google/genai').GoogleGenAI; } catch (e) {}
try { Groq = require('groq-sdk').Groq; } catch (e) {}
try { OpenAI = require('openai'); } catch (e) {}

const ai = (process.env.GEMINI_API_KEY && GoogleGenAI) ? new GoogleGenAI({ apiKey: process.env.GEMINI_API_KEY }) : null;
const groq = (process.env.GROQ_API_KEY && Groq) ? new Groq({ apiKey: process.env.GROQ_API_KEY }) : null;
const openrouter = (process.env.OPENROUTER_API_KEY && OpenAI) ? new OpenAI({ apiKey: process.env.OPENROUTER_API_KEY, baseURL: 'https://openrouter.ai/api/v1' }) : null;
const huggingface = (process.env.HUGGINGFACE_API_KEY && OpenAI) ? new OpenAI({ apiKey: process.env.HUGGINGFACE_API_KEY, baseURL: 'https://api-inference.huggingface.co/v1/' }) : null;

if (![ai, groq, openrouter, huggingface].some(Boolean)) {
    throw new Error('Atur setidaknya satu API key yang valid di dalam file .env atau Key.env.');
}

const userSessions = {};

// Menyimpan seluruh status chat per nomor
const chatStates = {}; 

async function generateAIResponse(senderID, messageText) {
    const systemPrompt = "Kamu adalah asisten digital cerdas yang mengelola pesan masuk untuk Arsad. Jawab dengan ramah, sopan, dan ringkas tanpa menyebutkan identitas AI.";

    try {
        if (!ai) throw new Error('GEMINI_API_KEY missing');
        if (!userSessions[senderID]) {
            userSessions[senderID] = ai.chats.create({
                model: 'gemini-3.8-flash',
                config: { systemInstruction: systemPrompt }
            });
        }
        return (await userSessions[senderID].sendMessage({ message: messageText })).text;
    } catch (err1) {
        try {
            if (!groq) throw new Error('GROQ missing');
            const res = await groq.chat.completions.create({ model: "llama-3.3-70b-versatile", messages: [{ role: "system", content: systemPrompt }, { role: "user", content: messageText }] });
            return res.choices[0].message.content;
        } catch (err2) {
            try {
                if (!openrouter) throw new Error('OPENROUTER missing');
                const res = await openrouter.chat.completions.create({ model: "deepseek/deepseek-chat", messages: [{ role: "system", content: systemPrompt }, { role: "user", content: messageText }] });
                return res.choices[0].message.content;
            } catch (err3) {
                return "Halo! Saat ini asisten utama sedang sibuk, pesanmu telah dicatat untuk disampaikan kepada Arsad.";
            }
        }
    }
}

async function connectToWhatsApp() {
    const { state, saveCreds } = await useMultiFileAuthState('auth_info_baileys');

    const sock = makeWASocket({
        auth: state,
        logger: pino({ level: 'silent' })
    });

    sock.ev.on('connection.update', (update) => {
        const { connection, lastDisconnect, qr } = update;
        if (qr) qrcode.generate(qr, { small: true });

        if (connection === 'close') {
            const shouldReconnect = (lastDisconnect?.error)?.output?.statusCode !== DisconnectReason.loggedOut;
            if (shouldReconnect) connectToWhatsApp();
        } else if (connection === 'open') {
            console.log('Tahniah! Bot WhatsApp dengan Cooldown 30 Menit Dinamis berhasil diaktifkan.');
        }
    });

    sock.ev.on('creds.update', saveCreds);

    sock.ev.on('messages.upsert', async ({ messages, type }) => {
        if (type !== 'notify') return;
        
        const msg = messages[0];
        if (!msg.message) return;

        const senderID = msg.key.remoteJid;
        if (senderID.endsWith('@g.us')) return; // Abaikan Grup

        // ANTI-BACKLOG: Abaikan pesan basi (lebih dari 60 detik)
        if (Math.floor(Date.now() / 1000) - msg.messageTimestamp > 60) return;

        const now = Date.now();
        const COOLDOWN_DURATION = 30 * 60 * 1000; // 30 Menit

        // Inisialisasi status untuk nomor baru
        if (!chatStates[senderID]) {
            chatStates[senderID] = { lastActivity: 0, isMuted: false, lastAITime: 0, botReplyCount: 0 };
        }
        const state = chatStates[senderID];

        // 1. KONTROL ARSAD (Pemicu Mute 30 Menit)
        if (msg.key.fromMe) {
            state.isMuted = true;
            state.lastActivity = now;
            console.log(`💬 Arsad membalas. AI masuk mode diam 30 Menit untuk ${senderID}.`);
            return;
        }

        // 2. CEK STATUS MUTE (COOLDOWN)
        if (state.isMuted) {
            const timeSinceLastActivity = now - state.lastActivity;
            if (timeSinceLastActivity < COOLDOWN_DURATION) {
                // Obrolan masih berlanjut, RESET timer mundur kembali ke 30 menit penuh
                state.lastActivity = now;
                console.log(`⏳ Pesan baru dari ${senderID}. Cooldown di-reset kembali menjadi 30 menit.`);
                return;
            } else {
                // Sudah hening total selama 30 menit, AI bangkit kembali
                console.log(`✅ Hening 30 menit tercapai. AI kembali aktif untuk ${senderID}.`);
                state.isMuted = false;
            }
        }

        // 3. DETEKSI BOT BCA Lanjutan (Toleransi ditingkatkan ke 8 Detik)
        if (state.lastAITime > 0) {
            const timeSinceAIReply = now - state.lastAITime;
            if (timeSinceAIReply < 8000) {
                // Jika terdeteksi balasan di bawah 8 detik sebanyak 2x beruntun, anggap itu bot BCA
                state.botReplyCount += 1;
                if (state.botReplyCount >= 2) {
                    state.isMuted = true;
                    state.lastActivity = now;
                    console.log(`🤖 Terdeteksi bot/mesin pada ${senderID}, AI dimatikan secara paksa selama 30 menit.`);
                    return;
                }
            } else {
                // Balasan normal manusia, reset hitungan
                state.botReplyCount = 0;
            }
        }

        const messageText = msg.message.conversation || msg.message.extendedTextMessage?.text;
        if (!messageText) return;

        console.log(`Mesej peribadi diterima daripada ${senderID}: ${messageText}`);

        // 4. AI MEMBALAS & MENCATAT WAKTU
        const botReply = await generateAIResponse(senderID, messageText);
        await sock.sendMessage(senderID, { text: botReply });
        
        // Catat kapan AI terakhir membalas untuk melacak bot lain di obrolan selanjutnya
        state.lastAITime = Date.now();
    });
}

connectToWhatsApp();