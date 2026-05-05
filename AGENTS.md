# QControl - Instruksi Agent (HeadQC)

## Identitas & Peran
- **Repo**: QControl (Desktop Windows App, BUKAN Android).
- **Role**: HeadQC (Solo Developer).
- **Bahasa**: Bahasa Indonesia penuh (Kode, File, Fungsi, Variabel, Komentar, Log, UI).
- **Branch**: main.

## Tech Stack
- **Core**: Kotlin 2.x, JDK 17.
- **UI**: Compose for Desktop (JetBrains).
- **Networking**: Ktor Client.
- **Database**: SQLite JDBC (Cache Lokal).
- **Concurrency**: Coroutines & StateFlow.
- **Backend**: PGNServer (http://127.0.0.1:8000) - Source of Truth Master Data.

## Arsitektur (Clean Architecture)
- `inti/`: Hasil operasi, kesalahan global, validasi dasar.
- `konfigurasi/`: Pengaturan aplikasi, URL server, konstanta peran.
- `ranah/`: Domain model bisnis dan use case.
- `data/`: Implementasi repository, remote (Ktor), lokal (SQLite), sinkronisasi.
- `tampilan/`: UI screens (halaman), viewmodels (state holder), navigasi, komponen reusable.
- `tema/`: Design system (warna, tipografi, ukuran, MaterialTheme).
- `utilitas/`: Helper format angka, tanggal, dsb.

## Batasan & Larangan
- **Dilarang** menggunakan library Android (SDK Android, Room Android, dll).
- **Dilarang** membuat role selain HeadQC.
- **Dilarang** melakukan CRUD pada Master Data (hanya Read-only cache).
- **Dilarang** input transaksi harian sebelum validasi template defect per part selesai.

## Fase Aktif: 2D-R3
- **Fokus**: Cache dan tampilkan `kodeTampilanDefect`.
- **Validasi**: Pastikan item defect muncul sesuai part yang dipilih (Defect Template).

## Perintah Verifikasi
```bash
./gradlew :desktop:compileKotlin --console=plain
./gradlew :desktop:assemble --console=plain
```

## Format Patch Report Wajib (Bahasa Indonesia)
PATCH REPORT - QControl - [KODE_TUGAS]

1. Ringkasan keputusan teknis: [Isi]
2. File dibuat: [Isi]
3. File diubah: [Isi]
4. Koreksi penting yang dilakukan: [Isi]
5. Command yang dijalankan: [Isi]
6. Hasil verifikasi: [Isi]
7. Risiko tersisa: [Isi]
8. Rekomendasi fase berikutnya: [Isi]
