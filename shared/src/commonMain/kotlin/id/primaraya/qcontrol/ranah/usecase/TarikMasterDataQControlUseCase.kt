package id.primaraya.qcontrol.ranah.usecase

import id.primaraya.qcontrol.data.repositori.RepositoriMasterDataQControl
import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.inti.kesalahan.KesalahanAplikasi
import id.primaraya.qcontrol.ranah.model.RingkasanMasterData

class TarikMasterDataQControlUseCase(
    private val repositori: RepositoriMasterDataQControl,
    private val ambilSesiAktifUseCase: AmbilSesiAktifUseCase
) {
    suspend fun eksekusi(): HasilOperasi<RingkasanMasterData> {
        val hasilSesi = ambilSesiAktifUseCase.eksekusi()

        // HasilOperasi<Autentikasi?> — ambil token dari data yang bisa null
        val token: String? = when (hasilSesi) {
            is HasilOperasi.Berhasil<*> -> hasilSesi.data?.let {
                (it as? id.primaraya.qcontrol.ranah.model.Autentikasi)?.token
            }
            is HasilOperasi.Gagal -> null
        }

        if (token.isNullOrBlank()) {
            return HasilOperasi.Gagal(
                KesalahanAplikasi.Server("Sesi HeadQC tidak tersedia. Silakan login terlebih dahulu.")
            )
        }

        return repositori.tarikDanSimpanMasterData(token)
    }
}
