package id.primaraya.qcontrol.ranah.model

data class DraftInputPart(
    val id: String,
    val pemeriksaanHarianId: String,
    val partId: String,
    val namaPart: String = "",
    val nomorPart: String = "",
    val qtyCheck: Int = 0,
    val totalOk: Int = 0,
    val totalDefect: Int = 0,
    val rasioDefect: Double = 0.0,
    val urutanTampil: Int = 0
)

data class DraftInputDefectSlot(
    val id: String,
    val inputPartId: String,
    val relasiPartDefectId: String,
    val slotWaktuId: String,
    val namaDefect: String = "",
    val jumlahDefect: Int = 0
)
