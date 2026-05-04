# Laporan Reset Fondasi QControl (Android ke Compose Desktop)

## Perubahan Utama
1.  **Arsip Struktur Lama**: Memindahkan semua file Android ke `_arsip_android_lama/`.
2.  **Modul Baru `:desktop`**: Fondasi bersih menggunakan Compose Multiplatform untuk Windows.
3.  **Teknologi**: Kotlin 2.1.0, JDK 17, Compose Multiplatform 1.7.3.
4.  **UI Shell**: Sidebar navigasi, Header, dan tema warna brand (Yellow/Orange/Amber).
5.  **Refactoring Arsitektur (Fase 1C)**: 
    *   Pemisahan komponen UI (Sidebar, Header, Konten).
    *   Implementasi Unidirectional Data Flow dengan `StateFlow`.
    *   Struktur package yang mendukung Clean Architecture.
    *   Sentralisasi Design Token (Warna, Tipografi, Ukuran).
6.  **Fondasi SQLite Lokal (Fase 1E)**:
    *   Implementasi database SQLite menggunakan JDBC untuk Windows.
    *   Automated Schema Migration (Tabel Konfigurasi, Cache Status Server, Outbox).
    *   Repositori lokal untuk persistensi pengaturan aplikasi.
    *   Integrasi status penyimpanan ke dalam Reactive UI (StateFlow).

## Cara Menjalankan Aplikasi (Running)
Pastikan Anda memiliki JDK 17 terinstal di sistem Anda.

1.  Buka Terminal di root proyek `QControl`.
2.  Jalankan perintah Gradle berikut:
    ```bash
    ./gradlew :desktop:run
    ```

## Cara Membuat Installer Windows (MSI)
Untuk membangun paket distribusi native:
```bash
./gradlew :desktop:packageMsi
```
Output akan tersedia di `desktop/build/compose/binaries/`.

## Struktur Folder Baru
- `desktop/`: Modul utama aplikasi.
- `desktop/src/main/kotlin/id/primaraya/qcontrol/`: Source code utama (Bahasa Indonesia).
- `desktop/src/main/resources/`: Asset gambar dan konfigurasi.
- `_arsip_android_lama/`: Backup struktur Android sebelumnya.
