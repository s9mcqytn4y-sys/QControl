package id.primaraya.qcontrol.ranah.model

data class SlotWaktu(
    val id: String,
    val kodeSlot: String,
    val labelSlot: String,
    val jamMulai: String? = null,
    val jamSelesai: String? = null,
    val aktif: Boolean,
    val urutanTampil: Int
)
