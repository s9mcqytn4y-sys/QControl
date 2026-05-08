package id.primaraya.qcontrol.ranah.usecase

import id.primaraya.qcontrol.data.repositori.RepositoriKonfigurasiLokal
import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.ranah.model.KonfigurasiLokal

class SimpanKonfigurasiLokalUseCase(
    private val repositori: RepositoriKonfigurasiLokal
) {
    suspend operator fun invoke(konfigurasi: KonfigurasiLokal): HasilOperasi<Unit> {
        return repositori.simpanKonfigurasi(konfigurasi)
    }
}
