package id.primaraya.qcontrol.ranah.model

data class ItemOutboxSinkronisasi(
    val id: String,
    val jenisOperasi: String,
    val endpointTujuan: String,
    val metodeHttp: MetodeHttpSinkronisasi,
    val payloadJson: String,
    val idempotencyKey: String,
    val hashPayload: String?,
    val status: StatusOutboxSinkronisasi,
    val jumlahPercobaan: Int,
    val maksPercobaan: Int,
    val nextRetryAt: String?,
    val lastHttpStatus: Int?,
    val lastErrorCode: String?,
    val pesanGagalTerakhir: String?,
    val dibuatPada: String,
    val diperbaruiPada: String,
    val dikirimPada: String?
)
