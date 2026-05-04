package id.primaraya.qcontrol.ranah.usecase

import id.primaraya.qcontrol.data.lokal.repositori.RepositoriOutboxSinkronisasi
import id.primaraya.qcontrol.inti.hasil.HasilOperasi

class TandaiOutboxBerhasilUseCase(
    private val repositori: RepositoriOutboxSinkronisasi
) {
    operator fun invoke(id: String): HasilOperasi<Unit> {
        return repositori.tandaiBerhasil(id)
    }
}
