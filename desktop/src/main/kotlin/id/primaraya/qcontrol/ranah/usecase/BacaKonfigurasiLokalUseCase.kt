package id.primaraya.qcontrol.ranah.usecase

import id.primaraya.qcontrol.data.lokal.repositori.RepositoriKonfigurasiLokal
import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.ranah.model.KonfigurasiLokal

class BacaKonfigurasiLokalUseCase(
    private val repositori: RepositoriKonfigurasiLokal
) {
    suspend operator fun invoke(): HasilOperasi<KonfigurasiLokal> {
        return repositori.bacaKonfigurasi()
    }
}
