package id.primaraya.qcontrol.data.lokal.repositori

import id.primaraya.qcontrol.data.lokal.database.KoneksiDatabaseLokal
import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.inti.kesalahan.KesalahanAplikasi
import id.primaraya.qcontrol.ranah.model.KonfigurasiLokal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

class RepositoriKonfigurasiLokal(
    private val koneksiDatabase: KoneksiDatabaseLokal
) {
    suspend fun bacaKonfigurasi(): HasilOperasi<KonfigurasiLokal> = withContext(Dispatchers.IO) {
        try {
            var urlServer: String? = null
            var lineAktif: String? = null
            var namaPenggunaTerakhir: String? = null
            var peranPenggunaTerakhir: String? = null

            koneksiDatabase.bukaKoneksi().use { koneksi ->
                val sql = "SELECT kunci, nilai FROM konfigurasi_lokal"
                koneksi.createStatement().use { statement ->
                    val resultSet = statement.executeQuery(sql)
                    while (resultSet.next()) {
                        val kunci = resultSet.getString("kunci")
                        val nilai = resultSet.getString("nilai")
                        when (kunci) {
                            "url_server" -> urlServer = nilai
                            "line_aktif" -> lineAktif = nilai
                            "nama_pengguna_terakhir" -> namaPenggunaTerakhir = nilai
                            "peran_pengguna_terakhir" -> peranPenggunaTerakhir = nilai
                        }
                    }
                }
            }

            val konfigurasiDefault = KonfigurasiLokal()
            val konfigurasi = KonfigurasiLokal(
                urlServer = urlServer ?: konfigurasiDefault.urlServer,
                lineAktif = lineAktif ?: konfigurasiDefault.lineAktif,
                namaPenggunaTerakhir = namaPenggunaTerakhir ?: konfigurasiDefault.namaPenggunaTerakhir,
                peranPenggunaTerakhir = peranPenggunaTerakhir ?: konfigurasiDefault.peranPenggunaTerakhir
            )
            
            // Jika ada yang null, simpan default agar ada di database
            if (urlServer == null || lineAktif == null || namaPenggunaTerakhir == null || peranPenggunaTerakhir == null) {
                simpanKonfigurasiInternal(konfigurasi)
            }

            HasilOperasi.Berhasil(konfigurasi)
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal membaca konfigurasi: ${e.message}"))
        }
    }

    suspend fun simpanKonfigurasi(konfigurasi: KonfigurasiLokal): HasilOperasi<Unit> = withContext(Dispatchers.IO) {
        try {
            simpanKonfigurasiInternal(konfigurasi)
            HasilOperasi.Berhasil(Unit)
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal menyimpan konfigurasi: ${e.message}"))
        }
    }

    private fun simpanKonfigurasiInternal(konfigurasi: KonfigurasiLokal) {
        koneksiDatabase.bukaKoneksi().use { koneksi ->
            koneksi.autoCommit = false
            try {
                val sql = """
                    INSERT INTO konfigurasi_lokal (kunci, nilai, diperbarui_pada) 
                    VALUES (?, ?, ?) 
                    ON CONFLICT(kunci) DO UPDATE SET 
                        nilai=excluded.nilai, 
                        diperbarui_pada=excluded.diperbarui_pada
                """.trimIndent()

                val waktuSekarang = Instant.now().toString()

                koneksi.prepareStatement(sql).use { statement ->
                    val data = listOf(
                        "url_server" to konfigurasi.urlServer,
                        "line_aktif" to konfigurasi.lineAktif,
                        "nama_pengguna_terakhir" to konfigurasi.namaPenggunaTerakhir,
                        "peran_pengguna_terakhir" to konfigurasi.peranPenggunaTerakhir
                    )

                    for ((kunci, nilai) in data) {
                        statement.setString(1, kunci)
                        statement.setString(2, nilai)
                        statement.setString(3, waktuSekarang)
                        statement.addBatch()
                    }
                    statement.executeBatch()
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
}
