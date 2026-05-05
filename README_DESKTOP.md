# QControl Desktop - Fase 2E-B-R3: UI/UX Polish QA/QC

Aplikasi desktop Windows untuk Quality Control (QC) manufaktur, dioptimalkan untuk peran HeadQC dengan dukungan offline-first.

## Status Fase Saat Ini
- **Fase 2E-B-R3**: Pemolesan UI/UX menyeluruh, standarisasi copywriting Bahasa Indonesia, dan klarifikasi status Local/Offline/Backend.

## Teknologi Utama
- **Kotlin 2.1.0** (JVM 17)
- **Compose Desktop 1.7.3** (Material 3)
- **Ktor Client** untuk komunikasi PGNServer
- **SQLite JDBC** untuk penyimpanan lokal (Offline-first)

## Alur Kerja Operasional (HeadQC)
1. **Login**: Gunakan kredensial HeadQC (`headqc@pgn.local`).
2. **Koneksi Server**: Periksa status PGNServer pada Header (Chip Status).
3. **Master Data**: Tarik data referensi terbaru dari server untuk penggunaan offline.
4. **Input Harian**: Catat temuan defect berdasarkan matrix part x slot waktu.
5. **Draft Lokal**: Data tersimpan otomatis di perangkat dan siap disinkronkan.

## Verifikasi Mandiri
Jalankan perintah berikut untuk memastikan aplikasi siap build:
```bash
./gradlew :desktop:compileKotlin --console=plain
./gradlew :desktop:assemble --console=plain
```

## Kebijakan Data
- **Offline-first**: Aplikasi tetap berfungsi tanpa koneksi server menggunakan cache lokal.
- **Source of Truth**: PGNServer adalah sumber data master final.
- **Draft Lokal**: Input harian disimpan sebagai draft di SQLite sebelum dikirim ke server.
