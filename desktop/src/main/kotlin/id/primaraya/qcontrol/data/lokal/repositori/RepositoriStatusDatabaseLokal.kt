package id.primaraya.qcontrol.data.lokal.repositori

import id.primaraya.qcontrol.data.lokal.database.KoneksiDatabaseLokal
import id.primaraya.qcontrol.data.lokal.database.MigrasiDatabaseLokal
import id.primaraya.qcontrol.data.lokal.database.PenyediaPathDatabaseLokal
import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.inti.kesalahan.KesalahanAplikasi
import id.primaraya.qcontrol.ranah.model.InformasiDatabaseLokal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import java.nio.file.Files
import kotlin.io.path.fileSize

class RepositoriStatusDatabaseLokal(
    private val penyediaPath: PenyediaPathDatabaseLokal,
    private val koneksiDatabase: KoneksiDatabaseLokal,
    private val migrasiDatabaseLokal: MigrasiDatabaseLokal
) {
    suspend fun periksaStatus(): HasilOperasi<InformasiDatabaseLokal> = withContext(Dispatchers.IO) {
        val pathDb = penyediaPath.dapatkanPathDatabase()
        val stringPath = pathDb.toAbsolutePath().toString()
        
        try {
            migrasiDatabaseLokal.jalankanMigrasi()

            var versiTerakhir: Int? = null
            var jumlahOutbox = 0

            koneksiDatabase.bukaKoneksi().use { koneksi ->
                val sqlVersi = "SELECT MAX(versi) as versi_terakhir FROM migrasi_database_lokal"
                koneksi.createStatement().use { statement ->
                    val resultSet = statement.executeQuery(sqlVersi)
                    if (resultSet.next()) {
                        versiTerakhir = resultSet.getInt("versi_terakhir")
                        if (resultSet.wasNull()) versiTerakhir = null
                    }
                }

                val sqlOutbox = "SELECT COUNT(*) as jumlah FROM outbox_sinkronisasi WHERE status = 'MENUNGGU'"
                koneksi.createStatement().use { statement ->
                    val resultSet = statement.executeQuery(sqlOutbox)
                    if (resultSet.next()) {
                        jumlahOutbox = resultSet.getInt("jumlah")
                    }
                }
            }

            val ukuranBytes = if (Files.exists(pathDb)) pathDb.fileSize() else 0L
            val ukuranKB = ukuranBytes / 1024.0
            val ukuranTeks = if (ukuranKB > 1024) String.format("%.2f MB", ukuranKB / 1024.0) else String.format("%.2f KB", ukuranKB)

            HasilOperasi.Berhasil(
                InformasiDatabaseLokal(
                    tersedia = true,
                    path = stringPath,
                    versiSkema = versiTerakhir,
                    jumlahItemOutboxMenunggu = jumlahOutbox,
                    ukuranReadable = ukuranTeks,
                    pesan = "Database lokal berhasil diakses"
                )
            )
        } catch (e: Exception) {
            HasilOperasi.Gagal(
                KesalahanAplikasi.PenyimpananLokal("Gagal mengakses database lokal: ${e.message}")
            )
        }
    }
}
