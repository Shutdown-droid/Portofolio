import cv2

# 1. Deklarasikan file referensi cascade
face_ref = cv2.CascadeClassifier('face_ref.xml')

# Tambahkan cv2.CAP_DSHOW agar kamera Windows merespon lebih cepat
camera = cv2.VideoCapture(0, cv2.CAP_DSHOW)

# Fungsi untuk mendeteksi wajah
def face_detection(frame):
    # Optimasi: Ubah frame menjadi hitam putih (grayscale) agar lebih ringan diproses
    optimized_frame = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    
    # Deteksi wajah menggunakan file referensi (face_ref.xml)
    # minSize=(100, 100) diubah agar deteksi lebih adaptif
    faces = face_ref.detectMultiScale(optimized_frame, scaleFactor=1.1, minNeighbors=5, minSize=(100, 100))
    return faces

# Fungsi untuk menggambar kotak di sekitar wajah
def drawer_box(frame):
    # Panggil fungsi face_detection untuk mendapatkan koordinat wajah
    faces = face_detection(frame)
    
    # x, y = koordinat sudut kiri atas
    # w, h = lebar (width) dan tinggi (height) wajah
    for (x, y, w, h) in faces:
        # cv2.rectangle (frame, point1, point2, color_BGR, thickness)
        # Warna Biru (B=255, G=0, R=0), ketebalan 4
        cv2.rectangle(frame, (x, y), (x + w, y + h), (255, 0, 0), 4)

# Fungsi utama untuk menjalankan program
def main():
    print("Membuka kamera... Tekan tombol 'q' untuk keluar.")
    while True:
        # Baca frame per frame dari kamera
        ret, frame = camera.read()
        
        # Validasi jika kamera gagal merespon
        if not ret:
            print("Gagal membaca kamera.")
            break
            
        # 🌟 EFEK CERMIN: Membalikkan gambar secara horizontal agar tidak mirror
        frame = cv2.flip(frame, 1)
        
        # Panggil fungsi untuk menggambar kotak deteksi
        drawer_box(frame)
        
        # Tampilkan frame ke jendela (window)
        cv2.imshow('Project 9: AI Deteksi Wajah', frame)
        
        # Kondisi untuk keluar: Tekan tombol 'q'
        if cv2.waitKey(1) & 0xFF == ord('q'):
            close_window()
            break

# Fungsi untuk menutup dan membersihkan memori
def close_window():
    camera.release()
    cv2.destroyAllWindows()
    exit()

# Menjalankan fungsi utama
if __name__ == '__main__':
    main()