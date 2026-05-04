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
            
            // Tambahkan versi migrasi selanjutnya di sini jika diperlukan
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
}
