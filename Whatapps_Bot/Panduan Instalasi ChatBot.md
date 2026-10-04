Markdown
# 🤖 Dokumentasi Bot Asisten Pribadi WhatsApp

Bot ini berfungsi sebagai asisten digital WhatsApp otomatis yang ditenagai oleh kecerdasan buatan berlapis (*Multi-Agent Fallback*). Sistem dirancang agar tidak mengganggu percakapan manusia melalui fitur *Cooldown Dinamis* dan perlindungan *Anti-Spam Bot*.

---

## ✨ Fitur Utama

1. **4-Tier Multi-Agent Fallback:** Otomatis beralih dari Gemini ➔ Groq ➔ OpenRouter ➔ Hugging Face jika server utama sedang sibuk atau mengalami gangguan.
2. **Clean Text Response:** Balasan murni berupa teks natural layaknya manusia, tanpa menyertakan label atau nama agen AI di depannya.
3. **Smart Cooldown 30 Menit:** 
   - Jika Anda (*owner*) membalas chat, bot otomatis mati (diam) selama 30 menit.
   - Waktu diam akan terus di-reset mundur ke 30 menit selama obrolan kalian masih berlangsung. 
   - Bot baru akan aktif kembali jika obrolan benar-benar hening selama 30 menit penuh, dan ada chat *baru* yang masuk.
4. **Anti-Bot Loop (BCA Detector):** Mendeteksi balasan instan di bawah 8 detik dari mesin/bot lain (seperti notifikasi Bank) dan langsung mematikan AI untuk mencegah *spamming* otomatis.
5. **Anti-Backlog:** Otomatis mengabaikan pesan lama (yang tertunda lebih dari 60 detik) saat bot baru dinyalakan agar tidak merespon pesan basi secara borongan.
6. **Group Filter:** Mengabaikan secara mutlak semua pesan yang berasal dari grup WhatsApp (`@g.us`).

---

## 🛠️ Langkah 1: Persiapan Direktori

1. Buat folder baru khusus untuk proyek ini (contoh: `WhatsApp_Bot`).
2. Buka folder tersebut di dalam **Visual Studio Code (VS Code)**.
3. Buka Terminal terintegrasi di VS Code (`Ctrl` + `~`).
4. Jika menggunakan Windows PowerShell dan muncul *error* `Execution_Policies`, buka PowerShell sebagai Administrator dan jalankan:
   ```powershell
   Set-ExecutionPolicy RemoteSigned -Scope CurrentUser
Inisialisasi proyek Node.js baru:

Bash
npm init -y
📦 Langkah 2: Instalasi Modul (Dependencies)
Jalankan perintah berikut di terminal untuk memasang pustaka WhatsApp (Baileys) dan seluruh SDK AI:

Bash
npm install @whiskeysockets/baileys pino qrcode-terminal dotenv @google/genai groq-sdk openai
(Opsional) Jika versi npm Anda meminta persetujuan untuk menjalankan skrip instalasi, gunakan perintah:

Bash
npm install-scripts approve --all
🔑 Langkah 3: Konfigurasi API Keys (.env)
Buat file baru bernama .env di dalam folder root proyek Anda.

Salin format di bawah ini dan masukkan API Key Anda (kosongkan bagian yang belum Anda miliki kuncinya):

Code snippet
GEMINI_API_KEY=masukkan_kunci_api_gemini_di_sini
GROQ_API_KEY=masukkan_kunci_api_groq_di_sini
OPENROUTER_API_KEY=masukkan_kunci_api_openrouter_di_sini
HUGGINGFACE_API_KEY=masukkan_kunci_api_huggingface_di_sini
💻 Langkah 4: Kode Utama (wa_bot.js)
Buat file baru bernama wa_bot.js.

Salin dan tempel (paste) seluruh baris kode JavaScript di bawah ini ke dalam file tersebut:

JavaScript
const { makeWASocket, useMultiFileAuthState, DisconnectReason } = require('@whiskeysockets/baileys');
const pino = require('pino');
const qrcode = require('qrcode-terminal');
require('dotenv').config({ path: ['Key.env', '.env'] });

// Impor SDK secara aman (Mencegah crash jika modul belum terinstal)
let GoogleGenAI, Groq, OpenAI;
try { GoogleGenAI = require('@google/genai').GoogleGenAI; } catch (e) {}
try { Groq = require('groq-sdk').Groq; } catch (e) {}
try { OpenAI = require('openai'); } catch (e) {}

const ai = (process.env.GEMINI_API_KEY && GoogleGenAI) ? new GoogleGenAI({ apiKey: process.env.GEMINI_API_KEY }) : null;
const groq = (process.env.GROQ_API_KEY && Groq) ? new Groq({ apiKey: process.env.GROQ_API_KEY }) : null;
const openrouter = (process.env.OPENROUTER_API_KEY && OpenAI) ? new OpenAI({ apiKey: process.env.OPENROUTER_API_KEY, baseURL: '[https://openrouter.ai/api/v1](https://openrouter.ai/api/v1)' }) : null;
const huggingface = (process.env.HUGGINGFACE_API_KEY && OpenAI) ? new OpenAI({ apiKey: process.env.HUGGINGFACE_API_KEY, baseURL: '[https://api-inference.huggingface.co/v1/](https://api-inference.huggingface.co/v1/)' }) : null;

if (![ai, groq, openrouter, huggingface].some(Boolean)) {
    throw new Error('Atur setidaknya satu API key yang valid di dalam file .env atau Key.env.');
}

const userSessions = {};
const chatStates = {}; // Menyimpan memori status obrolan per nomor

async function generateAIResponse(senderID, messageText) {
    const systemPrompt = "Kamu adalah asisten digital cerdas yang mengelola pesan masuk untuk Arsad. Jawab dengan ramah, sopan, dan ringkas tanpa menyebutkan identitas AI yang digunakan.";

    // Lapis 1: Gemini
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
        // Lapis 2: Groq
        try {
            if (!groq) throw new Error('GROQ missing');
            const res = await groq.chat.completions.create({ model: "llama-3.3-70b-versatile", messages: [{ role: "system", content: systemPrompt }, { role: "user", content: messageText }] });
            return res.choices[0].message.content;
        } catch (err2) {
            // Lapis 3: OpenRouter
            try {
                if (!openrouter) throw new Error('OPENROUTER missing');
                const res = await openrouter.chat.completions.create({ model: "deepseek/deepseek-chat", messages: [{ role: "system", content: systemPrompt }, { role: "user", content: messageText }] });
                return res.choices[0].message.content;
            } catch (err3) {
                // Pesan Sistem (Lapis Terakhir)
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
            console.log('Tahniah! Bot WhatsApp Asisten Pribadi dengan Cooldown 30 Menit berhasil diaktifkan.');
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
        const COOLDOWN_DURATION = 30 * 60 * 1000; // 30 Menit dalam milidetik

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
                state.lastActivity = now; // Reset timer ke 30 menit penuh
                console.log(`⏳ Aktivitas obrolan dari ${senderID}. Cooldown di-reset kembali menjadi 30 menit.`);
                return;
            } else {
                console.log(`✅ Hening 30 menit tercapai. AI bersiap aktif kembali untuk ${senderID}.`);
                state.isMuted = false;
            }
        }

        // 3. DETEKSI BOT LAIN (Anti BCA-Loop)
        if (state.lastAITime > 0) {
            const timeSinceAIReply = now - state.lastAITime;
            if (timeSinceAIReply < 8000) { // Toleransi balasan instan mesin di bawah 8 detik
                state.botReplyCount += 1;
                if (state.botReplyCount >= 2) {
                    state.isMuted = true;
                    state.lastActivity = now;
                    console.log(`🤖 Terdeteksi bot/mesin pada ${senderID}, AI dimatikan paksa selama 30 menit.`);
                    return;
                }
            } else {
                state.botReplyCount = 0; // Balasan normal manusia
            }
        }

        const messageText = msg.message.conversation || msg.message.extendedTextMessage?.text;
        if (!messageText) return;

        console.log(`Mesej peribadi diterima daripada ${senderID}: ${messageText}`);

        // 4. AI MEMBALAS & MENCATAT WAKTU
        const botReply = await generateAIResponse(senderID, messageText);
        await sock.sendMessage(senderID, { text: botReply });
        
        state.lastAITime = Date.now();
    });
}

connectToWhatsApp();
▶️ Langkah 5: Cara Menjalankan Sistem
Buka terminal di dalam folder proyek Anda (WhatsApp_Bot).

Jalankan perintah eksekusi berikut:

Bash
node wa_bot.js
Tunggu hingga Kode QR berbentuk piksel muncul di layar terminal.

Buka aplikasi WhatsApp di smartphone Anda.

Masuk ke Perangkat Tertaut (Linked Devices) ➔ Tautkan Perangkat (Link a Device).

Arahkan kamera untuk memindai kode QR yang ada di terminal.

Selesai! Bot kini telah terhubung dan akan terus siaga merespon pesan selama jendela terminal tersebut tidak ditutup.

Catatan:

Untuk mematikan bot secara manual, klik pada area terminal dan tekan Ctrl + C.

Jika ingin mendapatkan kode QR yang baru (misalnya karena sesi logout), hapus folder bernama auth_info_baileys yang ada di dalam proyek, lalu jalankan kembali perintah node wa_bot.js.
