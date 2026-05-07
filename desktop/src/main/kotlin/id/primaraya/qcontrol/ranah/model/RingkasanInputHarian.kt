package id.primaraya.qcontrol.ranah.model

data class RingkasanInputHarian(
    val totalQtyCheck: Int,
    val totalQtyDefect: Int,
    val totalPartSudahDiisi: Int = 0,
    val totalPartBelumDiisi: Int = 0,
    val daftarDefect: List<DefectTerhitung>,
    val daftarPerSlot: List<DefectPerSlotTerhitung> = emptyList()
)

data class DefectTerhitung(
    val namaDefect: String,
    val jumlah: Int
)

data class DefectPerSlotTerhitung(
    val slotWaktuId: String,
    val labelSlot: String,
    val jumlah: Int
)
