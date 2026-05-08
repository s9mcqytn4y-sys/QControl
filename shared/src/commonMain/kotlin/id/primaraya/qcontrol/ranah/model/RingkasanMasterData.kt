package id.primaraya.qcontrol.ranah.model

data class RingkasanMasterData(
    val versiMasterData: String,
    val ditarikPada: String,
    val jumlahLineProduksi: Int,
    val jumlahSlotWaktu: Int,
    val jumlahMaterial: Int,
    val jumlahPart: Int,
    val jumlahJenisDefect: Int,
    val jumlahRelasiPartDefect: Int,
    val jumlahShiftOperasional: Int
)
