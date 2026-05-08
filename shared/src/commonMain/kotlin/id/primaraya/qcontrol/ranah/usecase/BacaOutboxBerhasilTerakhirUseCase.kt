package id.primaraya.qcontrol.ranah.usecase

import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.ranah.model.ItemOutboxSinkronisasi
import id.primaraya.qcontrol.data.repositori.RepositoriOutboxSinkronisasi

class BacaOutboxBerhasilTerakhirUseCase(
    private val repositori: RepositoriOutboxSinkronisasi
) {
    suspend operator fun invoke(): HasilOperasi<ItemOutboxSinkronisasi?> {
        return repositori.bacaBerhasilTerakhir()
    }
}
