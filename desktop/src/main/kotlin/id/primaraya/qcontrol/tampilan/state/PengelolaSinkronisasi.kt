package id.primaraya.qcontrol.tampilan.state

import id.primaraya.qcontrol.ranah.usecase.BacaDaftarOutboxMenungguUseCase
import id.primaraya.qcontrol.ranah.usecase.KirimItemOutboxUseCase
import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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
    
    /**
     * State yang menunjukkan apakah proses sinkronisasi sedang berjalan aktif.
     */
    val sedangSinkronisasi: StateFlow<Boolean> = _sedangSinkronisasi.asStateFlow()

    private var jobSinkronisasi: Job? = null

    /**
     * Memulai loop sinkronisasi otomatis dengan interval tertentu.
     */
    fun mulaiSinkronisasiOtomatis(intervalMs: Long = 30000) {
        if (jobSinkronisasi?.isActive == true) return

        jobSinkronisasi = scope.launch {
            while (isActive) {
                sinkronkanSekarang()
                delay(intervalMs)
            }
        }
    }

    /**
     * Menghentikan loop sinkronisasi otomatis.
     */
    fun berhenti() {
        jobSinkronisasi?.cancel()
    }

    /**
     * Melakukan satu siklus sinkronisasi secara manual atau dipicu oleh loop.
     */
    suspend fun sinkronkanSekarang() {
        // Jangan jalankan jika sudah ada yang berjalan
        if (_sedangSinkronisasi.value) return
        
        _sedangSinkronisasi.value = true

        try {
            // Ambil maksimal 10 item per siklus untuk menghindari beban berlebih
            val hasilDaftar = bacaOutboxMenunggu(batas = 10)
            
            if (hasilDaftar is HasilOperasi.Berhasil) {
                val daftar = hasilDaftar.data
                
                if (daftar.isNotEmpty()) {
                    println("Sinkronisasi: Memproses ${daftar.size} item outbox...")
                    
                    for (item in daftar) {
                        // Kirim item satu per satu
                        kirimItemOutbox.eksekusi(item)
                        
                        // Beri jeda kecil (backoff sederhana) antar pengiriman
                        delay(1000)
                    }
                    
                    println("Sinkronisasi: Siklus selesai.")
                }
            }
        } catch (e: Exception) {
            System.err.println("Sinkronisasi: Terjadi kesalahan kritis - ${e.message}")
        } finally {
            _sedangSinkronisasi.value = false
        }
    }
}
