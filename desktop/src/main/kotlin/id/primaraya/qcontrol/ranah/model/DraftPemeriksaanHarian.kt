package id.primaraya.qcontrol.ranah.model

data class DraftPemeriksaanHarian(
    val id: String,
    val tanggalProduksi: String,
    val lineId: String,
    val catatan: String?,
    val dibuatPada: String,
    val diperbaruiPada: String
)
