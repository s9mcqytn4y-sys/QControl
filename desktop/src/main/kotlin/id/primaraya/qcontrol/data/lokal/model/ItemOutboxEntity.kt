package id.primaraya.qcontrol.data.lokal.model

data class ItemOutboxEntity(
    val id: String,
    val jenisOperasi: String,
    val endpointTujuan: String,
    val metodeHttp: String,
    val payloadJson: String,
    val idempotencyKey: String,
    val status: String,
    val jumlahPercobaan: Int,
    val pesanGagalTerakhir: String?,
    val dibuatPada: String,
    val diperbaruiPada: String
)
