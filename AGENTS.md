# AGENTS - QControl

Instruksi terpusat untuk agen AI agar memahami konteks proyek QControl secara ringkas dan hemat token. File ini adalah **Source of Truth** untuk semua instruksi agent.

## 1. Identitas Repo
- **Nama**: QControl
- **Tipe**: Desktop Windows App (**BUKAN Android**).
- **Tujuan**: Aplikasi Quality Control untuk inspeksi Part dan Defect.
- **Bahasa Proyek**: **Bahasa Indonesia penuh** untuk penamaan file, fungsi, variabel, komentar, dan log (kecuali istilah teknis standar).

## 2. Tech Stack
- **Bahasa**: Kotlin 2.x, JDK 17.
- **UI**: Compose for Desktop (JetBrains).
- **Networking**: Ktor Client (REST API).
- **Database**: SQLite JDBC (Cache Lokal).
- **Concurrency**: Coroutines & StateFlow (UDF Pattern).
- **Backend**: PGNServer (http://127.0.0.1:8000).

## 3. Arsitektur Proyek
Aplikasi menggunakan struktur folder berikut:
- `inti/`: Hasil operasi, kesalahan global, dan validasi dasar.
- `konfigurasi/`: Pengaturan aplikasi, URL server, dan konstanta peran.
- `ranah/`: Domain model bisnis dan use case (Logic bisnis murni).
- `data/`: Implementasi repository, remote (Ktor), lokal (SQLite), dan sinkronisasi.
- `tampilan/`: UI screens (halaman), ViewModels (state holder), navigasi, dan komponen reusable.
- `tema/`: Design system (warna, tipografi, ukuran, MaterialTheme).
- `utilitas/`: Helper format angka, tanggal, dan fungsi pembantu lainnya.

## 4. Aturan Role & Auth
- **Role Tunggal**: **HeadQC**.
- **Larangan**: Jangan membuat role baru (Admin, Inspector, Viewer, dll).
- **Auth**: HeadQC login via PGNServer untuk mendapatkan token Sanctum yang disimpan di SQLite lokal.

## 5. Batasan & Kebijakan
- **Dilarang Keras**: Menggunakan dependency Android (SDK, Room Android, dll).
- **Master Data**: Bersifat **Read-only** (hanya tarik dari PGNServer). Dilarang melakukan CRUD Master Data di aplikasi ini.
- **Transaksi**: Input Harian dilarang dilakukan sebelum template defect per part tervalidasi.
- **Sinkronisasi**: Gunakan Outbox dengan X-Idempotency-Key. Client Draft ID wajib unik per input.
- **Fase Saat Ini**: **2G-A** (Pematangan Input Harian QC).

## 6. Command Verifikasi
```bash
./gradlew :desktop:compileKotlin --console=plain
./gradlew :desktop:assemble --console=plain
```

## 7. Format Patch Report Wajib
Setiap perubahan wajib dilaporkan dengan format:
```text
PATCH REPORT - QControl - AgentOps-R1

1. Ringkasan keputusan teknis
2. File dibuat
3. File diubah
4. Koreksi penting yang dilakukan
5. Command yang dijalankan
6. Hasil verifikasi
7. Risiko tersisa
8. Rekomendasi fase berikutnya
```
