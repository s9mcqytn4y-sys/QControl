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

## Batasan Fase Saat Ini (Fase 1C)
- Fitur bisnis (Dashboard, Input, dll) masih berupa **placeholder**.
- Belum ada koneksi ke API PGNServer.
- Belum ada database lokal SQLite.
- Fokus utama adalah **kerangka arsitektur yang scalable**.
