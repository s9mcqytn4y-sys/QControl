package id.primaraya.qcontrol.ranah.usecase

import id.primaraya.qcontrol.data.lokal.repositori.RepositoriAutentikasiLokal
import id.primaraya.qcontrol.data.remote.layanan.LayananAutentikasiRemote
import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.ranah.model.Autentikasi

class MasukSesiUseCase(
    private val layananRemote: LayananAutentikasiRemote,
    private val repositoriLokal: RepositoriAutentikasiLokal
) {
    suspend fun eksekusi(username: String, password: String): HasilOperasi<Autentikasi> {
        val hasilRemote = layananRemote.login(username, password)
        if (hasilRemote is HasilOperasi.Berhasil) {
            val simpanHasil = repositoriLokal.simpanSesi(hasilRemote.data)
            if (simpanHasil is HasilOperasi.Gagal) {
                return HasilOperasi.Gagal(simpanHasil.kesalahan)
            }
        }
        return hasilRemote
    }
}
