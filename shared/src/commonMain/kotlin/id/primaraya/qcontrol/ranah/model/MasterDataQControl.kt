package id.primaraya.qcontrol.ranah.model

data class MasterDataQControl(
    val versiMasterData: String,
    val lineProduksi: List<LineProduksi> = emptyList(),
    val slotWaktu: List<SlotWaktu> = emptyList(),
    val material: List<Material> = emptyList(),
    val part: List<Part> = emptyList(),
    val kategoriDefect: List<KategoriDefect> = emptyList(),
    val jenisDefect: List<JenisDefect> = emptyList(),
    val relasiPartDefect: List<RelasiPartDefect> = emptyList(),
    val jumlahShiftOperasional: Int = 1
)
