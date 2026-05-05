# QControl - Instruksi Agent (HeadQC)

## Identitas & Peran
- **Repo**: QControl (Desktop Windows App, BUKAN Android).
- **Role**: HeadQC (Solo Developer).
- **Bahasa**: Bahasa Indonesia penuh untuk kode, log, komentar, dan variabel.
- **Branch**: main.

## Tech Stack
- **Core**: Kotlin 2.x, JDK 17.
- **UI**: Compose for Desktop (JetBrains).
- **Networking**: Ktor Client.
- **Database**: SQLite JDBC (Cache Lokal).
- **Concurrency**: Coroutines & StateFlow (MVI Pattern).
- **Backend**: PGNServer (http://127.0.0.1:8000).

## Arsitektur (Clean Architecture)
- `core/`: Tema, DI, Network.
- `data/`: repository_impl, remote, local (SQLite).
- `domain/`: model, repository_interface, usecase.
- `presentation/`: ui screens, viewmodels, contracts (MVI).

## Batasan & Larangan
- **Dilarang** menggunakan library Android (SDK, Room Android, dll).
- **Dilarang** membuat role selain HeadQC.
- **Dilarang** melakukan CRUD pada Master Data (hanya Read-only cache).
- **Dilarang** input transaksi harian sebelum validasi template defect per part selesai.

## Fase Aktif: 2D-R3
- Fokus: Cache dan tampilkan `kodeTampilanDefect`.
- Validasi template defect per part.

## Perintah Verifikasi
```bash
./gradlew :desktop:compileKotlin --console=plain
./gradlew :desktop:assemble --console=plain
```

## Format Patch Report Wajib
PATCH REPORT - QControl - AgentOps

1. Ringkasan keputusan teknis: [Isi]
2. File dibuat: [Isi]
3. File diubah: [Isi]
4. Isi ringkas setiap file agent: [Isi]
5. Command yang dijalankan: [Isi]
6. Hasil verifikasi: [Isi]
7. Risiko tersisa: [Isi]
8. Rekomendasi fase berikutnya: [Isi]
