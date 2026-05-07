package id.primaraya.qcontrol.ranah.usecase

import id.primaraya.qcontrol.data.lokal.repositori.RepositoriOutboxSinkronisasi
import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.ranah.model.ItemOutboxSinkronisasi
import id.primaraya.qcontrol.ranah.model.MetodeHttpSinkronisasi
import id.primaraya.qcontrol.ranah.model.StatusOutboxSinkronisasi
import id.primaraya.qcontrol.utilitas.PembuatIdempotencyKey
import id.primaraya.qcontrol.utilitas.PembuatWaktuSekarang
import java.util.UUID

class BuatItemOutboxSinkronisasiUseCase(
    private val repositori: RepositoriOutboxSinkronisasi
) {
    operator fun invoke(
        jenisOperasi: String,
        endpointTujuan: String,
        metodeHttp: MetodeHttpSinkronisasi,
        payloadJson: String
    ): HasilOperasi<Unit> {
        val sekarang = PembuatWaktuSekarang.buatIsoOffsetSekarang()
        
        val item = ItemOutboxSinkronisasi(
            id = UUID.randomUUID().toString(),
            jenisOperasi = jenisOperasi,
            endpointTujuan = endpointTujuan,
            metodeHttp = metodeHttp,
            payloadJson = payloadJson,
            idempotencyKey = PembuatIdempotencyKey.buat(jenisOperasi, endpointTujuan),
            hashPayload = null,
            status = StatusOutboxSinkronisasi.MENUNGGU,
            jumlahPercobaan = 0,
            pesanGagalTerakhir = null,
            dibuatPada = sekarang,
            diperbaruiPada = sekarang,
            dikirimPada = null
        )
        
        return repositori.tambah(item)
    }
}
