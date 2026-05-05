package id.primaraya.qcontrol.data.lokal.database

import java.sql.Connection
import java.time.Instant

class MigrasiDatabaseLokal(
    private val koneksiDatabase: KoneksiDatabaseLokal
) {
    fun jalankanMigrasi() {
        koneksiDatabase.bukaKoneksi().use { koneksi ->
            buatTabelMetadataMigrasiJikaBelumAda(koneksi)
            
            val versiSaatIni = dapatkanVersiMigrasiSaatIni(koneksi)
            
            if (versiSaatIni < 1) {
                migrasiVersi1(koneksi)
                catatMigrasi(koneksi, 1, "inisialisasi_tabel_awal")
            }
            
            if (versiSaatIni < 2) {
                migrasiVersi2(koneksi)
                catatMigrasi(koneksi, 2, "tambah_tabel_sesi_auth")
            }

            if (versiSaatIni < 3) {
                migrasiVersi3(koneksi)
                catatMigrasi(koneksi, 3, "tambah_kolom_email_sesi_auth")
            }

            if (versiSaatIni < 4) {
                migrasiVersi4(koneksi)
                catatMigrasi(koneksi, 4, "tambah_tabel_cache_master_data")
            }

            if (versiSaatIni < 5) {
                migrasiVersi5(koneksi)
                catatMigrasi(koneksi, 5, "tambah_kolom_kode_tampilan_defect_relasi")
            }
        }
    }

    private fun buatTabelMetadataMigrasiJikaBelumAda(koneksi: Connection) {
        val sql = """
            CREATE TABLE IF NOT EXISTS migrasi_database_lokal (
                versi INTEGER PRIMARY KEY,
                nama TEXT NOT NULL,
                dijalankan_pada TEXT NOT NULL
            )
        """.trimIndent()
        koneksi.createStatement().use { statement ->
            statement.execute(sql)
        }
    }

    private fun dapatkanVersiMigrasiSaatIni(koneksi: Connection): Int {
        val sql = "SELECT MAX(versi) as versi_terakhir FROM migrasi_database_lokal"
        return koneksi.createStatement().use { statement ->
            val resultSet = statement.executeQuery(sql)
            if (resultSet.next()) {
                resultSet.getInt("versi_terakhir")
            } else {
                0
            }
        }
    }

    private fun catatMigrasi(koneksi: Connection, versi: Int, nama: String) {
        val sql = "INSERT INTO migrasi_database_lokal (versi, nama, dijalankan_pada) VALUES (?, ?, ?)"
        koneksi.prepareStatement(sql).use { statement ->
            statement.setInt(1, versi)
            statement.setString(2, nama)
            statement.setString(3, Instant.now().toString())
            statement.executeUpdate()
        }
    }

    private fun migrasiVersi1(koneksi: Connection) {
        val sqlKonfigurasi = """
            CREATE TABLE IF NOT EXISTS konfigurasi_lokal (
                kunci TEXT PRIMARY KEY,
                nilai TEXT NOT NULL,
                diperbarui_pada TEXT NOT NULL
            )
        """.trimIndent()
        
        val sqlCacheStatus = """
            CREATE TABLE IF NOT EXISTS cache_status_server (
                id INTEGER PRIMARY KEY CHECK (id = 1),
                status TEXT NOT NULL,
                nama_aplikasi TEXT,
                versi_api TEXT,
                waktu_server TEXT,
                zona_waktu TEXT,
                status_database TEXT,
                driver_database TEXT,
                diperbarui_pada TEXT NOT NULL
            )
        """.trimIndent()
        
        val sqlOutbox = """
            CREATE TABLE IF NOT EXISTS outbox_sinkronisasi (
                id TEXT PRIMARY KEY,
                jenis_operasi TEXT NOT NULL,
                endpoint_tujuan TEXT NOT NULL,
                metode_http TEXT NOT NULL,
                payload_json TEXT NOT NULL,
                idempotency_key TEXT NOT NULL UNIQUE,
                status TEXT NOT NULL,
                jumlah_percobaan INTEGER NOT NULL DEFAULT 0,
                pesan_gagal_terakhir TEXT,
                dibuat_pada TEXT NOT NULL,
                diperbarui_pada TEXT NOT NULL
            )
        """.trimIndent()
        
        koneksi.autoCommit = false
        try {
            koneksi.createStatement().use { statement ->
                statement.execute(sqlKonfigurasi)
                statement.execute(sqlCacheStatus)
                statement.execute(sqlOutbox)
            }
            koneksi.commit()
        } catch (e: Exception) {
            koneksi.rollback()
            throw e
        } finally {
            koneksi.autoCommit = true
        }
    }

    private fun migrasiVersi2(koneksi: Connection) {
        val sqlSesiAuth = """
            CREATE TABLE IF NOT EXISTS sesi_autentikasi (
                id INTEGER PRIMARY KEY CHECK (id = 1),
                token TEXT NOT NULL,
                nama_pengguna TEXT NOT NULL,
                peran TEXT NOT NULL,
                dibuat_pada TEXT NOT NULL
            )
        """.trimIndent()

        koneksi.autoCommit = false
        try {
            koneksi.createStatement().use { statement ->
                statement.execute(sqlSesiAuth)
            }
            koneksi.commit()
        } catch (e: Exception) {
            koneksi.rollback()
            throw e
        } finally {
            koneksi.autoCommit = true
        }
    }

    private fun migrasiVersi3(koneksi: Connection) {
        val namaTabel = "sesi_autentikasi"
        val namaKolom = "email"
        
        // Cek apakah kolom sudah ada (Idempotent Migration)
        var kolomSudahAda = false
        val sqlCek = "PRAGMA table_info($namaTabel)"
        koneksi.createStatement().use { statement ->
            val resultSet = statement.executeQuery(sqlCek)
            while (resultSet.next()) {
                if (resultSet.getString("name") == namaKolom) {
                    kolomSudahAda = true
                    break
                }
            }
        }

        if (!kolomSudahAda) {
            val sqlTambah = "ALTER TABLE $namaTabel ADD COLUMN $namaKolom TEXT"
            koneksi.createStatement().use { statement ->
                statement.execute(sqlTambah)
            }
        }
    }

    private fun migrasiVersi4(koneksi: Connection) {
        val sqlMetadata = """
            CREATE TABLE IF NOT EXISTS metadata_master_data (
                kunci TEXT PRIMARY KEY,
                nilai TEXT NOT NULL,
                diperbarui_pada TEXT NOT NULL
            )
        """.trimIndent()

        val sqlLineProduksi = """
            CREATE TABLE IF NOT EXISTS master_line_produksi (
                id TEXT PRIMARY KEY,
                kode_line TEXT NOT NULL UNIQUE,
                nama_line TEXT NOT NULL,
                aktif INTEGER NOT NULL,
                urutan_tampil INTEGER NOT NULL
            )
        """.trimIndent()

        val sqlSlotWaktu = """
            CREATE TABLE IF NOT EXISTS master_slot_waktu (
                id TEXT PRIMARY KEY,
                kode_slot TEXT NOT NULL UNIQUE,
                label_slot TEXT NOT NULL,
                jam_mulai TEXT,
                jam_selesai TEXT,
                aktif INTEGER NOT NULL,
                urutan_tampil INTEGER NOT NULL
            )
        """.trimIndent()

        val sqlMaterial = """
            CREATE TABLE IF NOT EXISTS master_material (
                id TEXT PRIMARY KEY,
                kode_material TEXT,
                nama_material TEXT NOT NULL UNIQUE,
                aktif INTEGER NOT NULL
            )
        """.trimIndent()

        val sqlPart = """
            CREATE TABLE IF NOT EXISTS master_part (
                id TEXT PRIMARY KEY,
                kode_unik_part TEXT NOT NULL UNIQUE,
                nama_part TEXT NOT NULL,
                nomor_part TEXT,
                material_id TEXT,
                kode_material TEXT,
                nama_material TEXT,
                kode_proyek TEXT,
                jumlah_item_per_kanban INTEGER,
                line_default_id TEXT,
                kode_line_default TEXT,
                nama_line_default TEXT,
                aktif INTEGER NOT NULL,
                sumber_data TEXT
            )
        """.trimIndent()

        val sqlKategoriDefect = """
            CREATE TABLE IF NOT EXISTS master_kategori_defect (
                id TEXT PRIMARY KEY,
                kode_kategori TEXT NOT NULL UNIQUE,
                nama_kategori TEXT NOT NULL,
                aktif INTEGER NOT NULL,
                urutan_tampil INTEGER NOT NULL
            )
        """.trimIndent()

        val sqlJenisDefect = """
            CREATE TABLE IF NOT EXISTS master_jenis_defect (
                id TEXT PRIMARY KEY,
                kode_defect TEXT NOT NULL UNIQUE,
                nama_defect TEXT NOT NULL,
                kategori_defect_id TEXT,
                kode_kategori TEXT,
                nama_kategori TEXT,
                aktif INTEGER NOT NULL
            )
        """.trimIndent()

        val sqlRelasiPartDefect = """
            CREATE TABLE IF NOT EXISTS master_relasi_part_defect (
                id TEXT PRIMARY KEY,
                part_id TEXT NOT NULL,
                kode_unik_part TEXT,
                jenis_defect_id TEXT NOT NULL,
                kode_defect TEXT,
                urutan_tampil INTEGER NOT NULL,
                aktif INTEGER NOT NULL,
                UNIQUE(part_id, jenis_defect_id)
            )
        """.trimIndent()

        koneksi.autoCommit = false
        try {
            koneksi.createStatement().use { statement ->
                statement.execute(sqlMetadata)
                statement.execute(sqlLineProduksi)
                statement.execute(sqlSlotWaktu)
                statement.execute(sqlMaterial)
                statement.execute(sqlPart)
                statement.execute(sqlKategoriDefect)
                statement.execute(sqlJenisDefect)
                statement.execute(sqlRelasiPartDefect)
            }
            koneksi.commit()
        } catch (e: Exception) {
            koneksi.rollback()
            throw e
        } finally {
            koneksi.autoCommit = true
        }
    }

    private fun migrasiVersi5(koneksi: Connection) {
        val namaTabel = "master_relasi_part_defect"
        val namaKolom = "kode_tampilan_defect"
        
        // Cek apakah kolom sudah ada (Idempotent Migration)
        var kolomSudahAda = false
        val sqlCek = "PRAGMA table_info($namaTabel)"
        koneksi.createStatement().use { statement ->
            val resultSet = statement.executeQuery(sqlCek)
            while (resultSet.next()) {
                if (resultSet.getString("name") == namaKolom) {
                    kolomSudahAda = true
                    break
                }
            }
        }

        if (!kolomSudahAda) {
            val sqlTambah = "ALTER TABLE $namaTabel ADD COLUMN $namaKolom TEXT"
            koneksi.createStatement().use { statement ->
                statement.execute(sqlTambah)
            }
        }
    }
}
