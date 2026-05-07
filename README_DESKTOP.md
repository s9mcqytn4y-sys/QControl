# QControl Desktop - Fase 2G-A: Pematangan Input Harian QC

Aplikasi desktop Windows untuk Quality Control (QC) manufaktur, dioptimalkan untuk peran HeadQC dengan dukungan offline-first dan tampilan premium.

## Status Fase Saat Ini
- **Fase 2G-A**: Pematangan Input Harian (Digital Checksheet), Switch Line dinamis, validasi ketat, dan visual polish.

## Fitur Utama
- **Digital Checksheet**: Workflow input harian yang matang dengan pemilihan Line dan Part otomatis.
- **Switch Line Dinamis**: Mendukung perpindahan line produksi dengan pemuatan draft dan daftar part yang sesuai secara instan.
- **Premium Dark Theme**: Antarmuka berbasis Material 3 dengan estetika manufacturing control system yang elegan.
- **Offline-First**: Bekerja penuh tanpa internet menggunakan cache SQLite lokal.
- **Client Validation**: Validasi input ketat (Total Defect vs Total Check) untuk memastikan integritas data.
- **Rules & Prosedur**: Panduan langkah demi langkah penggunaan Digital Checksheet langsung di UI.

## Alur Kerja Operasional (HeadQC)
1. **Login**: Gunakan kredensial HeadQC (`headqc@pgn.local`).
2. **Koneksi Server**: Periksa status PGNServer pada Header (Chip Status).
3. **Master Data**: Tarik data referensi terbaru dari server untuk penggunaan offline.
4. **Input Harian (Digital Checksheet)**: 
   - Pilih Tanggal dan Line Produksi.
   - Pilih Part dari daftar yang otomatis muncul sesuai line.
   - Isi QTY CHECK dan Matrix Defect.
   - Pastikan Ringkasan Harian valid.
5. **Draft Lokal & Sinkronisasi**: Data tersimpan otomatis di SQLite dan siap dikirim ke PGNServer saat online.

## Verifikasi Mandiri
Jalankan perintah berikut untuk memastikan aplikasi siap build:
```bash
./gradlew :desktop:compileKotlin --console=plain
./gradlew :desktop:assemble --console=plain
```

## Kebijakan Data
- **Source of Truth**: PGNServer adalah sumber data master final.
- **Draft Lokal**: Input harian disimpan sebagai draft di SQLite sebelum dikirim ke server.
