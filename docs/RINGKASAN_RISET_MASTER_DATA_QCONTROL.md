# Ringkasan Riset: Persiapan Master Data QControl

Dokumen ini berfungsi sebagai panduan transisi dari Fase 2C (Auth) ke Fase 2D (Master Data) untuk aplikasi QControl Desktop.

## Latar Belakang
Sebelum aplikasi dapat digunakan untuk mencatat inspeksi harian, aplikasi membutuhkan data master yang konsisten antara Desktop dan PGNServer. Data ini bersumber dari normalisasi file Excel yang selama ini digunakan secara manual.

## Struktur Master Data Minimal (Fase 2D)

### 1. Data Line (Lokasi Produksi)
- **Atribut**: ID, Nama Line (contoh: "Line 1", "Line 2"), Kode Area.
- **Kebutuhan UI**: Dropdown pilihan line di Dashboard/Form Inspeksi.

### 2. Data Shift
- **Atribut**: ID, Nama Shift (Shift 1, Shift 2, Shift 3), Jam Mulai, Jam Selesai.
- **Logika**: Mempengaruhi pengelompokan data laporan harian.

### 3. Jenis Defect (Master NG)
- **Atribut**: ID, Kode Defect, Nama Defect (contoh: "Gores", "Patah", "Warna Tidak Sesuai"), Kategori.
- **Normalisasi**: Harus unik dan seragam untuk menghindari redundansi data saat perhitungan Rasio NG.

### 4. Part Number / Model
- **Atribut**: ID, Kode Part, Nama Part.
- **Kebutuhan**: Validasi input harian agar data yang masuk selalu merujuk pada part yang valid.

## Rencana Implementasi (Fase 2E & 2F)

1. **Backend (PGNServer)**:
   - Membuat endpoint `GET /api/v1/master/semua` yang mengembalikan seluruh data master dalam satu payload (untuk efisiensi pertama kali tarik data).
   - Menambahkan kolom `updated_at` untuk mendukung sinkronisasi parsial di masa depan.

2. **Frontend (Desktop)**:
   - Membuat tabel SQLite lokal: `master_line`, `master_shift`, `master_defect`, `master_part`.
   - Implementasi `AmbilMasterDataUseCase` yang menarik data dari server dan menyimpannya (overwrite atau update) ke database lokal.
   - Data master bersifat **Read-Only** di sisi Desktop (semua manajemen data master dilakukan via Web Admin/PGNServer).

## Risiko & Mitigasi
- **Data Outdated**: Aplikasi harus melakukan pengecekan versi data master secara berkala atau saat login.
- **Inkonsistensi ID**: Gunakan UUID atau ID yang dihasilkan server (bukan auto-increment lokal) untuk kunci utama master data agar konsisten saat diunggah kembali ke server.

---
*Dokumen ini dibuat pada akhir Fase 2C-R3 sebagai persiapan pengembangan selanjutnya.*
