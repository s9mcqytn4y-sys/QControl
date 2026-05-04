package id.primaraya.qcontrol.tampilan.state

import id.primaraya.qcontrol.konfigurasi.KonfigurasiSinkronisasi
import id.primaraya.qcontrol.ranah.usecase.BacaDaftarOutboxMenungguUseCase
import id.primaraya.qcontrol.ranah.usecase.KirimItemOutboxUseCase
import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Pengelola siklus hidup sinkronisasi data di latar belakang.
 * Bertanggung jawab untuk menjalankan loop periodik yang memeriksa outbox lokal.
 */
class PengelolaSinkronisasi(
    private val bacaOutboxMenunggu: BacaDaftarOutboxMenungguUseCase,
    private val kirimItemOutbox: KirimItemOutboxUseCase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) {
    private val _sedangSinkronisasi = MutableStateFlow(false)
    val sedangSinkronisasi: StateFlow<Boolean> = _sedangSinkronisasi.asStateFlow()

    private val _sinkronisasiOtomatisAktif = MutableStateFlow(KonfigurasiSinkronisasi.SINKRONISASI_OTOMATIS_AKTIF_DEFAULT)
    val sinkronisasiOtomatisAktif: StateFlow<Boolean> = _sinkronisasiOtomatisAktif.asStateFlow()

    private val _pesanSinkronisasiTerakhir = MutableStateFlow<String?>(null)
    val pesanSinkronisasiTerakhir: StateFlow<String?> = _pesanSinkronisasiTerakhir.asStateFlow()

    private val _waktuSinkronisasiTerakhir = MutableStateFlow<String?>(null)
    val waktuSinkronisasiTerakhir: StateFlow<String?> = _waktuSinkronisasiTerakhir.asStateFlow()

    var tokenAktif: String? = null

    private var jobSinkronisasi: Job? = null
    private val formatWaktu = DateTimeFormatter.ofPattern("HH:mm:ss")
        .withZone(ZoneId.systemDefault())

    /**
     * Memulai loop sinkronisasi otomatis.
     */
    fun mulaiSinkronisasiOtomatis() {
        _sinkronisasiOtomatisAktif.value = true
        if (jobSinkronisasi?.isActive == true) return

        jobSinkronisasi = scope.launch {
            while (isActive && _sinkronisasiOtomatisAktif.value) {
                sinkronkanSekarang()
                delay(KonfigurasiSinkronisasi.INTERVAL_SINKRONISASI_MILIDETIK)
            }
        }
    }

    /**
     * Menghentikan loop sinkronisasi otomatis.
     */
    fun hentikanSinkronisasiOtomatis() {
        _sinkronisasiOtomatisAktif.value = false
        jobSinkronisasi?.cancel()
    }

    /**
     * Mengubah status aktif/nonaktif sinkronisasi otomatis.
     */
    fun ubahSinkronisasiOtomatis(aktif: Boolean) {
        if (aktif) mulaiSinkronisasiOtomatis() else hentikanSinkronisasiOtomatis()
    }

    /**
     * Melakukan satu siklus sinkronisasi secara manual atau dipicu oleh loop.
     */
    suspend fun sinkronkanSekarang() {
        // Jangan jalankan jika sudah ada yang berjalan aktif
        if (_sedangSinkronisasi.value) return
        
        _sedangSinkronisasi.value = true
        _pesanSinkronisasiTerakhir.value = "Memulai sinkronisasi..."

        try {
            val hasilDaftar = bacaOutboxMenunggu(batas = KonfigurasiSinkronisasi.BATAS_ITEM_PER_SIKLUS)
            
            if (hasilDaftar is HasilOperasi.Berhasil) {
                val daftar = hasilDaftar.data
                
                if (daftar.isNotEmpty()) {
                    var berhasil = 0
                    var gagal = 0
                    
                    for (item in daftar) {
                        val hasilKirim = kirimItemOutbox.eksekusi(item, tokenAktif)
                        if (hasilKirim is HasilOperasi.Berhasil) berhasil++ else gagal++
                        
                        // Jeda kecil antar pengiriman
                        delay(500)
                    }
                    
                    _pesanSinkronisasiTerakhir.value = "Selesai: $berhasil berhasil, $gagal gagal."
                } else {
                    _pesanSinkronisasiTerakhir.value = "Tidak ada data yang perlu disinkronkan (Idle)."
                }
            } else if (hasilDaftar is HasilOperasi.Gagal) {
                _pesanSinkronisasiTerakhir.value = "Gagal membaca outbox: ${hasilDaftar.kesalahan.pesan}"
            }
        } catch (e: Exception) {
            _pesanSinkronisasiTerakhir.value = "Terjadi kesalahan sistem sinkronisasi."
            // Logging minimal untuk debugging developer
            System.err.println("Kesalahan PengelolaSinkronisasi: ${e.message}")
        } finally {
            _waktuSinkronisasiTerakhir.value = formatWaktu.format(Instant.now())
            _sedangSinkronisasi.value = false
        }
    }
}
