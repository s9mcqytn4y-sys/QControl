package id.primaraya.qcontrol.tampilan.mvi

import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.inti.mvi.MviStore
import id.primaraya.qcontrol.ranah.usecase.PeriksaKesehatanServerUseCase
import id.primaraya.qcontrol.tampilan.state.StatusKoneksiServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ShellStore(
    private val periksaKesehatanServerUseCase: PeriksaKesehatanServerUseCase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) : MviStore<ShellIntent, ShellState, ShellEffect> {

    private val _state = MutableStateFlow(ShellState())
    override val state: StateFlow<ShellState> = _state.asStateFlow()

    private val _effect = MutableStateFlow<ShellEffect?>(null)
    override val effect: StateFlow<ShellEffect?> = _effect.asStateFlow()

    override fun tangani(intent: ShellIntent) {
        when (intent) {
            is ShellIntent.PilihRute -> {
                _state.update { it.copy(ruteAktif = intent.rute) }
            }
            is ShellIntent.PeriksaKoneksi -> periksaKoneksi()
            is ShellIntent.TampilkanPesan -> {
                _state.update { it.copy(pesanFlash = ShellPesanFlash(intent.pesan, intent.tipe)) }
            }
            is ShellIntent.BersihkanPesan -> {
                _state.update { it.copy(pesanFlash = null) }
            }
        }
    }

    override fun bersihkanEffect() {
        _effect.value = null
    }

    private fun periksaKoneksi() {
        _state.update { it.copy(statusKoneksi = StatusKoneksiServer.Memeriksa, pesanStatusKoneksi = "Memeriksa koneksi...") }
        scope.launch {
            when (val hasil = periksaKesehatanServerUseCase()) {
                is HasilOperasi.Berhasil<*> -> {
                    _state.update { 
                        it.copy(
                            statusKoneksi = StatusKoneksiServer.Tersambung,
                            pesanStatusKoneksi = "Server Perusahaan Tersambung"
                        )
                    }
                }
                is HasilOperasi.Gagal -> {
                    _state.update { 
                        it.copy(
                            statusKoneksi = StatusKoneksiServer.Terputus,
                            pesanStatusKoneksi = "Server Perusahaan Terputus"
                        )
                    }
                }
            }
        }
    }
}
