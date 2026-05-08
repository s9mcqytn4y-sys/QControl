package id.primaraya.qcontrol.ranah.model

data class LineProduksi(
    val id: String,
    val kodeLine: String,
    val namaLine: String,
    val aktif: Boolean,
    val urutanTampil: Int
)
