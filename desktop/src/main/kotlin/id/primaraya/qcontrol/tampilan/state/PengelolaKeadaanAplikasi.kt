package id.primaraya.qcontrol.tampilan.state

import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.ranah.usecase.PeriksaKesehatanServerUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import id.primaraya.qcontrol.ranah.usecase.*

import kotlinx.coroutines.flow.collect
import id.primaraya.qcontrol.ranah.usecase.BacaKonfigurasiLokalUseCase
import id.primaraya.qcontrol.ranah.usecase.PeriksaDatabaseLokalUseCase
import id.primaraya.qcontrol.ranah.usecase.BuatItemOutboxSinkronisasiUseCase
import id.primaraya.qcontrol.ranah.usecase.BacaRingkasanOutboxSinkronisasiUseCase
import id.primaraya.qcontrol.ranah.usecase.ResetOutboxSedangDikirimUseCase
import id.primaraya.qcontrol.ranah.model.MetodeHttpSinkronisasi
import id.primaraya.qcontrol.ranah.usecase.MasukSesiUseCase
import id.primaraya.qcontrol.ranah.usecase.KeluarSesiUseCase
import id.primaraya.qcontrol.ranah.usecase.AmbilSesiAktifUseCase
import id.primaraya.qcontrol.ranah.model.Autentikasi
import id.primaraya.qcontrol.ranah.usecase.UjiUlangOutboxBerhasilTerakhirUseCase
import id.primaraya.qcontrol.ranah.usecase.TarikMasterDataQControlUseCase
import id.primaraya.qcontrol.ranah.usecase.BacaRingkasanMasterDataUseCase
import id.primaraya.qcontrol.ranah.usecase.BacaDaftarPartMasterUseCase
import id.primaraya.qcontrol.ranah.usecase.BacaDaftarJenisDefectMasterUseCase
import id.primaraya.qcontrol.ranah.usecase.BacaDaftarMaterialMasterUseCase
import id.primaraya.qcontrol.ranah.usecase.BacaDaftarSlotWaktuMasterUseCase
import id.primaraya.qcontrol.ranah.usecase.BacaDaftarLineProduksiMasterUseCase

class PengelolaKeadaanAplikasi(
    private val periksaKesehatanServerUseCase: PeriksaKesehatanServerUseCase,
    private val periksaDatabaseLokalUseCase: PeriksaDatabaseLokalUseCase,
    private val bacaKonfigurasiLokalUseCase: BacaKonfigurasiLokalUseCase,
    private val buatItemOutboxSinkronisasiUseCase: BuatItemOutboxSinkronisasiUseCase,
    private val bacaRingkasanOutboxSinkronisasiUseCase: BacaRingkasanOutboxSinkronisasiUseCase,
    private val resetOutboxSedangDikirimUseCase: ResetOutboxSedangDikirimUseCase,
    private val ujiUlangOutboxBerhasilTerakhirUseCase: UjiUlangOutboxBerhasilTerakhirUseCase,
    private val masukSesiUseCase: MasukSesiUseCase,
    private val keluarSesiUseCase: KeluarSesiUseCase,
    private val ambilSesiAktifUseCase: AmbilSesiAktifUseCase,
    private val pengelolaSinkronisasi: PengelolaSinkronisasi,
    private val tarikMasterDataUseCase: TarikMasterDataQControlUseCase,
    private val bacaRingkasanMasterDataUseCase: BacaRingkasanMasterDataUseCase,
    private val bacaDaftarPartMasterUseCase: BacaDaftarPartMasterUseCase,
    private val bacaDaftarJenisDefectMasterUseCase: BacaDaftarJenisDefectMasterUseCase,
    private val bacaDaftarMaterialMasterUseCase: BacaDaftarMaterialMasterUseCase,
    private val bacaDaftarSlotWaktuMasterUseCase: BacaDaftarSlotWaktuMasterUseCase,
    private val bacaDaftarLineProduksiMasterUseCase: BacaDaftarLineProduksiMasterUseCase,
    private val bacaRelasiPartDefectMasterUseCase: BacaRelasiPartDefectMasterUseCase,
    private val bacaTemplateDefectPartUseCase: BacaTemplateDefectPartUseCase,
    private val kelolaInputHarianUseCase: KelolaInputHarianUseCase,
    private val kirimPemeriksaanHarianUseCase: KirimPemeriksaanHarianUseCase,
    private val bacaDiagnostikMasterDataUseCase: BacaDiagnostikMasterDataUseCase,
    private val lingkup: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    private val _keadaan = MutableStateFlow(KeadaanAplikasi())
    val keadaan: StateFlow<KeadaanAplikasi> = _keadaan.asStateFlow()

    // Diagnostik (Fase 2G-Bugfix)
    private val _diagnostikMasterData = MutableStateFlow<Map<String, Int>>(emptyMap())
    val diagnostikMasterData: StateFlow<Map<String, Int>> = _diagnostikMasterData.asStateFlow()

    init {
        // Amati status dari pengelola sinkronisasi
        lingkup.launch {
            pengelolaSinkronisasi.sedangSinkronisasi.collect { sedang ->
                _keadaan.update { it.copy(sedangSinkronisasi = sedang) }
            }
        }
        lingkup.launch {
            pengelolaSinkronisasi.pesanSinkronisasiTerakhir.collect { pesan ->
                _keadaan.update { it.copy(pesanSinkronisasiTerakhir = pesan) }
            }
        }
        lingkup.launch {
            pengelolaSinkronisasi.waktuSinkronisasiTerakhir.collect { waktu ->
                _keadaan.update { it.copy(waktuSinkronisasiTerakhir = waktu) }
            }
        }
        lingkup.launch {
            pengelolaSinkronisasi.sinkronisasiOtomatisAktif.collect { aktif ->
                _keadaan.update { it.copy(sinkronisasiOtomatisAktif = aktif) }
            }
        }

        // Muat data awal
        muatKonfigurasiLokal()
        periksaKoneksi()
        muatRingkasanOutbox()
        periksaSesiAktif()
        muatMasterDataLokal()
        muatDiagnostik()
    }

    private fun muatDiagnostik() {
        _diagnostikMasterData.value = bacaDiagnostikMasterDataUseCase.eksekusi()
    }

    fun tangani(aksi: AksiAplikasi) {
        when (aksi) {
            is AksiAplikasi.PilihRute -> {
                _keadaan.update { it.copy(ruteAktif = aksi.rute) }
            }
            is AksiAplikasi.GantiLineAktif -> {
                _keadaan.update { it.copy(
                    lineAktifId = aksi.line.id,
                    kodeLineAktif = aksi.line.kodeLine,
                    namaLineAktif = aksi.line.namaLine,
                    lineAktif = aksi.line.namaLine, // Sync deprecated
                    inputPartTerpilih = null,
                    matrixInputDefectPart = null,
                    kataKunciPartInputHarian = "" // Reset search
                ) }
                muatDraftInputHarian(_keadaan.value.tanggalPemeriksaanHarian, aksi.line.id)
                tangani(AksiAplikasi.TampilkanPesanFlash("Line Produksi diubah ke ${aksi.line.namaLine}", TipePesanFlash.INFO))
            }
            is AksiAplikasi.PeriksaKoneksiServer -> {
                periksaKoneksi()
            }
            is AksiAplikasi.PeriksaDatabaseLokal -> {
                periksaDatabaseLokal()
            }
            is AksiAplikasi.MuatKonfigurasiLokal -> {
                muatKonfigurasiLokal()
            }
            is AksiAplikasi.MuatRingkasanOutboxSinkronisasi -> {
                muatRingkasanOutbox()
            }
            is AksiAplikasi.BuatContohItemOutboxUntukPengujian -> {
                buatContohOutbox()
            }
            is AksiAplikasi.SinkronkanOutboxSekarang -> {
                sinkronkanSekarang()
            }
            is AksiAplikasi.ResetOutboxSedangDikirim -> {
                resetOutboxStuck()
            }
            is AksiAplikasi.AktifkanSinkronisasiOtomatis -> {
                pengelolaSinkronisasi.mulaiSinkronisasiOtomatis()
            }
            is AksiAplikasi.NonaktifkanSinkronisasiOtomatis -> {
                pengelolaSinkronisasi.hentikanSinkronisasiOtomatis()
            }
            is AksiAplikasi.UjiUlangIdempotency -> {
                ujiUlangIdempotency()
            }
            is AksiAplikasi.Login -> {
                login(aksi.email, aksi.kataSandi)
            }
            is AksiAplikasi.Logout -> {
                logout()
            }
            is AksiAplikasi.InisialisasiSesi -> {
                periksaSesiAktif()
            }
            is AksiAplikasi.TarikMasterDataDariServer -> {
                tarikMasterDataDariServer()
            }
            is AksiAplikasi.MuatMasterDataLokal -> {
                muatMasterDataLokal()
            }
            is AksiAplikasi.PilihTabMasterData -> {
                _keadaan.update { it.copy(tabMasterDataAktif = aksi.tab) }
                muatDaftarTabMasterData(aksi.tab)
            }
            is AksiAplikasi.UbahKataKunciMasterData -> {
                _keadaan.update { it.copy(kataKunciMasterData = aksi.kataKunci) }
                muatDaftarTabMasterData(_keadaan.value.tabMasterDataAktif, aksi.kataKunci)
            }
            is AksiAplikasi.PilihPartMaster -> {
                pilihPartMaster(aksi.part)
            }
            is AksiAplikasi.PilihPartMasterUntukTemplate -> {
                pilihPartMaster(aksi.part)
            }
            is AksiAplikasi.MuatTemplateDefectPart -> {
                muatTemplateDefectPart(aksi.partId)
            }
            is AksiAplikasi.MuatDraftInputHarian -> {
                muatDraftInputHarian(aksi.tanggal, aksi.lineId)
            }
            is AksiAplikasi.UbahTanggalDraftHarian -> {
                _keadaan.update { it.copy(tanggalPemeriksaanHarian = aksi.tanggal) }
                muatDraftInputHarian(aksi.tanggal, _keadaan.value.lineAktif)
            }
            is AksiAplikasi.UbahKataKunciInputPart -> {
                _keadaan.update { it.copy(kataKunciPartInputHarian = aksi.kataKunci) }
                muatDaftarInputPart(aksi.kataKunci)
            }
            is AksiAplikasi.PilihInputPart -> {
                pilihInputPart(aksi.part)
            }
            is AksiAplikasi.UpdateQtyCheckInputPart -> {
                updateQtyCheck(aksi.partId, aksi.qty)
            }
            is AksiAplikasi.UpdateDefectSlot -> {
                updateDefectSlot(aksi.partId, aksi.slotId, aksi.defectId, aksi.qty)
            }
            is AksiAplikasi.ResetDraftInputHarian -> {
                resetDraftInputHarian()
            }
            is AksiAplikasi.KirimKeServer -> {
                kirimKeServer()
            }
            is AksiAplikasi.MuatUlangDataLokal -> {
                muatMasterDataLokal()
                muatKonfigurasiLokal()
                tangani(AksiAplikasi.TampilkanPesanFlash("Data lokal berhasil dimuat ulang", TipePesanFlash.SUKSES))
            }
            is AksiAplikasi.BersihkanPesanFlash -> {
                _keadaan.update { it.copy(pesanFlash = null) }
            }
            is AksiAplikasi.TampilkanPesanFlash -> {
                _keadaan.update { it.copy(pesanFlash = PesanFlash(aksi.pesan, aksi.tipe)) }
            }
        }
    }

    private fun ujiUlangIdempotency() {
        _keadaan.update { 
            it.copy(
                sedangMengujiUlangIdempotency = true,
                pesanUjiUlangIdempotency = "Mengirim ulang item sukses terakhir..."
            ) 
        }
        
        lingkup.launch {
            when (val hasil = ujiUlangOutboxBerhasilTerakhirUseCase()) {
                is HasilOperasi.Berhasil<*> -> {
                    _keadaan.update { 
                        it.copy(
                            sedangMengujiUlangIdempotency = false,
                            pesanUjiUlangIdempotency = "Respons Server: ${hasil.data as? String}"
                        )
                    }
                }
                is HasilOperasi.Gagal -> {
                    _keadaan.update { 
                        it.copy(
                            sedangMengujiUlangIdempotency = false,
                            pesanUjiUlangIdempotency = "Uji Gagal: ${hasil.kesalahan.pesan}"
                        )
                    }
                }
            }
        }
    }

    private fun resetOutboxStuck() {
        lingkup.launch {
            resetOutboxSedangDikirimUseCase()
            muatRingkasanOutbox()
            tangani(AksiAplikasi.TampilkanPesanFlash("Status antrean berhasil direset", TipePesanFlash.INFO))
        }
    }

    private fun sinkronkanSekarang() {
        lingkup.launch {
            // Selalu reset yang stuck sebelum sinkronisasi manual untuk keamanan
            resetOutboxSedangDikirimUseCase()
            pengelolaSinkronisasi.sinkronkanSekarang()
            muatRingkasanOutbox()
            tangani(AksiAplikasi.TampilkanPesanFlash("Sinkronisasi manual selesai", TipePesanFlash.SUKSES))
        }
    }

    private fun muatRingkasanOutbox() {
        _keadaan.update { 
            it.copy(
                statusRingkasanOutbox = StatusRingkasanOutbox.Memuat,
                pesanRingkasanOutbox = "Memuat ringkasan outbox..."
            ) 
        }
        
        lingkup.launch {
            when (val hasil = bacaRingkasanOutboxSinkronisasiUseCase()) {
                is HasilOperasi.Berhasil<*> -> {
                    val ringkasan = hasil.data as id.primaraya.qcontrol.ranah.model.RingkasanOutboxSinkronisasi
                    _keadaan.update { 
                        it.copy(
                            statusRingkasanOutbox = StatusRingkasanOutbox.Berhasil,
                            ringkasanOutboxSinkronisasi = ringkasan,
                            pesanRingkasanOutbox = null
                        )
                    }
                }
                is HasilOperasi.Gagal -> {
                    _keadaan.update { 
                        it.copy(
                            statusRingkasanOutbox = StatusRingkasanOutbox.Gagal,
                            pesanRingkasanOutbox = hasil.kesalahan.pesan
                        )
                    }
                }
            }
        }
    }

    private fun buatContohOutbox() {
        lingkup.launch {
            val payload = """
                {
                  "contoh": true,
                  "sumber": "fase_2a_r2"
                }
            """.trimIndent()
            
            val hasil = buatItemOutboxSinkronisasiUseCase(
                jenisOperasi = "CONTOH_PENGUJIAN_OUTBOX",
                endpointTujuan = "/api/v1/qcontrol/contoh",
                metodeHttp = MetodeHttpSinkronisasi.POST,
                payloadJson = payload
            )
            
            when (hasil) {
                is HasilOperasi.Berhasil<*> -> {
                    muatRingkasanOutbox()
                }
                is HasilOperasi.Gagal -> {
                    _keadaan.update { 
                        it.copy(
                            statusRingkasanOutbox = StatusRingkasanOutbox.Gagal,
                            pesanRingkasanOutbox = "Gagal membuat contoh: ${hasil.kesalahan.pesan}"
                        )
                    }
                }
            }
        }
    }

    private fun muatKonfigurasiLokal() {
        lingkup.launch {
            when (val hasil = bacaKonfigurasiLokalUseCase()) {
                is HasilOperasi.Berhasil<*> -> {
                    _keadaan.update { 
                        it.copy(
                            konfigurasiLokal = hasil.data as id.primaraya.qcontrol.ranah.model.KonfigurasiLokal,
                            lineAktif = hasil.data.lineAktif,
                            namaPengguna = hasil.data.namaPenggunaTerakhir,
                            peranPengguna = hasil.data.peranPenggunaTerakhir
                        )
                    }
                }
                is HasilOperasi.Gagal -> {
                    // Biarkan pakai default
                }
            }
        }
    }

    private fun periksaDatabaseLokal() {
        _keadaan.update {
            it.copy(
                statusDatabaseLokal = StatusPenyimpananLokal.Memeriksa,
                pesanStatusDatabaseLokal = "Memeriksa database lokal..."
            )
        }

        lingkup.launch {
            when (val hasil = periksaDatabaseLokalUseCase()) {
                is HasilOperasi.Berhasil<*> -> {
                    val info = hasil.data as id.primaraya.qcontrol.ranah.model.InformasiDatabaseLokal
                    _keadaan.update {
                        it.copy(
                            statusDatabaseLokal = StatusPenyimpananLokal.Tersedia,
                            informasiDatabaseLokal = info,
                            pesanStatusDatabaseLokal = info.pesan
                        )
                    }
                }
                is HasilOperasi.Gagal -> {
                    _keadaan.update {
                        it.copy(
                            statusDatabaseLokal = StatusPenyimpananLokal.Gagal,
                            informasiDatabaseLokal = null,
                            pesanStatusDatabaseLokal = hasil.kesalahan.pesan
                        )
                    }
                }
            }
        }
    }

    private fun periksaKoneksi() {
        _keadaan.update { 
            it.copy(
                statusKoneksi = StatusKoneksiServer.Memeriksa,
                pesanStatusKoneksi = "Memeriksa koneksi server..."
            ) 
        }
        
        lingkup.launch {
            when (val hasil = periksaKesehatanServerUseCase()) {
                is HasilOperasi.Berhasil<*> -> {
                    val status = hasil.data as id.primaraya.qcontrol.ranah.model.StatusKesehatanServer
                    _keadaan.update { 
                        it.copy(
                            statusKoneksi = StatusKoneksiServer.Tersambung,
                            statusKesehatanServer = status,
                            pesanStatusKoneksi = "Tersambung ke ${status.namaAplikasi}"
                        )
                    }
                }
                is HasilOperasi.Gagal -> {
                    _keadaan.update { 
                        it.copy(
                            statusKoneksi = StatusKoneksiServer.Terputus,
                            statusKesehatanServer = null,
                            pesanStatusKoneksi = hasil.kesalahan.pesan
                        )
                    }
                }
            }
        }
    }

    private fun periksaSesiAktif() {
        lingkup.launch {
            when (val hasil = ambilSesiAktifUseCase.eksekusi()) {
                is HasilOperasi.Berhasil<*> -> {
                    val sesi = hasil.data as? Autentikasi
                    
                    // Validasi Role: Hanya HeadQC yang diizinkan di QControl Desktop
                    if (sesi != null && sesi.peran != "HeadQC") {
                        logout() // Paksa keluar jika role tidak valid
                        return@launch
                    }

                    _keadaan.update { it.copy(sesiAktif = sesi) }
                    pengelolaSinkronisasi.tokenAktif = sesi?.token
                }
                is HasilOperasi.Gagal -> {
                    // Abaikan jika gagal baca lokal
                }
            }
        }
    }

    private fun login(email: String, kataSandi: String) {
        _keadaan.update { it.copy(sedangLogin = true, pesanLogin = "Sedang masuk...") }
        
        lingkup.launch {
            when (val hasil = masukSesiUseCase.eksekusi(email, kataSandi)) {
                is HasilOperasi.Berhasil<*> -> {
                    val sesi = hasil.data as Autentikasi
                    
                    // Validasi Role: QControl Desktop hanya untuk HeadQC
                    if (sesi.peran != "HeadQC") {
                        _keadaan.update { 
                            it.copy(
                                sedangLogin = false,
                                pesanLogin = "Akses ditolak: Hanya HeadQC yang dapat masuk ke QControl Desktop."
                            ) 
                        }
                        tangani(AksiAplikasi.TampilkanPesanFlash("Akses ditolak: Role Anda bukan HeadQC", TipePesanFlash.ERROR))
                        keluarSesiUseCase.eksekusi() // Bersihkan sesi yang mungkin sudah tersimpan
                        return@launch
                    }

                    _keadaan.update { 
                        it.copy(
                            sedangLogin = false,
                            sesiAktif = sesi,
                            pesanLogin = null
                        ) 
                    }
                    pengelolaSinkronisasi.tokenAktif = sesi.token
                    tangani(AksiAplikasi.TampilkanPesanFlash("Selamat datang, ${sesi.namaPengguna}!", TipePesanFlash.SUKSES))
                }
                is HasilOperasi.Gagal -> {
                    _keadaan.update { 
                        it.copy(
                            sedangLogin = false,
                            pesanLogin = hasil.kesalahan.pesan
                        )
                    }
                    tangani(AksiAplikasi.TampilkanPesanFlash("Gagal masuk: ${hasil.kesalahan.pesan}", TipePesanFlash.ERROR))
                }
            }
        }
    }

    private fun logout() {
        lingkup.launch {
            keluarSesiUseCase.eksekusi()
            _keadaan.update { it.copy(sesiAktif = null) }
            pengelolaSinkronisasi.tokenAktif = null
            tangani(AksiAplikasi.TampilkanPesanFlash("Sesi HeadQC telah diakhiri", TipePesanFlash.INFO))
        }
    }

    private fun tarikMasterDataDariServer() {
        _keadaan.update {
            it.copy(
                sedangMenarikMasterData = true,
                pesanMasterData = "Menarik master data dari PGNServer...",
                sesiHeadQCTidakValid = false
            )
        }
        lingkup.launch {
            when (val hasil = tarikMasterDataUseCase.eksekusi()) {
                is HasilOperasi.Berhasil<*> -> {
                    val ringkasan = hasil.data as id.primaraya.qcontrol.ranah.model.RingkasanMasterData
                    _keadaan.update {
                        it.copy(
                            sedangMenarikMasterData = false,
                            pesanMasterData = "Master data berhasil diperbarui.",
                            ringkasanMasterData = ringkasan,
                            masterDataLokalTersedia = true
                        )
                    }
                    tangani(AksiAplikasi.TampilkanPesanFlash("Master data berhasil ditarik dari server", TipePesanFlash.SUKSES))
                    
                    // REFRESH SETELAH TARIK MASTER DATA (TASK 4)
                    muatMasterDataLokal()
                    muatDiagnostik()
                    
                    // Validasi line aktif
                    val lineValid = _keadaan.value.daftarLineProduksiMaster.find { it.id == _keadaan.value.lineAktifId }
                        ?: _keadaan.value.daftarLineProduksiMaster.firstOrNull()
                    
                    lineValid?.let { line ->
                        _keadaan.update { it.copy(
                            lineAktifId = line.id,
                            kodeLineAktif = line.kodeLine,
                            namaLineAktif = line.namaLine,
                            lineAktif = line.namaLine
                        ) }
                        muatDraftInputHarian(_keadaan.value.tanggalPemeriksaanHarian, line.id)
                    }

                    muatDaftarTabMasterData(_keadaan.value.tabMasterDataAktif)
                }
                is HasilOperasi.Gagal -> {
                    val pesan = hasil.kesalahan.pesan
                    val sesiInvalid = pesan.contains("401") || pesan.contains("Unauthorized", ignoreCase = true) || pesan.contains("Sesi", ignoreCase = true)
                    
                    _keadaan.update {
                        it.copy(
                            sedangMenarikMasterData = false,
                            pesanMasterData = pesan,
                            sesiHeadQCTidakValid = sesiInvalid
                        )
                    }
                    
                    if (sesiInvalid) {
                        tangani(AksiAplikasi.TampilkanPesanFlash("Sesi berakhir, silakan login ulang", TipePesanFlash.PERINGATAN))
                    } else {
                        tangani(AksiAplikasi.TampilkanPesanFlash("Gagal tarik data: $pesan", TipePesanFlash.ERROR))
                    }
                }
            }
        }
    }

    private fun muatMasterDataLokal() {
        lingkup.launch {
            when (val hasil = bacaRingkasanMasterDataUseCase.eksekusi()) {
                is HasilOperasi.Berhasil<*> -> {
                    val ringkasan = hasil.data as id.primaraya.qcontrol.ranah.model.RingkasanMasterData
                    _keadaan.update { it.copy(ringkasanMasterData = ringkasan, pesanMasterData = null, masterDataLokalTersedia = true) }
                    
                    // Muat data pendukung (TASK 5)
                    muatDaftarLineProduksi()
                    muatDaftarSlotWaktu()
                    
                    // Validasi dan muat draft awal jika sudah ada line
                    val lineId = _keadaan.value.lineAktifId
                    if (!lineId.isNullOrEmpty()) {
                        muatDraftInputHarian(_keadaan.value.tanggalPemeriksaanHarian, lineId)
                    } else {
                        // Jika belum ada line aktif (start awal), coba ambil dari master
                        _keadaan.value.daftarLineProduksiMaster.firstOrNull()?.let { firstLine ->
                            _keadaan.update { it.copy(
                                lineAktifId = firstLine.id,
                                kodeLineAktif = firstLine.kodeLine,
                                namaLineAktif = firstLine.namaLine,
                                lineAktif = firstLine.namaLine
                            ) }
                            muatDraftInputHarian(_keadaan.value.tanggalPemeriksaanHarian, firstLine.id)
                        }
                    }

                    muatDaftarTabMasterData(_keadaan.value.tabMasterDataAktif)
                }
                is HasilOperasi.Gagal -> {
                    _keadaan.update { it.copy(ringkasanMasterData = null, masterDataLokalTersedia = false) }
                }
            }
        }
    }

    private suspend fun muatDaftarLineProduksi() {
        when (val hasil = bacaDaftarLineProduksiMasterUseCase.eksekusi()) {
            is HasilOperasi.Berhasil<*> -> {
                _keadaan.update { it.copy(daftarLineProduksiMaster = hasil.data as List<id.primaraya.qcontrol.ranah.model.LineProduksi>) }
            }
            is HasilOperasi.Gagal -> {}
        }
    }

    private suspend fun muatDaftarSlotWaktu() {
        when (val hasil = bacaDaftarSlotWaktuMasterUseCase.eksekusi()) {
            is HasilOperasi.Berhasil<*> -> {
                _keadaan.update { it.copy(daftarSlotWaktuMaster = hasil.data as List<id.primaraya.qcontrol.ranah.model.SlotWaktu>) }
            }
            is HasilOperasi.Gagal -> {}
        }
    }

    private fun muatDaftarTabMasterData(
        tab: TabMasterData,
        kataKunci: String = _keadaan.value.kataKunciMasterData
    ) {
        lingkup.launch {
            when (tab) {
                TabMasterData.PART -> {
                    val hasil = bacaDaftarPartMasterUseCase.eksekusi(kataKunci)
                    if (hasil is HasilOperasi.Berhasil<*>) {
                        @Suppress("UNCHECKED_CAST")
                        _keadaan.update { it.copy(daftarPartMaster = hasil.data as List<id.primaraya.qcontrol.ranah.model.Part>) }
                    }
                }
                TabMasterData.JENIS_DEFECT -> {
                    val hasil = bacaDaftarJenisDefectMasterUseCase.eksekusi(kataKunci)
                    if (hasil is HasilOperasi.Berhasil<*>) {
                        @Suppress("UNCHECKED_CAST")
                        _keadaan.update { it.copy(daftarJenisDefectMaster = hasil.data as List<id.primaraya.qcontrol.ranah.model.JenisDefect>) }
                    }
                }
                TabMasterData.MATERIAL -> {
                    val hasil = bacaDaftarMaterialMasterUseCase.eksekusi(kataKunci)
                    if (hasil is HasilOperasi.Berhasil<*>) {
                        @Suppress("UNCHECKED_CAST")
                        _keadaan.update { it.copy(daftarMaterialMaster = hasil.data as List<id.primaraya.qcontrol.ranah.model.Material>) }
                    }
                }
                TabMasterData.SLOT_WAKTU -> {
                    val hasil = bacaDaftarSlotWaktuMasterUseCase.eksekusi()
                    if (hasil is HasilOperasi.Berhasil<*>) {
                        @Suppress("UNCHECKED_CAST")
                        _keadaan.update { it.copy(daftarSlotWaktuMaster = hasil.data as List<id.primaraya.qcontrol.ranah.model.SlotWaktu>) }
                    }
                }
                TabMasterData.LINE_PRODUKSI -> {
                    val hasil = bacaDaftarLineProduksiMasterUseCase.eksekusi()
                    if (hasil is HasilOperasi.Berhasil<*>) {
                        @Suppress("UNCHECKED_CAST")
                        _keadaan.update { it.copy(daftarLineProduksiMaster = hasil.data as List<id.primaraya.qcontrol.ranah.model.LineProduksi>) }
                    }
                }
                TabMasterData.RINGKASAN -> { /* tidak perlu memuat daftar */ }
            }
        }
    }

    private fun pilihPartMaster(part: id.primaraya.qcontrol.ranah.model.Part?) {
        _keadaan.update { 
            it.copy(
                partMasterTerpilih = part, 
                daftarRelasiPartDefectMaster = emptyList(),
                daftarTemplateDefectPart = emptyList(),
                pesanTemplateDefectPart = null
            ) 
        }
        
        if (part != null) {
            lingkup.launch {
                when (val hasil = bacaRelasiPartDefectMasterUseCase.eksekusi(part.id)) {
                    is HasilOperasi.Berhasil<*> -> {
                        @Suppress("UNCHECKED_CAST")
                        _keadaan.update { it.copy(daftarRelasiPartDefectMaster = hasil.data as List<id.primaraya.qcontrol.ranah.model.RelasiPartDefect>) }
                    }
                    is HasilOperasi.Gagal -> {
                        // Abaikan error pembacaan relasi
                    }
                }
            }
            muatTemplateDefectPart(part.id)
        }
    }

    private fun muatTemplateDefectPart(partId: String) {
        _keadaan.update { it.copy(pesanTemplateDefectPart = "Memuat template...") }
        lingkup.launch {
            when (val hasil = bacaTemplateDefectPartUseCase.eksekusi(partId)) {
                is HasilOperasi.Berhasil<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    val daftar = hasil.data as List<id.primaraya.qcontrol.ranah.model.TemplateDefectPart>
                    _keadaan.update { 
                        it.copy(
                            daftarTemplateDefectPart = daftar,
                            pesanTemplateDefectPart = if (daftar.isEmpty()) "Aman: Belum ada template defect untuk part ini." else null
                        ) 
                    }
                }
                is HasilOperasi.Gagal -> {
                    _keadaan.update { it.copy(pesanTemplateDefectPart = "Gagal memuat template: ${hasil.kesalahan.pesan}") }
                }
            }
        }
    }

    private fun muatDraftInputHarian(tanggal: String, lineId: String) {
        _keadaan.update { it.copy(sedangMemuatInputHarian = true, pesanInputHarian = "Memuat draft...") }
        lingkup.launch {
            // Pastikan master line sudah ada di state untuk selector
            if (_keadaan.value.daftarLineProduksiMaster.isEmpty()) {
                muatDaftarTabMasterData(TabMasterData.LINE_PRODUKSI)
            }

            when (val hasil = kelolaInputHarianUseCase.ambilAtauBuatDraft(tanggal, lineId)) {
                is HasilOperasi.Berhasil<*> -> {
                    val draft = hasil.data as id.primaraya.qcontrol.ranah.model.DraftPemeriksaanHarian
                    _keadaan.update { 
                        it.copy(
                            sedangMemuatInputHarian = false,
                            draftPemeriksaanHarian = draft,
                            pesanInputHarian = null,
                            inputPartTerpilih = null,
                            tanggalPemeriksaanHarian = tanggal,
                            lineAktif = lineId
                        ) 
                    }
                    muatDaftarInputPart()
                    muatRingkasanInputHarian()
                }
                is HasilOperasi.Gagal -> {
                    _keadaan.update { 
                        it.copy(
                            sedangMemuatInputHarian = false,
                            pesanInputHarian = "Gagal memuat draft: ${hasil.kesalahan.pesan}"
                        ) 
                    }
                }
            }
        }
    }

    private fun muatDaftarInputPart(kataKunci: String = _keadaan.value.kataKunciPartInputHarian) {
        val draft = _keadaan.value.draftPemeriksaanHarian ?: return
        lingkup.launch {
            when (val hasil = kelolaInputHarianUseCase.bacaDaftarPart(draft.id, draft.lineId, kataKunci)) {
                is HasilOperasi.Berhasil<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    val daftar = hasil.data as List<id.primaraya.qcontrol.ranah.model.DraftInputPart>
                    _keadaan.update { it.copy(daftarInputPartDraft = daftar) }
                }
                is HasilOperasi.Gagal -> {}
            }
        }
    }

    private fun pilihInputPart(part: id.primaraya.qcontrol.ranah.model.DraftInputPart?) {
        _keadaan.update { it.copy(inputPartTerpilih = part, matrixInputDefectPart = null, pesanValidasiInputHarian = null) }
        if (part != null) {
            muatMatrixInputDefect(part)
        }
    }

    private fun muatMatrixInputDefect(part: id.primaraya.qcontrol.ranah.model.DraftInputPart) {
        lingkup.launch {
            // 1. Ambil Slot Waktu Aktif
            val hasilSlot = bacaDaftarSlotWaktuMasterUseCase.eksekusi()
            if (hasilSlot !is HasilOperasi.Berhasil<*>) return@launch
            @Suppress("UNCHECKED_CAST")
            val slots = hasilSlot.data as List<id.primaraya.qcontrol.ranah.model.SlotWaktu>

            // 2. Ambil Template Defect Part
            val hasilTemplate = bacaTemplateDefectPartUseCase.eksekusi(part.partId)
            if (hasilTemplate !is HasilOperasi.Berhasil<*>) return@launch
            @Suppress("UNCHECKED_CAST")
            val templates = hasilTemplate.data as List<id.primaraya.qcontrol.ranah.model.TemplateDefectPart>

            // 3. Ambil Data Input Defect Eksisting
            val hasilInput = kelolaInputHarianUseCase.bacaDaftarDefectSlot(part.id)
            val inputs = if (hasilInput is HasilOperasi.Berhasil<*>) {
                @Suppress("UNCHECKED_CAST")
                hasilInput.data as List<id.primaraya.qcontrol.ranah.model.DraftInputDefectSlot>
            } else emptyList()

            // 4. Bangun Matrix
            val matrix = kelolaInputHarianUseCase.bacaMatrixInputDefect(part, slots, templates, inputs)
            _keadaan.update { 
                it.copy(
                    matrixInputDefectPart = matrix,
                    pesanKesiapanInputHarian = if (templates.isEmpty()) "Template defect part ini belum tersedia. Periksa Master Data." else null
                ) 
            }
        }
    }

    private fun updateQtyCheck(partId: String, qty: Int) {
        if (qty < 0) return
        val harianId = _keadaan.value.draftPemeriksaanHarian?.id ?: return
        lingkup.launch {
            kelolaInputHarianUseCase.updateQtyCheck(harianId, partId, qty)
            muatDaftarInputPart()
            muatRingkasanInputHarian()
            
            // Update selection state and matrix
            val partTerupdate = _keadaan.value.daftarInputPartDraft.find { it.partId == partId }
            if (_keadaan.value.inputPartTerpilih?.partId == partId) {
                _keadaan.update { it.copy(inputPartTerpilih = partTerupdate) }
                if (partTerupdate != null) muatMatrixInputDefect(partTerupdate)
            }
        }
    }

    private fun updateDefectSlot(partId: String, slotId: String, relasiId: String, qty: Int) {
        if (qty < 0) return
        
        val harianId = _keadaan.value.draftPemeriksaanHarian?.id ?: return
        val currentPart = _keadaan.value.inputPartTerpilih ?: return
        
        // QC Validation: Total Defect cannot exceed Total Check
        val currentMatrix = _keadaan.value.matrixInputDefectPart
        if (currentMatrix != null) {
            val oldVal = currentMatrix.barisDefect
                .find { it.relasiPartDefectId == relasiId }
                ?.nilaiPerSlot?.find { it.slotWaktuId == slotId }?.jumlahDefect ?: 0
            
            val diff = qty - oldVal
            if (currentMatrix.ringkasan.totalDefectPart + diff > currentPart.qtyCheck) {
                _keadaan.update { it.copy(pesanValidasiInputHarian = "Total Defect tidak boleh melebihi Total Check (${currentPart.qtyCheck})") }
                return
            }
        }

        _keadaan.update { it.copy(pesanValidasiInputHarian = null) }

        lingkup.launch {
            kelolaInputHarianUseCase.updateDefectSlot(harianId, partId, relasiId, slotId, qty)
            muatDaftarInputPart()
            muatRingkasanInputHarian()
            
            // Update selection state and matrix
            val partTerupdate = _keadaan.value.daftarInputPartDraft.find { it.partId == partId }
            if (_keadaan.value.inputPartTerpilih?.partId == partId) {
                _keadaan.update { it.copy(inputPartTerpilih = partTerupdate) }
                if (partTerupdate != null) muatMatrixInputDefect(partTerupdate)
            }
        }
    }

    private fun muatRingkasanInputHarian() {
        val draftId = _keadaan.value.draftPemeriksaanHarian?.id ?: return
        lingkup.launch {
            when (val hasil = kelolaInputHarianUseCase.hitungRingkasan(draftId)) {
                is HasilOperasi.Berhasil<*> -> {
                    _keadaan.update { it.copy(ringkasanInputHarian = hasil.data as id.primaraya.qcontrol.ranah.model.RingkasanInputHarian) }
                }
                is HasilOperasi.Gagal -> {}
            }
        }
    }

    private fun resetDraftInputHarian() {
        val draftId = _keadaan.value.draftPemeriksaanHarian?.id ?: return
        lingkup.launch {
            when (val hasil = kelolaInputHarianUseCase.resetDraft(draftId)) {
                is HasilOperasi.Berhasil<*> -> {
                    _keadaan.update { it.copy(inputPartTerpilih = null, matrixInputDefectPart = null) }
                    muatDaftarInputPart()
                    muatRingkasanInputHarian()
                    tangani(AksiAplikasi.TampilkanPesanFlash("Draft input berhasil dikosongkan", TipePesanFlash.INFO))
                }
                is HasilOperasi.Gagal -> {
                    tangani(AksiAplikasi.TampilkanPesanFlash("Gagal reset draft: ${hasil.kesalahan.pesan}", TipePesanFlash.ERROR))
                }
            }
        }
    }

    private fun kirimKeServer() {
        val draft = _keadaan.value.draftPemeriksaanHarian ?: return
        
        _keadaan.update { it.copy(sedangSinkronisasi = true) }
        
        lingkup.launch {
            when (val hasil = kirimPemeriksaanHarianUseCase.eksekusi(draft)) {
                is HasilOperasi.Berhasil<*> -> {
                    tangani(AksiAplikasi.TampilkanPesanFlash("Draft masuk antrean sinkronisasi", TipePesanFlash.SUKSES))
                    muatRingkasanOutbox()
                    // Sinkronkan outbox secara proaktif jika online
                    if (_keadaan.value.statusKoneksi == StatusKoneksiServer.Tersambung) {
                        pengelolaSinkronisasi.sinkronkanSekarang()
                    }
                    muatDraftInputHarian(draft.tanggalProduksi, draft.lineId)
                }
                is HasilOperasi.Gagal -> {
                    _keadaan.update { it.copy(sedangSinkronisasi = false) }
                    tangani(AksiAplikasi.TampilkanPesanFlash("Gagal mengirim: ${hasil.kesalahan.pesan}", TipePesanFlash.ERROR))
                }
            }
        }
    }
}
