# AGENTS - QControl

Instruksi terpusat untuk agen AI pada repo `QControl`. File ini adalah **source of truth** untuk alur kerja agen, arsitektur aktif, dan batasan implementasi.

## 1. Identitas Repo
- **Nama**: QControl
- **Tipe**: Desktop Windows App, bukan Android
- **Pengguna utama**: HeadQC
- **Bahasa proyek**: Bahasa Indonesia penuh untuk nama file, fungsi, variabel, komentar, dan copy UI, kecuali istilah teknis yang wajib

## 2. Stack Aktif
- **Bahasa**: Kotlin 2.x, JDK 17
- **UI**: Compose for Desktop
- **Networking**: Ktor Client
- **Penyimpanan lokal**: SQLDelight + SQLite driver JVM
- **DI**: Koin
- **Concurrency**: Coroutines + StateFlow
- **Backend**: Server perusahaan pada `http://127.0.0.1:8000`

## 3. Struktur Modul
- `:shared`
  - `commonMain`: ranah, use case, repositori, DTO, SQLDelight schema, MVI core, utilitas umum
  - `jvmMain`: driver database, proteksi token sesi, binding khusus desktop
- `:composeApp`
  - composition root, store MVI, halaman Compose Desktop, kerangka aplikasi, dan tema

## 4. Arsitektur Aktif
- Gunakan **offline-first**.
- UI hanya boleh bergantung pada **state store** dan **intent**.
- Jangan lakukan manual DI di composable layar. Gunakan `rememberAplikasiGraph()` dan modul Koin.
- Pisahkan state per fitur:
  - `ShellState`
  - `SessionState`
  - `MasterDataState`
  - `InputHarianState`
  - `SinkronisasiState`
- Hindari kebangkitan kembali `KeadaanAplikasi` atau pengelola state monolitik.

## 5. Aturan Domain
- **Role tunggal**: `HeadQC`
- Jangan tambahkan role baru.
- **Master data** bersifat **read-only** dari server perusahaan.
- Input harian harus tetap bisa disimpan lokal walau server tidak tersedia.
- Pengiriman memakai outbox dengan `X-Idempotency-Key`.
- Status outbox yang berlaku:
  - `MENUNGGU`
  - `SEDANG_DIKIRIM`
  - `GAGAL_SEMENTARA`
  - `KONFLIK`
  - `BERHASIL`
- Retry otomatis hanya berlaku untuk `MENUNGGU` dan `GAGAL_SEMENTARA`, dan harus menghormati `next_retry_at` serta `maks_percobaan`.

## 6. Batasan Teknis
- Dilarang memakai dependensi Android.
- Semua operasi repositori database dan HTTP wajib berjalan pada `Dispatchers.IO`.
- Token sesi lokal tidak boleh disimpan plaintext.
- Jangan tampilkan jargon internal seperti nama server lama, idempotency, atau istilah debugging ke pengguna akhir.
- Jangan tambahkan placeholder produksi di sidebar atau dashboard.

## 7. Fokus UI
- Gunakan copy yang jelas untuk HeadQC.
- Dashboard harus menampilkan data lokal nyata atau state terblokir yang jujur.
- Gunakan komponen virtualized seperti `LazyColumn`.
- Validasi angka harus menolak nilai negatif.
- Search input harus didebounce.

## 8. Command Verifikasi
```bash
./gradlew :shared:compileKotlinJvm --console=plain --no-daemon
./gradlew :composeApp:compileKotlin --console=plain --no-daemon
./gradlew :composeApp:assemble --console=plain --no-daemon
```

## 9. Housekeeping Repo
- Hapus artefak lama yang tidak lagi dipakai setelah migrasi KMP.
- Pastikan dokumentasi berikut selalu sinkron saat arsitektur berubah:
  - `README.md`
  - `AGENTS.md`
  - `CODEX.md`
  - `GEMINI.md`
  - `CLAUDE.md`

## 10. Format Patch Report
Gunakan format berikut saat melaporkan hasil kerja:

```text
PATCH REPORT - QControl - AgentOps-R1

1. Ringkasan keputusan teknis
2. File dibuat
3. File diubah
4. Koreksi penting yang dilakukan
5. Command yang dijalankan
6. Hasil verifikasi
7. Risiko tersisa
8. Rekomendasi fase berikutnya
```
