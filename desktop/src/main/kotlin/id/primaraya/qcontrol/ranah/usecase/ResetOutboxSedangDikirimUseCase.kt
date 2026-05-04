package id.primaraya.qcontrol.ranah.usecase

import id.primaraya.qcontrol.data.lokal.repositori.RepositoriOutboxSinkronisasi
import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.konfigurasi.KonfigurasiSinkronisasi

/**
 * Use case untuk memulihkan item outbox yang terjebak dalam status SEDANG_DIKIRIM.
 */
class ResetOutboxSedangDikirimUseCase(
    private val repositori: RepositoriOutboxSinkronisasi
) {
    /**
     * Mengeksekusi pemulihan item outbox yang kadaluarsa.
     */
    operator fun invoke(): HasilOperasi<Int> {
        return repositori.resetSedangDikirimKadaluarsa(
            KonfigurasiSinkronisasi.BATAS_MENIT_RESET_SEDANG_DIKIRIM
        )
    }
}
