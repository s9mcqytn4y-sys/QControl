package id.primaraya.qcontrol.tampilan.state

import id.primaraya.qcontrol.konfigurasi.KonfigurasiAplikasi
import id.primaraya.qcontrol.tampilan.navigasi.RuteAplikasi

data class KeadaanAplikasi(
    val ruteAktif: RuteAplikasi = RuteAplikasi.Dashboard,
    val daftarRute: List<RuteAplikasi> = RuteAplikasi.dapatkanDaftarRute(),
    val statusKoneksi: StatusKoneksiServer = StatusKoneksiServer.TidakDiperiksa,
    val namaPengguna: String = "Wahyu",
    val peranPengguna: String = "QC Inspector",
    val lineAktif: String = KonfigurasiAplikasi.LINE_DEFAULT
)

enum class StatusKoneksiServer {
    TidakDiperiksa,
    Tersambung,
    Terputus
}
