# QControl

QControl adalah aplikasi desktop Windows untuk HeadQC yang mendukung pencatatan inspeksi harian secara offline-first, sinkronisasi berbasis outbox, dan data acuan yang ditarik dari server perusahaan.

## Struktur proyek
- `shared`
  - logic bisnis, use case, repositori, schema SQLDelight, utilitas, dan integrasi jaringan
- `composeApp`
  - aplikasi Compose Desktop, store MVI, halaman, komponen UI, dan composition root

## Arsitektur
- **Kotlin Multiplatform multi-module**
  - `:shared` untuk fondasi bersama
  - `:composeApp` untuk shell desktop
- **Strict MVI**
  - shell, sesi, data acuan, input harian, dan sinkronisasi dipisah per state
- **Offline-first**
  - draft harian tersimpan lokal
  - outbox mengantrekan kirim data
  - retry otomatis mengikuti `next_retry_at` dan `maks_percobaan`
- **Proteksi token sesi**
  - token lokal tidak disimpan plaintext

## Fitur aktif
- Login HeadQC
- Tarik data acuan dari server perusahaan
- Input harian berbasis draft lokal
- Ringkasan hari ini dari penyimpanan lokal
- Antrean kirim dengan status konflik dan gagal

## Verifikasi lokal
```bash
./gradlew :shared:compileKotlinJvm --console=plain --no-daemon
./gradlew :composeApp:compileKotlin --console=plain --no-daemon
./gradlew :composeApp:assemble --console=plain --no-daemon
```

## Catatan implementasi
- Jangan gunakan dependensi Android.
- Jangan hidupkan kembali state monolitik lama.
- UI tidak boleh menampilkan placeholder produksi atau jargon internal ke HeadQC.
