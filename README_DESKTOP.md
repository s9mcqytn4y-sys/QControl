# QControl Desktop - Fase 2E-C: Premium UI/UX Hardening

Aplikasi desktop Windows untuk Quality Control (QC) manufaktur, dioptimalkan untuk peran HeadQC dengan dukungan offline-first dan tampilan premium.

## Status Fase Saat Ini
- **Fase 2E-C**: Pemolesan UI/UX Premium, Design System Hardening, Client Validation, dan Sistem Flash Message.

## Fitur Utama
- **Premium Dark Theme**: Antarmuka berbasis Material 3 dengan estetika manufacturing control system yang elegan.
- **Offline-First**: Bekerja penuh tanpa internet menggunakan cache SQLite lokal.
- **Client Validation**: Validasi input login dan data pemeriksaan langsung di sisi klien.
- **Flash Message**: Sistem notifikasi internal (Snackbar) untuk umpan balik operasi yang konsisten.
- **Branding Resmi**: Logo dan ikon aplikasi disesuaikan dengan identitas QControl.

## Alur Kerja Operasional (HeadQC)
1. **Login**: Gunakan kredensial HeadQC (`headqc@pgn.local`). Validasi format email dan panjang password dilakukan otomatis.
2. **Koneksi Server**: Periksa status PGNServer pada Header (Chip Status).
3. **Master Data**: Tarik data referensi terbaru dari server untuk penggunaan offline.
4. **Input Harian**: Catat temuan defect berdasarkan matrix part x slot waktu dengan validasi Total Defect <= Total Check.
5. **Draft Lokal**: Data tersimpan otomatis di perangkat dan siap disinkronkan.

## Verifikasi Mandiri
Jalankan perintah berikut untuk memastikan aplikasi siap build:
```bash
./gradlew :desktop:compileKotlin --console=plain
./gradlew :desktop:assemble --console=plain
```

## Kebijakan Data
- **Source of Truth**: PGNServer adalah sumber data master final.
- **Draft Lokal**: Input harian disimpan sebagai draft di SQLite sebelum dikirim ke server.
