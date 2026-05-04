package id.primaraya.qcontrol.ranah.model

import id.primaraya.qcontrol.konfigurasi.KonfigurasiAplikasi
import id.primaraya.qcontrol.konfigurasi.KonfigurasiPeran

data class KonfigurasiLokal(
    val urlServer: String = KonfigurasiAplikasi.URL_SERVER_DEFAULT,
    val lineAktif: String = KonfigurasiAplikasi.LINE_DEFAULT,
    val namaPenggunaTerakhir: String = "Wahyu",
    val peranPenggunaTerakhir: String = KonfigurasiPeran.HEAD_QC
)
