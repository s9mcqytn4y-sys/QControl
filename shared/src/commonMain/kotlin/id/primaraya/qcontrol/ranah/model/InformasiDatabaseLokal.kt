package id.primaraya.qcontrol.ranah.model

data class InformasiDatabaseLokal(
    val tersedia: Boolean,
    val path: String,
    val versiSkema: Int?,
    val jumlahItemOutboxMenunggu: Int,
    val ukuranReadable: String,
    val pesan: String
)
