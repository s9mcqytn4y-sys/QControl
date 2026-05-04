# Roadmap QControl Desktop

Dokumen ini merangkum rencana pengembangan QControl sebagai aplikasi Desktop (Windows) berbasis Compose Desktop.

## Fase 1: Fondasi & Arsitektur (Selesai)
- [x] Inisialisasi Proyek Compose Desktop.
- [x] Implementasi SQLite Lokal (JDBC) & Migrasi Database.
- [x] Ktor Client untuk integrasi backend PGNServer.
- [x] Sistem Outbox Sinkronisasi (Offline-First).
- [x] UI Pengaturan & Kontrol Sinkronisasi.
- [x] Reset Outbox Stuck (Self-Recovery).

## Fase 2: Integrasi Backend & Kontrak Data (Sedang Berjalan)
- [x] Fase 2A: Implementasi kontrak backend `qcontrol/contoh`.
- [x] Fase 2A-R2: Verifikasi end-to-end QControl ke PGNServer (Sync manual).
- [x] Fase 2B: Idempotency persistence server-side di PGNServer & Client.
- [/] Fase 2C: Autentikasi (Sanctum) pada Desktop Client.
- [ ] Fase 2D: Definisi Master Data minimal (Line, Shift, Jenis Defect).
- [ ] Fase 2E: Mekanisme Pull Master Data ke Desktop.
- [ ] Fase 2F: Caching Master Data lokal.

## Fase 3: Fitur Bisnis Utama
- [ ] Implementasi Dashboard Utama (Rasio NG, Total Inspeksi).
- [ ] Modul Inspeksi Harian (Skeleton Input).
- [ ] Entri Data Defect & Perhitungan Rasio Otomatis.
- [ ] Sinkronisasi Real-time untuk Data Inspeksi.

## Fase 4: Analitik & Pelaporan
- [ ] Export Laporan ke Excel/PDF.
- [ ] Grafik Tren Defect Mingguan/Bulanan.
- [ ] Print Label/Barcode.

## Fase 5: Polishing & Deployment
- [ ] Optimasi Performa UI & Database.
- [ ] Build MSI/EXE via jpackage/Conveyor.
- [ ] Auto-update mechanism.

---
*Status: Aktif dikerjakan oleh Solo Developer.*
