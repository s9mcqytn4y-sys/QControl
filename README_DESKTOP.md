# QControl Desktop - Fase 2C-R3: Hardening Auth Client HeadQC

Dokumen ini menjelaskan struktur dan cara kerja aplikasi QControl versi Desktop.

## Teknologi Utama
- **Kotlin 2.1.0** (JVM 17)
- **Compose Multiplatform 1.7.3**
- **Coroutines & Flow** untuk State Management
- **Material 3** untuk Design System

## Cara Menjalankan
Pastikan JDK 17 terinstal, lalu jalankan perintah di root project:

```bash
./gradlew :desktop:run
```

## Kebijakan Autentikasi (Hanya HeadQC)
Sesuai kontrak PGNServer Fase 2C, aplikasi ini hanya mengizinkan akses untuk peran **HeadQC**.
- **Email Default**: `headqc@pgn.local`
- **Password Default**: `HeadQC@12345` (untuk lingkungan development)
- **Akses**: Penuh end-to-end.
- **Server Default**: `http://127.0.0.1:8000`

## Cara Uji Autentikasi (Fase 2C-R2)
1. Pastikan PGNServer aktif (menjalankan seeder `UserHeadQCSeeder`).
2. Masukkan email `headqc@pgn.local` dan password `HeadQC@12345` pada halaman Login.
3. Jika berhasil, token Sanctum akan disimpan di database lokal (`sesi_autentikasi`).
4. Sesi akan bertahan meskipun aplikasi ditutup (Persistent Session).

## Cara Uji Validasi Runtime (Fase 2D-R2-R1)
1. Login dengan kredensial HeadQC (seperti langkah di atas).
2. Buka menu Master Data.
3. Klik tombol "Tarik Master Data dari Server" (membutuhkan koneksi PGNServer).
4. Pastikan ringkasan data tampil.
5. Buka tab Part, coba fitur pencarian part.
6. Tutup aplikasi.
7. Buka ulang aplikasi, masuk ke Master Data.
8. Klik "Muat Data Lokal".
9. Pastikan data tetap utuh tanpa mengambil dari server (mode offline).

## Catatan Penting
- **Role Lock**: Hanya role `HeadQC` yang dapat login dan disimpan sesi lokalnya.
- **Persistent Token**: Token dikirim di setiap request sinkronisasi melalui header `Authorization: Bearer`.
- **Database**: Skema lokal telah ditingkatkan ke Versi 3 untuk mendukung kolom `email`.
