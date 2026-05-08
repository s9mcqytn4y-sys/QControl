package id.primaraya.qcontrol.ranah.usecase

import id.primaraya.qcontrol.data.repositori.RepositoriOutboxSinkronisasi
import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.ranah.model.ItemOutboxSinkronisasi

class BacaDaftarOutboxMenungguUseCase(
    private val repositori: RepositoriOutboxSinkronisasi
) {
    suspend operator fun invoke(batas: Int = 50): HasilOperasi<List<ItemOutboxSinkronisasi>> {
        return repositori.bacaMenunggu(batas)
    }
}
