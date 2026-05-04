package id.primaraya.qcontrol.ranah.usecase

import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.ranah.model.ItemOutboxSinkronisasi
import id.primaraya.qcontrol.data.lokal.repositori.RepositoriOutboxSinkronisasi

class BacaOutboxBerhasilTerakhirUseCase(
    private val repositori: RepositoriOutboxSinkronisasi
) {
    operator fun invoke(): HasilOperasi<ItemOutboxSinkronisasi?> {
        return repositori.bacaBerhasilTerakhir()
    }
}
