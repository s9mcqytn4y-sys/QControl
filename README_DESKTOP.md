# QControl Desktop - Fase Fondasi & Arsitektur

Dokumen ini menjelaskan struktur dan cara kerja aplikasi QControl versi Desktop.

## Teknologi Utama
- **Kotlin 2.1.0** (JVM 17)
- **Compose Multiplatform 1.7.3**
- **Coroutines & Flow** untuk State Management
- **Material 3** untuk Design System

## Cara Menjalankan
Pastikan JDK 17 terinstal, lalu jalankan perintah di root project:

```bash
./gradlew :desktop:run
```

Untuk membangun installer MSI (Windows):
```bash
./gradlew :desktop:packageMsi
```

## Struktur Folder Utama
- `inti/`: Logika dasar aplikasi (hasil, kesalahan, dsb).
- `ranah/`: Domain layer (model bisnis dan use case).
- `data/`: Data layer (repository, lokal, remote).
- `tampilan/`: Presentation layer (UI, state, navigasi).
- `tema/`: Design system (warna, tipografi, ukuran).
- `konfigurasi/`: Pengaturan global aplikasi.

## State Management & Navigasi
Aplikasi menggunakan pola **Unidirectional Data Flow (UDF)**:
1.  **KeadaanAplikasi**: Data class yang menampung seluruh state UI.
2.  **AksiAplikasi**: Sealed class yang mendefinisikan interaksi pengguna.
3.  **PengelolaKeadaanAplikasi**: State holder yang mengelola perubahan state melalui Flow.

## Batasan Fase Saat Ini (Fase 1F)
- Fitur bisnis (Dashboard, Input, dll) masih berupa **placeholder**.
- Sudah memiliki fondasi **Ktor Client** untuk koneksi ke PGNServer.
- Fitur **Cek Kesehatan Server** sudah aktif di Header.
- Sudah memiliki **SQLite Lokal** untuk persistensi offline-first.
- Sudah memiliki **Skeleton Outbox Sinkronisasi** (Fase 1F).

## Outbox Sinkronisasi & Idempotency Key
Fitur ini adalah fondasi agar aplikasi tetap bisa bekerja saat offline.

### Prinsip Kerja:
1. Setiap aksi yang perlu dikirim ke server disimpan di tabel `outbox_sinkronisasi` lokal.
2. Setiap item memiliki `idempotency_key` untuk mencegah duplikasi data di sisi server.
3. Status Outbox: `MENUNGGU`, `SEDANG_DIKIRIM`, `BERHASIL`, `GAGAL`, `KONFLIK`.

### Cara Menguji (Fase 1F):
1. Jalankan aplikasi: `./gradlew :desktop:run`.
2. Buka halaman **Pengaturan**.
3. Klik tombol **Buat Contoh Item Outbox**.
4. Klik tombol **Muat Ringkasan Outbox**.
5. Pastikan jumlah **Menunggu** atau **Total Item** bertambah.

*Catatan: Sinkronisasi otomatis ke server belum diaktifkan di fase ini.*

## Integrasi PGNServer (Backend)
1. Jalankan PGNServer lokal (Laravel):
   ```bash
   ./vendor/bin/sail up -d
   ```
2. Pastikan API Kesehatan dapat diakses:
   `GET http://localhost:8000/api/v1/kesehatan`
3. Di QControl Desktop, klik tombol **Refresh** pada Header untuk memperbarui status koneksi.

### Perilaku Koneksi:
- **Hijau**: Tersambung ke PGNServer.
- **Kuning**: Sedang memeriksa...
- **Merah**: Terputus (Server mati atau URL salah).
