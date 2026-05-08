package id.primaraya.qcontrol.ranah.model

data class JenisDefect(
    val id: String,
    val kodeDefect: String,
    val namaDefect: String,
    val kategoriDefectId: String? = null,
    val kodeKategori: String? = null,
    val namaKategori: String? = null,
    val aktif: Boolean
)
