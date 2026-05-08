package id.primaraya.qcontrol.ranah.usecase

import id.primaraya.qcontrol.data.repositori.RepositoriOutboxSinkronisasi
import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.ranah.model.RingkasanOutboxSinkronisasi

class BacaRingkasanOutboxSinkronisasiUseCase(
    private val repositori: RepositoriOutboxSinkronisasi
) {
    suspend operator fun invoke(): HasilOperasi<RingkasanOutboxSinkronisasi> {
        return repositori.bacaRingkasan()
    }
}
