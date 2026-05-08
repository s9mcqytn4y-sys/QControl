package id.primaraya.qcontrol.tampilan.mvi

import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.inti.mvi.MviStore
import id.primaraya.qcontrol.ranah.usecase.*
import id.primaraya.qcontrol.tampilan.state.PengelolaSinkronisasi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SinkronisasiStore(
    private val bacaRingkasanOutboxSinkronisasiUseCase: BacaRingkasanOutboxSinkronisasiUseCase,
    private val resetOutboxSedangDikirimUseCase: ResetOutboxSedangDikirimUseCase,
    private val pengelolaSinkronisasi: PengelolaSinkronisasi,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) : MviStore<SinkronisasiIntent, SinkronisasiState, SinkronisasiEffect> {

    private val _state = MutableStateFlow(SinkronisasiState())
    override val state: StateFlow<SinkronisasiState> = _state.asStateFlow()

    private val _effect = MutableStateFlow<SinkronisasiEffect?>(null)
    override val effect: StateFlow<SinkronisasiEffect?> = _effect.asStateFlow()

    init {
        scope.launch {
            pengelolaSinkronisasi.sedangSinkronisasi.collect { sedang ->
                _state.update { it.copy(sedangSinkronisasi = sedang) }
                if (!sedang) muatRingkasan()
            }
        }
        scope.launch {
            pengelolaSinkronisasi.pesanSinkronisasiTerakhir.collect { pesan ->
                _state.update { it.copy(pesanTerakhir = pesan) }
            }
        }
    }

    override fun tangani(intent: SinkronisasiIntent) {
        when (intent) {
            is SinkronisasiIntent.MuatRingkasan -> muatRingkasan()
            is SinkronisasiIntent.SinkronkanSekarang -> {
                scope.launch {
                    pengelolaSinkronisasi.sinkronkanSekarang()
                }
            }
            is SinkronisasiIntent.ResetStuck -> {
                scope.launch {
                    resetOutboxSedangDikirimUseCase()
                    muatRingkasan()
                }
            }
        }
    }

    override fun bersihkanEffect() {
        _effect.value = null
    }

    private fun muatRingkasan() {
        _state.update { it.copy(sedangMemuat = true) }
        scope.launch {
            when (val hasil = bacaRingkasanOutboxSinkronisasiUseCase()) {
                is HasilOperasi.Berhasil<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    _state.update { 
                        it.copy(
                            sedangMemuat = false,
                            ringkasan = hasil.data as id.primaraya.qcontrol.ranah.model.RingkasanOutboxSinkronisasi
                        ) 
                    }
                }
                is HasilOperasi.Gagal -> {
                    _state.update { it.copy(sedangMemuat = false) }
                }
            }
        }
    }
}
