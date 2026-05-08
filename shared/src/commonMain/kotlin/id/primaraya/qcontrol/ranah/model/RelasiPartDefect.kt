package id.primaraya.qcontrol.ranah.model

data class RelasiPartDefect(
    val id: String,
    val partId: String,
    val kodeUnikPart: String? = null,
    val jenisDefectId: String,
    val kodeDefect: String? = null,
    val kodeTampilanDefect: String? = null,
    val urutanTampil: Int,
    val aktif: Boolean
)
