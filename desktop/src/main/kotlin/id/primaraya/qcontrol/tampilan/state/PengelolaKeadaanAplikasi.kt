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
    private val lingkup: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    private val _keadaan = MutableStateFlow(KeadaanAplikasi())
    val keadaan: StateFlow<KeadaanAplikasi> = _keadaan.asStateFlow()

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
    }

    fun tangani(aksi: AksiAplikasi) {
        when (aksi) {
            is AksiAplikasi.PilihRute -> {
                _keadaan.update { it.copy(ruteAktif = aksi.rute) }
            }
            is AksiAplikasi.GantiLineAktif -> {
                _keadaan.update { it.copy(lineAktif = aksi.line) }
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
        }
    }

    private fun sinkronkanSekarang() {
        lingkup.launch {
            // Selalu reset yang stuck sebelum sinkronisasi manual untuk keamanan
            resetOutboxSedangDikirimUseCase()
            pengelolaSinkronisasi.sinkronkanSekarang()
            muatRingkasanOutbox()
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
                }
                is HasilOperasi.Gagal -> {
                    _keadaan.update { 
                        it.copy(
                            sedangLogin = false,
                            pesanLogin = hasil.kesalahan.pesan
                        )
                    }
                }
            }
        }
    }

    private fun logout() {
        lingkup.launch {
            keluarSesiUseCase.eksekusi()
            _keadaan.update { it.copy(sesiAktif = null) }
            pengelolaSinkronisasi.tokenAktif = null
        }
    }
}
