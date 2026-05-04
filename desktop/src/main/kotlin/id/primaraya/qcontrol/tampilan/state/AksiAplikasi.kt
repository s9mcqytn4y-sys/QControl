package id.primaraya.qcontrol.tampilan.state

import id.primaraya.qcontrol.tampilan.navigasi.RuteAplikasi

sealed class AksiAplikasi {
    data class PilihRute(val rute: RuteAplikasi) : AksiAplikasi()
    data class GantiLineAktif(val line: String) : AksiAplikasi()
    object PeriksaKoneksiServer : AksiAplikasi()
    object PeriksaDatabaseLokal : AksiAplikasi()
    object MuatKonfigurasiLokal : AksiAplikasi()
    object MuatRingkasanOutboxSinkronisasi : AksiAplikasi()
    object BuatContohItemOutboxUntukPengujian : AksiAplikasi()
    object SinkronkanOutboxSekarang : AksiAplikasi()
    object AktifkanSinkronisasiOtomatis : AksiAplikasi()
    object NonaktifkanSinkronisasiOtomatis : AksiAplikasi()
    object ResetOutboxSedangDikirim : AksiAplikasi()
    object UjiUlangIdempotency : AksiAplikasi()
    
    // Autentikasi (Fase 2C)
    data class Login(val username: String, val kataSandi: String) : AksiAplikasi()
    object Logout : AksiAplikasi()
    object InisialisasiSesi : AksiAplikasi()
}
