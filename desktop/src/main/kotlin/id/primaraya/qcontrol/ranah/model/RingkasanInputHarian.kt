package id.primaraya.qcontrol.ranah.model

data class RingkasanInputHarian(
    val totalQtyCheck: Int,
    val totalQtyDefect: Int,
    val daftarDefect: List<DefectTerhitung>
)

data class DefectTerhitung(
    val namaDefect: String,
    val jumlah: Int
)
