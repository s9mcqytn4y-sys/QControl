# Roadmap QControl Desktop

Dokumen ini merangkum rencana pengembangan QControl sebagai aplikasi Desktop (Windows) berbasis Compose Desktop, menggantikan rencana lama yang berbasis Android.

## Fase 1: Fondasi & Operasional (Sedang Berjalan)
- [x] Inisialisasi Proyek Compose Desktop.
- [x] Implementasi SQLite Lokal (JDBC) & Migrasi Database.
- [x] Ktor Client untuk integrasi backend PGNServer.
- [x] Sistem Outbox Sinkronisasi (Offline-First).
- [x] UI Pengaturan & Kontrol Sinkronisasi.
- [x] Reset Outbox Stuck (Self-Recovery).

## Fase 2: Fitur Bisnis Utama
- [ ] Implementasi Dashboard Utama (Rasio NG, Total Inspeksi).
- [ ] Modul Inspeksi Harian (Entri Data Defect).
- [ ] Integrasi Sinkronisasi Real-time untuk Data Inspeksi.
- [ ] Manajemen Pengguna & Autentikasi (JWT).

## Fase 3: Analitik & Pelaporan
- [ ] Export Laporan ke Excel/PDF.
- [ ] Grafik Tren Defect Mingguan/Bulanan.
- [ ] Print Label/Barcode (Jika diperlukan).

## Fase 4: Polishing & Deployment
- [ ] Optimasi Performa UI & Database.
- [ ] Build MSI/EXE via Conveyor atau jpackage.
- [ ] Auto-update mechanism.

---
*Status: Aktif dikerjakan oleh Solo Developer.*
