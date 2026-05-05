package id.primaraya.qcontrol.ranah.model

data class TemplateDefectPart(
    val partId: String,
    val kodeUnikPart: String?,
    val jenisDefectId: String,
    val kodeTampilanDefect: String?,
    val kodeDefect: String?,
    val namaDefect: String?,
    val namaKategori: String?,
    val urutanTampil: Int,
    val aktif: Boolean
)
