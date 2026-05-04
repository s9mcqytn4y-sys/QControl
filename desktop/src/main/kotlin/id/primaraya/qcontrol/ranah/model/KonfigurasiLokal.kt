package id.primaraya.qcontrol.ranah.model

import id.primaraya.qcontrol.konfigurasi.KonfigurasiPeran

data class KonfigurasiLokal(
    val urlServer: String = "http://localhost:8000",
    val lineAktif: String = "PRESS",
    val namaPenggunaTerakhir: String = "Wahyu",
    val peranPenggunaTerakhir: String = KonfigurasiPeran.HEAD_QC
)
