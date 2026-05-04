package id.primaraya.qcontrol.data.lokal.database

import java.sql.Connection
import java.sql.DriverManager

class KoneksiDatabaseLokal(
    private val penyediaPath: PenyediaPathDatabaseLokal
) {
    init {
        // Memastikan driver SQLite termuat
        Class.forName("org.sqlite.JDBC")
    }

    fun bukaKoneksi(): Connection {
        val path = penyediaPath.dapatkanPathDatabase().toAbsolutePath().toString()
        val url = "jdbc:sqlite:$path"
        val koneksi = DriverManager.getConnection(url)
        
        // Aktifkan konfigurasi SQLite yang disarankan
        koneksi.createStatement().use { statement ->
            statement.execute("PRAGMA foreign_keys = ON;")
            statement.execute("PRAGMA journal_mode = WAL;")
            statement.execute("PRAGMA busy_timeout = 5000;")
        }
        
        return koneksi
    }

    fun tutup() {
        // Untuk fase ini, karena koneksi dibuka per operasi, fungsi tutup global tidak perlu menutup koneksi spesifik
        // Kecuali jika nanti menerapkan connection pooling.
    }
}
