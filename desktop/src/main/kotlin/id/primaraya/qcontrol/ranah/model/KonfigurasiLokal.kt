package id.primaraya.qcontrol.ranah.model

data class KonfigurasiLokal(
    val urlServer: String = "http://localhost:8000",
    val lineAktif: String = "PRESS",
    val namaPenggunaTerakhir: String = "Wahyu",
    val peranPenggunaTerakhir: String = "QC Inspector"
)
