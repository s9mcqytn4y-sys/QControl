# QControl - CLAUDE Instructions

Refer to [AGENTS.md](./AGENTS.md) for the primary source of truth regarding:
- Role: HeadQC (Solo Developer)
- Language: Full Indonesian
- Architecture: `inti`, `konfigurasi`, `ranah`, `data`, `tampilan`, `tema`, `utilitas`
- Tech Stack: Kotlin 2.x, Compose Desktop, Ktor, SQLite JDBC
- Phase: 2D-R3 (Defect Template Validation)

**Mandatory Command for Verification:**
```bash
./gradlew :desktop:compileKotlin --console=plain
./gradlew :desktop:assemble --console=plain
```
