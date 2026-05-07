# PATCH REPORT - QControl/PGNServer - Fase 2G-C / 2H-A

## 1. Ringkasan Keputusan Teknis
- Implementasi "Hasil Produksi Tanpa NG" sebagai tabel terpisah di PGNServer untuk mendukung flow checksheet PRESS.
- Perubahan QTY CHECK menjadi input numerik langsung untuk mempercepat data entry.
- Optimalisasi state management di QControl agar ringkasan (summary) terupdate secara real-time saat QTY atau defect berubah.
- Re-organisasi header aplikasi untuk memindahkan indikator sesi dan server ke level global.

## 2. File Dibuat
- **PGNServer**:
    - `database/migrations/2026_05_07_000001_create_qcontrol_pemeriksaan_produksi_tanpa_ng_table.php`
    - `app/Models/QControlPemeriksaanProduksiTanpaNg.php`
    - `app/Http/Resources/Api/V1/QControl/PemeriksaanProduksiTanpaNgResource.php`
- **QControl**:
    - `ranah/model/ProduksiTanpaNg.kt`
    - `utilitas/FormatWaktu.kt`

## 3. File Diubah
- **PGNServer**:
    - `app/Models/QControlPemeriksaanHarian.php` (Relasi ke produksi tanpa NG)
    - `app/Http/Requests/Api/V1/QControl/SimpanPemeriksaanHarianRequest.php` (Validasi payload baru)
    - `app/Application/QControl/MenyimpanPemeriksaanHarian.php` (Logic penyimpanan data tanpa NG)
    - `app/Http/Resources/Api/V1/QControl/DetailPemeriksaanHarianResource.php` (Output data tanpa NG)
- **QControl**:
    - `tampilan/state/KeadaanAplikasi.kt` (State baru untuk produksi tanpa NG dan UI expanded/collapsed)
    - `tampilan/state/AksiAplikasi.kt` (Aksi baru: `UpdateProduksiTanpaNg`, `ToggleRingkasanExpanded`)
    - `tampilan/state/PengelolaKeadaanAplikasi.kt` (Logic sinkronisasi QTY, summary, dan produksi tanpa NG)
    - `ranah/usecase/KelolaInputHarianUseCase.kt` (Bridge ke repositori)
    - `ranah/usecase/KirimPemeriksaanHarianUseCase.kt` (Mapping data produksi tanpa NG ke DTO server)
    - `data/lokal/database/MigrasiDatabaseLokal.kt` (Migrasi versi 8: tabel draft produksi tanpa NG)
    - `data/lokal/repositori/RepositoriInputHarianLokal.kt` (CRUD lokal produksi tanpa NG)
    - `data/remote/dto/PermintaanSimpanPemeriksaanHarianDto.kt` (DTO sinkronisasi baru)
    - `tampilan/komponen/KomponenQControl.kt` (Update `PanelPremiumQControl` dengan `aksiHeader`)
    - `tampilan/halaman/HalamanInputHarian.kt` (Refactor besar: UI, input numerik, collapsible summary, seksi PRESS)

## 4. Perbaikan Auto-load Catalog Part
- Saat line atau tanggal berubah, aplikasi secara otomatis memicu `muatDraftInputHarian` yang kemudian memanggil `muatDaftarInputPart`.
- Logic pembersihan state (`inputPartTerpilih`, `matrixInputDefectPart`) dipusatkan di `GantiLineAktif`.

## 5. Perbaikan State Sync QTY dan Summary
- `updateQtyCheck` dan `updateDefectSlot` sekarang melakukan fetch ulang data dari database lokal secara async namun berurutan untuk memastikan state `inputPartTerpilih` dan `ringkasanInputHarian` sinkron dengan data terbaru.

## 6. Perubahan Direct Numeric Input
- QTY CHECK dan Produksi Tanpa NG menggunakan `BasicTextField` dengan filter hanya digit untuk input cepat tanpa mengandalkan tombol +/-.

## 7. Perubahan Total Defect per Item
- Label footer matrix diubah dari "TOTAL DEFECT PER SLOT" menjadi "TOTAL DEFECT PER ITEM" sesuai instruksi.

## 8. Perubahan Floating Summary Panel
- "Ringkasan Harian" sekarang memiliki tombol expand/collapse. Saat collapsed, hanya menampilkan metrik inti (Check/OK/NG/Rasio) dalam format chip ringkas.

## 9. Penghapusan Muat Ulang Draft
- Tombol "Muat Ulang Draft" manual telah dihapus dari UI operator karena proses muat data sudah dilakukan secara otomatis (reactive).

## 10. Fondasi Hasil Produksi Tanpa NG
- Implementasi penuh di backend (API) dan frontend (Local Draft & UI khusus Line PRESS).

## 11. Fondasi Kalender dan Histori
- Ditambahkan selector tanggal di header dengan format Indonesia. Logic pengambilan draft sudah berdasarkan tanggal yang dipilih.

## 12. Hasil Build/Test
- **PGNServer**: 65 tests passed. Linting (Pint) CLEAN.
- **QControl**: Build SUCCESSFUL (`./gradlew :desktop:compileKotlin`).

## 13. Risiko yang Selesai Dibereskan
- Desinkronisasi data antara matrix dan summary harian.
- Lambatnya input data menggunakan tombol increment.

## 14. Risiko Tersisa
- Sinkronisasi "Hasil Produksi Tanpa NG" ke server memerlukan penyesuaian outbox worker jika ada kegagalan parsial (namun saat ini menggunakan atomic transaction di server).

## 15. Rekomendasi Fase Berikutnya
- Implementasi fitur "Histori" yang lebih lengkap untuk melihat trend NG per line.
- Dashboard bulanan di desktop menggunakan data dari endpoint laporan bulanan.
