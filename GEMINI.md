# GEMINI - QControl

Gunakan `AGENTS.md` sebagai acuan utama. QControl adalah desktop app untuk HeadQC dengan KMP multi-module, SQLDelight, Ktor, Koin, dan Compose Desktop.

## Prinsip kerja
- Hindari state global monolitik
- Ikuti kontrak store MVI per fitur
- Simpan draft lokal lebih dulu, kirim lewat outbox
- Jangan tampilkan istilah teknis internal ke pengguna akhir

## Verifikasi
```bash
./gradlew :shared:compileKotlinJvm --console=plain --no-daemon
./gradlew :composeApp:compileKotlin --console=plain --no-daemon
./gradlew :composeApp:assemble --console=plain --no-daemon
```
