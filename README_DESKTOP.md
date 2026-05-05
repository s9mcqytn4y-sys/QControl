# QControl Desktop - Fase 2E-B: Matrix Defect x Slot Waktu Dinamis

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

## Cara Uji Matrix Input Harian (Fase 2E-B)
1. Login sebagai HeadQC.
2. Pastikan Master Data sudah ditarik dari server (termasuk Slot Waktu dan Relasi Part-Defect).
3. Buka menu **Input Harian**.
4. Pilih Line dan Part yang memiliki template defect.
5. Gunakan matrix pada panel tengah untuk mengisi jumlah defect berdasarkan jam kejadian.
6. **Validasi QC**: Pastikan Total Defect tidak melebihi Total Check. Jika melebihi, akan muncul peringatan dan data tidak tersimpan.
7. **Offline Friendly**: Semua input disimpan ke draft lokal SQLite secara real-time.

## Catatan Penting
- **Role Lock**: Hanya role `HeadQC` yang dapat login.
- **Master Data Cache**: Semua data master bersifat read-only dari PGNServer dan disimpan di SQLite lokal untuk performa offline.
- **Defect Template**: Validasi template per part wajib sebelum masuk ke fase Input Harian.
- **Database**: Skema lokal telah ditingkatkan untuk mendukung `kodeTampilanDefect`.
