package id.primaraya.qcontrol.ranah.usecase

import id.primaraya.qcontrol.database.QControlDatabase
import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.ranah.model.InformasiDatabaseLokal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PeriksaDatabaseLokalUseCase(
    private val database: QControlDatabase? = null
) {
    suspend fun eksekusi(): HasilOperasi<InformasiDatabaseLokal> = withContext(Dispatchers.IO) {
        try {
            if (database == null) {
                return@withContext HasilOperasi.Berhasil(
                    InformasiDatabaseLokal(
                        tersedia = true,
                        path = "In-Memory / Default",
                        versiSkema = 1,
                        jumlahItemOutboxMenunggu = 0,
                        ukuranReadable = "N/A",
                        pesan = "Database driver terinisialisasi"
                    )
                )
            }

            val waiting = database.qControlQueries.hitungRingkasanOutbox().executeAsList()
                .find { it.status == "MENUNGGU" }?.jumlah?.toInt() ?: 0

            HasilOperasi.Berhasil(
                InformasiDatabaseLokal(
                    tersedia = true,
                    path = "SQLDelight Managed",
                    versiSkema = 1,
                    jumlahItemOutboxMenunggu = waiting,
                    ukuranReadable = "N/A",
                    pesan = "Database lokal aktif"
                )
            )
        } catch (e: Exception) {
            HasilOperasi.Berhasil(
                InformasiDatabaseLokal(
                    tersedia = false,
                    path = "Error",
                    versiSkema = 0,
                    jumlahItemOutboxMenunggu = 0,
                    ukuranReadable = "0 KB",
                    pesan = "Database lokal tidak dapat diakses: ${e.message}"
                )
            )
        }
    }
}
