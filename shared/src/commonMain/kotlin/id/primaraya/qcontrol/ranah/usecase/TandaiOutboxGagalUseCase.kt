package id.primaraya.qcontrol.ranah.usecase

import id.primaraya.qcontrol.data.repositori.RepositoriOutboxSinkronisasi
import id.primaraya.qcontrol.inti.hasil.HasilOperasi

class TandaiOutboxGagalUseCase(
    private val repositori: RepositoriOutboxSinkronisasi
) {
    suspend operator fun invoke(id: String, pesan: String?): HasilOperasi<Unit> {
        return repositori.tandaiGagal(id, pesan)
    }
}
