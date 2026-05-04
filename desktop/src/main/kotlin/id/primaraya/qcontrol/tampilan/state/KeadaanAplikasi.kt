package id.primaraya.qcontrol.tampilan.state

import id.primaraya.qcontrol.konfigurasi.KonfigurasiAplikasi
import id.primaraya.qcontrol.ranah.model.StatusKesehatanServer
import id.primaraya.qcontrol.tampilan.navigasi.RuteAplikasi

import id.primaraya.qcontrol.ranah.model.InformasiDatabaseLokal
import id.primaraya.qcontrol.ranah.model.KonfigurasiLokal
import id.primaraya.qcontrol.ranah.model.RingkasanOutboxSinkronisasi

data class KeadaanAplikasi(
    val ruteAktif: RuteAplikasi = RuteAplikasi.Dashboard,
    val daftarRute: List<RuteAplikasi> = RuteAplikasi.dapatkanDaftarRute(),
    val statusKoneksi: StatusKoneksiServer = StatusKoneksiServer.TidakDiperiksa,
    val pesanStatusKoneksi: String = "",
    val statusKesehatanServer: StatusKesehatanServer? = null,
    val konfigurasiLokal: KonfigurasiLokal? = null,
    val statusDatabaseLokal: StatusPenyimpananLokal = StatusPenyimpananLokal.TidakDiperiksa,
    val pesanStatusDatabaseLokal: String? = null,
    val informasiDatabaseLokal: InformasiDatabaseLokal? = null,
    val ringkasanOutboxSinkronisasi: RingkasanOutboxSinkronisasi? = null,
    val statusRingkasanOutbox: StatusRingkasanOutbox = StatusRingkasanOutbox.TidakDimuat,
    val pesanRingkasanOutbox: String? = null,
    val namaPengguna: String = "Wahyu",
    val peranPengguna: String = "QC Inspector",
    val lineAktif: String = KonfigurasiAplikasi.LINE_DEFAULT,
    val sinkronisasiOtomatisAktif: Boolean = false,
    val sedangSinkronisasi: Boolean = false,
    val pesanSinkronisasiTerakhir: String? = null,
    val waktuSinkronisasiTerakhir: String? = null,
    val sedangMengujiUlangIdempotency: Boolean = false,
    val pesanUjiUlangIdempotency: String? = null,
    
    // Autentikasi (Fase 2C)
    val sesiAktif: id.primaraya.qcontrol.ranah.model.Autentikasi? = null,
    val sedangLogin: Boolean = false,
    val pesanLogin: String? = null
)

sealed class StatusRingkasanOutbox {
    object TidakDimuat : StatusRingkasanOutbox()
    object Memuat : StatusRingkasanOutbox()
    object Berhasil : StatusRingkasanOutbox()
    object Gagal : StatusRingkasanOutbox()
}

sealed class StatusKoneksiServer {
    object TidakDiperiksa : StatusKoneksiServer()
    object Memeriksa : StatusKoneksiServer()
    object Tersambung : StatusKoneksiServer()
    object Terputus : StatusKoneksiServer()
}

sealed class StatusPenyimpananLokal {
    object TidakDiperiksa : StatusPenyimpananLokal()
    object Memeriksa : StatusPenyimpananLokal()
    object Tersedia : StatusPenyimpananLokal()
    object Gagal : StatusPenyimpananLokal()
}

