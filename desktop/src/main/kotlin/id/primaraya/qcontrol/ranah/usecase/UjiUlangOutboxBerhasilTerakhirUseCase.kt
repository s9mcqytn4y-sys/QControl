package id.primaraya.qcontrol.ranah.usecase

import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.inti.kesalahan.KesalahanAplikasi
import id.primaraya.qcontrol.data.remote.layanan.LayananSinkronisasiRemote
import id.primaraya.qcontrol.data.lokal.repositori.RepositoriOutboxSinkronisasi

class UjiUlangOutboxBerhasilTerakhirUseCase(
    private val repositori: RepositoriOutboxSinkronisasi,
    private val layananRemote: LayananSinkronisasiRemote
) {
    suspend operator fun invoke(): HasilOperasi<String> {
        val hasilBaca = repositori.bacaBerhasilTerakhir()
        
        if (hasilBaca is HasilOperasi.Gagal) {
            return HasilOperasi.Gagal(hasilBaca.kesalahan)
        }
        
        val item = (hasilBaca as HasilOperasi.Berhasil).data
            ?: return HasilOperasi.Gagal(KesalahanAplikasi.DataKosong("Tidak ada item outbox dengan status BERHASIL untuk diuji ulang"))
            
        return layananRemote.kirimPayload(
            endpoint = item.endpointTujuan,
            metode = item.metodeHttp,
            payloadJson = item.payloadJson,
            idempotencyKey = item.idempotencyKey
        )
    }
}
