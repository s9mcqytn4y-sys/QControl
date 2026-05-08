package id.primaraya.qcontrol.data.repositori

import id.primaraya.qcontrol.database.QControlDatabase
import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.inti.kesalahan.KesalahanAplikasi
import id.primaraya.qcontrol.ranah.model.KonfigurasiLokal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

class RepositoriKonfigurasiLokal(
    private val database: QControlDatabase
) {
    private val queries = database.qControlQueries

    suspend fun bacaKonfigurasi(): HasilOperasi<KonfigurasiLokal> = withContext(Dispatchers.IO) {
        try {
            val data = queries.transactionWithResult {
                val urlServer = queries.dapatkanKonfigurasi("url_server").executeAsOneOrNull()?.nilai
                val lineAktif = queries.dapatkanKonfigurasi("line_aktif").executeAsOneOrNull()?.nilai
                val namaPengguna = queries.dapatkanKonfigurasi("nama_pengguna_terakhir").executeAsOneOrNull()?.nilai
                val peranPengguna = queries.dapatkanKonfigurasi("peran_pengguna_terakhir").executeAsOneOrNull()?.nilai
                
                Triple(urlServer, lineAktif, Triple(namaPengguna, peranPengguna, null))
            }

            val urlServer = data.first
            val lineAktif = data.second
            val namaPengguna = data.third.first
            val peranPengguna = data.third.second

            val konfigurasiDefault = KonfigurasiLokal()
            val konfigurasi = KonfigurasiLokal(
                urlServer = urlServer ?: konfigurasiDefault.urlServer,
                lineAktif = lineAktif ?: konfigurasiDefault.lineAktif,
                namaPenggunaTerakhir = namaPengguna ?: konfigurasiDefault.namaPenggunaTerakhir,
                peranPenggunaTerakhir = peranPengguna ?: konfigurasiDefault.peranPenggunaTerakhir
            )

            if (urlServer == null || lineAktif == null || namaPengguna == null || peranPengguna == null) {
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
        val waktuSekarang = Instant.now().toString()
        queries.transaction {
            queries.simpanKonfigurasi("url_server", konfigurasi.urlServer, waktuSekarang)
            queries.simpanKonfigurasi("line_aktif", konfigurasi.lineAktif, waktuSekarang)
            queries.simpanKonfigurasi("nama_pengguna_terakhir", konfigurasi.namaPenggunaTerakhir, waktuSekarang)
            queries.simpanKonfigurasi("peran_pengguna_terakhir", konfigurasi.peranPenggunaTerakhir, waktuSekarang)
        }
    }
}
