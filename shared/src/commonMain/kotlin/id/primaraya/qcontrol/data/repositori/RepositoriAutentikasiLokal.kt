package id.primaraya.qcontrol.data.repositori

import id.primaraya.qcontrol.database.QControlDatabase
import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.inti.kesalahan.KesalahanAplikasi
import id.primaraya.qcontrol.keamanan.PelindungTokenSesi
import id.primaraya.qcontrol.ranah.model.Autentikasi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import id.primaraya.qcontrol.konfigurasi.KonfigurasiPeran

class RepositoriAutentikasiLokal(
    private val database: QControlDatabase
) {
    private val queries = database.qControlQueries

    suspend fun simpanSesi(autentikasi: Autentikasi): HasilOperasi<Unit> = withContext(Dispatchers.IO) {
        if (!autentikasi.adalahHeadQC()) {
            return@withContext HasilOperasi.Gagal(KesalahanAplikasi.Validasi("Hanya role HeadQC yang diperbolehkan menyimpan sesi."))
        }

        try {
            queries.simpanSesi(
                token = PelindungTokenSesi.lindungi(autentikasi.token),
                nama_pengguna = autentikasi.namaPengguna,
                email = autentikasi.email,
                peran = autentikasi.peran,
                dibuat_pada = Instant.now().toString()
            )
            HasilOperasi.Berhasil(Unit)
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal menyimpan sesi: ${e.message}"))
        }
    }

    suspend fun bacaSesi(): HasilOperasi<Autentikasi?> = withContext(Dispatchers.IO) {
        try {
            val sesi = queries.dapatkanSesi().executeAsOneOrNull()
            if (sesi != null) {
                if (sesi.peran != KonfigurasiPeran.HEAD_QC) {
                    return@withContext HasilOperasi.Berhasil(null)
                }

                val autentikasi = Autentikasi(
                    token = PelindungTokenSesi.buka(sesi.token),
                    namaPengguna = sesi.nama_pengguna,
                    peran = sesi.peran,
                    email = sesi.email
                )
                HasilOperasi.Berhasil(autentikasi)
            } else {
                HasilOperasi.Berhasil(null)
            }
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal membaca sesi: ${e.message}"))
        }
    }

    suspend fun hapusSesi(): HasilOperasi<Unit> = withContext(Dispatchers.IO) {
        try {
            queries.hapusSesi()
            HasilOperasi.Berhasil(Unit)
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal menghapus sesi: ${e.message}"))
        }
    }
}
