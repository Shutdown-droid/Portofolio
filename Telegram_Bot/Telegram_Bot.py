import os
import telebot
from dotenv import load_dotenv
from google import genai
from google.genai import types

# 1. Keamanan Kredensial (Standar Cybersecurity)
# Membaca token dari file .env agar tidak bocor jika di-push ke GitHub/GitLab
load_dotenv(os.path.join(os.path.dirname(__file__), "Key.env"))
TELEGRAM_TOKEN = os.getenv("TELEGRAM_TOKEN")
GEMINI_API_KEY = os.getenv("GEMINI_API_KEY")

if not TELEGRAM_TOKEN or not GEMINI_API_KEY:
    raise ValueError("Key.env must contain TELEGRAM_TOKEN and GEMINI_API_KEY.")

# 2. Inisialisasi Bot & AI
bot = telebot.TeleBot(TELEGRAM_TOKEN)
client = genai.Client(api_key=GEMINI_API_KEY)

# 3. Manajemen Memori Percakapan (State Management)
user_sessions = {}

def get_user_chat(user_id):
    if user_id not in user_sessions:
        # Membuat sesi chat baru dengan "Otak" Sekretaris Pribadi
        user_sessions[user_id] = client.chats.create(
            model="gemini-3.8-flash",
            config=types.GenerateContentConfig(
                system_instruction=(
                    "Kamu adalah asisten digital cerdas yang bertugas mengelola pesan masuk untuk Arsad. "
                    "Setiap kali ada pengguna yang menyapa, kamu WAJIB membalas dengan kalimat: "
                    "'Halo! Saya asisten digital yang membantu Arsad untuk menjawab pesan. Apa yang bisa saya bantu? Jika kamu ingin memberi pesan kepada Arsad, silakan tinggalkan pesan dan saya akan memberitahukannya.' "
                    "Setelah perkenalan itu, jika mereka bertanya hal lain, jawablah dengan cerdas dan ringkas. "
                    "Jika mereka menitipkan pesan untuk Arsad, katakan bahwa pesan telah dicatat dengan baik."
                )
            )
        )
    return user_sessions[user_id]

# 4. Handler Perintah Dasar
@bot.message_handler(commands=['start', 'help'])
def send_welcome(message):
    teks_sapaan = (
        "Halo! Saya adalah asisten AI buatan Arsad.\n"
        "Saya bisa mengingat percakapan kita. Ingin membahas jaringan, koding, atau hal lain?"
    )
    bot.reply_to(message, teks_sapaan)

# 5. Handler Percakapan Utama dengan Memori
@bot.message_handler(func=lambda message: True)
def handle_message(message):
    bot.send_chat_action(message.chat.id, 'typing')
    
    try:
        user_id = message.chat.id
        chat_session = get_user_chat(user_id)
        
        # Mengirim pesan ke sesi yang sudah memiliki riwayat sebelumnya
        response = chat_session.send_message(message.text)
        bot.reply_to(message, response.text, parse_mode="Markdown")

    except Exception as e:
        bot.reply_to(message, f"Sistem sedang mengalami gangguan. Error: {e}")

print("Bot pintar dengan memori dan keamanan .env sedang berjalan...")
bot.infinity_polling()