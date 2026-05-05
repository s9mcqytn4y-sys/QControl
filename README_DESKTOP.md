# QControl Desktop - Fase 2D-R3: Master Data Cache & Defect Template Validasi

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

## Cara Uji Validasi Master Data & Template (Fase 2D-R3)
1. Login sebagai HeadQC.
2. Buka Master Data, lakukan penarikan data terbaru.
3. Pastikan `kodeTampilanDefect` tersimpan di cache SQLite lokal.
4. Pilih salah satu Part, validasi apakah template defect yang muncul sudah sesuai.
5. Mode Offline: Pastikan data tetap muncul dari cache lokal saat koneksi server diputus.

## Catatan Penting
- **Role Lock**: Hanya role `HeadQC` yang dapat login.
- **Master Data Cache**: Semua data master bersifat read-only dari PGNServer dan disimpan di SQLite lokal untuk performa offline.
- **Defect Template**: Validasi template per part wajib sebelum masuk ke fase Input Harian.
- **Database**: Skema lokal telah ditingkatkan untuk mendukung `kodeTampilanDefect`.
