# CLAUDE - QControl

QControl adalah aplikasi desktop HeadQC dengan arsitektur offline-first yang aktif pada modul `shared` dan `composeApp`.

## Hal yang wajib dijaga
- Ikuti `AGENTS.md`
- Gunakan Bahasa Indonesia
- Jangan hidupkan kembali state lama yang monolitik
- Hormati status outbox, retry window, dan proteksi token lokal
- Jangan masukkan placeholder produksi ke sidebar atau dashboard

## Verifikasi
```bash
./gradlew :shared:compileKotlinJvm --console=plain --no-daemon
./gradlew :composeApp:compileKotlin --console=plain --no-daemon
./gradlew :composeApp:assemble --console=plain --no-daemon
```
