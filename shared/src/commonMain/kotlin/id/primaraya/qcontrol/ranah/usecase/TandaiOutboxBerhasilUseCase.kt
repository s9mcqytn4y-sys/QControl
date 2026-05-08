package id.primaraya.qcontrol.ranah.usecase

import id.primaraya.qcontrol.data.repositori.RepositoriOutboxSinkronisasi
import id.primaraya.qcontrol.inti.hasil.HasilOperasi

class TandaiOutboxBerhasilUseCase(
    private val repositori: RepositoriOutboxSinkronisasi
) {
    suspend operator fun invoke(id: String): HasilOperasi<Unit> {
        return repositori.tandaiBerhasil(id)
    }
}
