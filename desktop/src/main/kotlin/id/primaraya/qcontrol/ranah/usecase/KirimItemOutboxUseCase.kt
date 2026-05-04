package id.primaraya.qcontrol.ranah.usecase

import id.primaraya.qcontrol.data.lokal.repositori.RepositoriOutboxSinkronisasi
import id.primaraya.qcontrol.data.remote.layanan.LayananSinkronisasiRemote
import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.inti.kesalahan.KesalahanAplikasi
import id.primaraya.qcontrol.ranah.model.ItemOutboxSinkronisasi

/**
 * Use case untuk mengirimkan satu item outbox ke server.
 * Mengelola perubahan status item di repositori lokal selama proses pengiriman.
 */
class KirimItemOutboxUseCase(
    private val repositoriLokal: RepositoriOutboxSinkronisasi,
    private val layananRemote: LayananSinkronisasiRemote
) {
    /**
     * Mengeksekusi pengiriman item outbox.
     * 
     * Alur:
     * 1. Tandai item sebagai 'SEDANG_DIKIRIM' di database lokal.
     * 2. Kirim payload ke server via Ktor.
     * 3. Jika Berhasil: Tandai 'BERHASIL' di lokal.
     * 4. Jika Gagal dengan Konflik (409): Tandai 'KONFLIK' di lokal.
     * 5. Jika Gagal Lainnya: Tandai 'GAGAL' di lokal (akan dicoba ulang otomatis).
     */
    suspend fun eksekusi(item: ItemOutboxSinkronisasi): HasilOperasi<Unit> {
        // 1. Tandai sedang dikirim agar tidak diproses oleh worker lain jika ada paralisme
        repositoriLokal.tandaiSedangDikirim(item.id)
        
        // 2. Kirim payload
        val hasil = layananRemote.kirimPayload(
            endpoint = item.endpointTujuan,
            metode = item.metodeHttp,
            payloadJson = item.payloadJson,
            idempotencyKey = item.idempotencyKey
        )
        
        return when (hasil) {
            is HasilOperasi.Berhasil -> {
                // 3. Tandai berhasil
                repositoriLokal.tandaiBerhasil(item.id)
                HasilOperasi.Berhasil(Unit)
            }
            is HasilOperasi.Gagal -> {
                val kesalahan = hasil.kesalahan
                if (kesalahan is KesalahanAplikasi.Server && kesalahan.kode == "409") {
                    // 4. Tandai konflik (tidak akan dicoba lagi otomatis)
                    repositoriLokal.tandaiKonflik(item.id, kesalahan.pesan)
                } else {
                    // 5. Tandai gagal (akan dicoba lagi nanti oleh worker)
                    repositoriLokal.tandaiGagal(item.id, kesalahan.pesan)
                }
                HasilOperasi.Gagal(kesalahan)
            }
        }
    }
}
