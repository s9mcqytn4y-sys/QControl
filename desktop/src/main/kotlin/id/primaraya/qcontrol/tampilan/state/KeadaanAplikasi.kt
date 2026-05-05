package id.primaraya.qcontrol.tampilan.state

import id.primaraya.qcontrol.konfigurasi.KonfigurasiAplikasi
import id.primaraya.qcontrol.ranah.model.StatusKesehatanServer
import id.primaraya.qcontrol.tampilan.navigasi.RuteAplikasi

import id.primaraya.qcontrol.ranah.model.InformasiDatabaseLokal
import id.primaraya.qcontrol.ranah.model.KonfigurasiLokal
import id.primaraya.qcontrol.ranah.model.RingkasanOutboxSinkronisasi

import id.primaraya.qcontrol.konfigurasi.KonfigurasiPeran

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
    val peranPengguna: String = KonfigurasiPeran.HEAD_QC,
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
    val pesanLogin: String? = null,

    // Master Data (Fase 2D-R2)
    val sedangMenarikMasterData: Boolean = false,
    val pesanMasterData: String? = null,
    val ringkasanMasterData: id.primaraya.qcontrol.ranah.model.RingkasanMasterData? = null,
    val daftarPartMaster: List<id.primaraya.qcontrol.ranah.model.Part> = emptyList(),
    val daftarJenisDefectMaster: List<id.primaraya.qcontrol.ranah.model.JenisDefect> = emptyList(),
    val daftarMaterialMaster: List<id.primaraya.qcontrol.ranah.model.Material> = emptyList(),
    val daftarSlotWaktuMaster: List<id.primaraya.qcontrol.ranah.model.SlotWaktu> = emptyList(),
    val daftarLineProduksiMaster: List<id.primaraya.qcontrol.ranah.model.LineProduksi> = emptyList(),
    val daftarRelasiPartDefectMaster: List<id.primaraya.qcontrol.ranah.model.RelasiPartDefect> = emptyList(),
    val partMasterTerpilih: id.primaraya.qcontrol.ranah.model.Part? = null,
    val kataKunciMasterData: String = "",
    val tabMasterDataAktif: TabMasterData = TabMasterData.RINGKASAN,
    val daftarTemplateDefectPart: List<id.primaraya.qcontrol.ranah.model.TemplateDefectPart> = emptyList(),
    val pesanTemplateDefectPart: String? = null,

    // Input Harian (Fase 2E-A)
    val sedangMemuatInputHarian: Boolean = false,
    val pesanInputHarian: String? = null,
    val draftPemeriksaanHarian: id.primaraya.qcontrol.ranah.model.DraftPemeriksaanHarian? = null,
    val daftarInputPartDraft: List<id.primaraya.qcontrol.ranah.model.DraftInputPart> = emptyList(),
    val inputPartTerpilih: id.primaraya.qcontrol.ranah.model.DraftInputPart? = null,
    val matrixInputDefectPart: id.primaraya.qcontrol.ranah.model.MatrixInputDefectPart? = null,
    val daftarDefectSlotDraft: List<id.primaraya.qcontrol.ranah.model.DraftInputDefectSlot> = emptyList(),
    val ringkasanInputHarian: id.primaraya.qcontrol.ranah.model.RingkasanInputHarian? = null,
    val tanggalPemeriksaanHarian: String = java.time.LocalDate.now().toString(),
    val kataKunciPartInputHarian: String = "",
    val pesanValidasiInputHarian: String? = null,
    val masterDataLokalTersedia: Boolean = false,
    val sesiHeadQCTidakValid: Boolean = false,
    val pesanKesiapanInputHarian: String? = null
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

enum class TabMasterData(val label: String) {
    RINGKASAN("Ringkasan"),
    PART("Part"),
    JENIS_DEFECT("Jenis Defect"),
    MATERIAL("Material"),
    SLOT_WAKTU("Slot Waktu"),
    LINE_PRODUKSI("Line Produksi")
}
