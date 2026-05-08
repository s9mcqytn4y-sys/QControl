# CODEX - QControl

Repo ini adalah aplikasi desktop Windows untuk HeadQC dengan arsitektur aktif `:shared` + `:composeApp`.

## Aturan cepat
- Baca `AGENTS.md` terlebih dahulu.
- Gunakan Bahasa Indonesia penuh.
- Pertahankan offline-first dan strict MVI.
- Jangan tambahkan dependensi Android.
- Semua operasi database dan HTTP wajib melalui `Dispatchers.IO`.

## Jalur kerja
- `shared` untuk domain, use case, repositori, SQLDelight, dan utilitas
- `composeApp` untuk store MVI, UI, navigasi, dan composition root

## Verifikasi
```bash
./gradlew :shared:compileKotlinJvm --console=plain --no-daemon
./gradlew :composeApp:compileKotlin --console=plain --no-daemon
./gradlew :composeApp:assemble --console=plain --no-daemon
```
