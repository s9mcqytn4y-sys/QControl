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
    data class Login(val email: String, val kataSandi: String) : AksiAplikasi()
    object Logout : AksiAplikasi()
    object InisialisasiSesi : AksiAplikasi()

    // Master Data (Fase 2D-R2)
    object TarikMasterDataDariServer : AksiAplikasi()
    object MuatMasterDataLokal : AksiAplikasi()
    data class PilihTabMasterData(val tab: TabMasterData) : AksiAplikasi()
    data class UbahKataKunciMasterData(val kataKunci: String) : AksiAplikasi()
    data class PilihPartMaster(val part: id.primaraya.qcontrol.ranah.model.Part?) : AksiAplikasi()
    data class PilihPartMasterUntukTemplate(val part: id.primaraya.qcontrol.ranah.model.Part?) : AksiAplikasi()
    data class MuatTemplateDefectPart(val partId: String) : AksiAplikasi()

    // Input Harian (Fase 2E-A)
    data class MuatDraftInputHarian(val tanggal: String, val lineId: String) : AksiAplikasi()
    data class UbahTanggalDraftHarian(val tanggal: String) : AksiAplikasi()
    data class UbahKataKunciInputPart(val kataKunci: String) : AksiAplikasi()
    data class PilihInputPart(val part: id.primaraya.qcontrol.ranah.model.DraftInputPart?) : AksiAplikasi()
    data class UpdateQtyCheckInputPart(val partId: String, val qty: Int) : AksiAplikasi()
    data class UpdateDefectSlot(val partId: String, val slotId: String, val defectId: String, val qty: Int) : AksiAplikasi()
    object ResetDraftInputHarian : AksiAplikasi()
    object MuatUlangDataLokal : AksiAplikasi()

    // Flash Message (Fase 2E-C)
    object BersihkanPesanFlash : AksiAplikasi()
    data class TampilkanPesanFlash(val pesan: String, val tipe: TipePesanFlash) : AksiAplikasi()
}
