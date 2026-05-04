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
        // 1. Tandai sedang dikirim agar tidak diproses oleh worker lain jika ada paralelisme
        val hasilTandaiSedangDikirim = repositoriLokal.tandaiSedangDikirim(item.id)
        if (hasilTandaiSedangDikirim is HasilOperasi.Gagal) {
            return HasilOperasi.Gagal(
                KesalahanAplikasi.PenyimpananLokal(
                    "Gagal memulai proses kirim: ${hasilTandaiSedangDikirim.kesalahan.pesan}"
                )
            )
        }
        
        // 2. Kirim payload
        val hasilRemote = layananRemote.kirimPayload(
            endpoint = item.endpointTujuan,
            metode = item.metodeHttp,
            payloadJson = item.payloadJson,
            idempotencyKey = item.idempotencyKey
        )
        
        return when (hasilRemote) {
            is HasilOperasi.Berhasil -> {
                // 3. Tandai berhasil di lokal
                val hasilTandaiBerhasil = repositoriLokal.tandaiBerhasil(item.id)
                if (hasilTandaiBerhasil is HasilOperasi.Gagal) {
                    HasilOperasi.Gagal(
                        KesalahanAplikasi.PenyimpananLokal(
                            "Data terkirim ke server, tapi gagal memperbarui status lokal: ${hasilTandaiBerhasil.kesalahan.pesan}"
                        )
                    )
                } else {
                    HasilOperasi.Berhasil(Unit)
                }
            }
            is HasilOperasi.Gagal -> {
                val kesalahanRemote = hasilRemote.kesalahan
                if (kesalahanRemote is KesalahanAplikasi.Server && kesalahanRemote.kode == "409") {
                    // 4. Tandai konflik (tidak akan dicoba lagi otomatis)
                    val hasilTandaiKonflik = repositoriLokal.tandaiKonflik(item.id, kesalahanRemote.pesan)
                    if (hasilTandaiKonflik is HasilOperasi.Gagal) {
                        HasilOperasi.Gagal(
                            KesalahanAplikasi.TidakDiketahui(
                                "Server konflik dan gagal simpan status lokal: ${hasilTandaiKonflik.kesalahan.pesan}"
                            )
                        )
                    } else {
                        HasilOperasi.Gagal(kesalahanRemote)
                    }
                } else {
                    // 5. Tandai gagal (akan dicoba lagi nanti oleh worker)
                    val hasilTandaiGagal = repositoriLokal.tandaiGagal(item.id, kesalahanRemote.pesan)
                    if (hasilTandaiGagal is HasilOperasi.Gagal) {
                        HasilOperasi.Gagal(
                            KesalahanAplikasi.TidakDiketahui(
                                "Server gagal dan gagal simpan status lokal: ${hasilTandaiGagal.kesalahan.pesan}"
                            )
                        )
                    } else {
                        HasilOperasi.Gagal(kesalahanRemote)
                    }
                }
            }
        }
    }
}
