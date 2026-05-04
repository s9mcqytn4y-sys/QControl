# QControl Desktop - Fase 2A-R2: Verifikasi Kontrak Outbox

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

Untuk membangun installer MSI (Windows):
```bash
./gradlew :desktop:packageMsi
```

## Struktur Folder Utama
- `inti/`: Logika dasar aplikasi (hasil, kesalahan, dsb).
- `ranah/`: Domain layer (model bisnis dan use case).
- `data/`: Data layer (repository, lokal, remote).
- `tampilan/`: Presentation layer (UI, state, navigasi).
- `tema/`: Design system (warna, tipografi, ukuran).
- `konfigurasi/`: Pengaturan global aplikasi.

## Integrasi PGNServer (Backend)
1. Jalankan PGNServer lokal (Laravel) melalui Docker Compose:
   `docker compose up -d`
2. Alamat Server: `http://127.0.0.1:8000`
3. Health Check:
   `GET http://127.0.0.1:8000/api/v1/kesehatan`
4. Endpoint Kontrak Outbox:
   `POST http://127.0.0.1:8000/api/v1/qcontrol/contoh`

## Status Sinkronisasi Otomatis
- **Default: OFF**. Sinkronisasi otomatis tidak berjalan saat aplikasi dibuka.
- User dapat mengaktifkan secara manual melalui halaman **Pengaturan**.
- User dapat memicu sinkronisasi manual kapan saja.

## Cara Uji End-to-End (Fase 2A-R2)
1. Pastikan PGNServer aktif.
2. Jalankan QControl Desktop.
3. Buka halaman **Pengaturan**.
4. Klik **Periksa Koneksi Server** (Pastikan indikator menjadi Hijau).
5. Klik **Buat Contoh Item Outbox**.
6. Klik **Sinkronkan Sekarang**.
7. Klik **Muat Ringkasan Outbox**.
8. Pastikan jumlah **BERHASIL** bertambah dan item contoh terkirim.

## Catatan Penting
- Endpoint `/api/v1/qcontrol/contoh` adalah kontrak awal untuk verifikasi sistem outbox.
- Belum ada fitur bisnis QC (Inspeksi, Defect, dll) di fase ini.
- Autentikasi (Sanctum) belum diaktifkan.
- Idempotency persistence sisi server masih dalam tahap pengembangan (Fase 2B).
