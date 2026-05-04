package id.primaraya.qcontrol.ranah.usecase

import id.primaraya.qcontrol.data.lokal.repositori.RepositoriOutboxSinkronisasi
import id.primaraya.qcontrol.inti.hasil.HasilOperasi

class TandaiOutboxKonflikUseCase(
    private val repositori: RepositoriOutboxSinkronisasi
) {
    operator fun invoke(id: String, pesan: String): HasilOperasi<Unit> {
        return repositori.tandaiKonflik(id, pesan)
    }
}
