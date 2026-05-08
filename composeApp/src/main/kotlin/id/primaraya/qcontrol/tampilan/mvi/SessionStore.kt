package id.primaraya.qcontrol.tampilan.mvi

import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.inti.mvi.MviStore
import id.primaraya.qcontrol.ranah.usecase.AmbilSesiAktifUseCase
import id.primaraya.qcontrol.ranah.usecase.KeluarSesiUseCase
import id.primaraya.qcontrol.ranah.usecase.MasukSesiUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SessionStore(
    private val masukSesiUseCase: MasukSesiUseCase,
    private val keluarSesiUseCase: KeluarSesiUseCase,
    private val ambilSesiAktifUseCase: AmbilSesiAktifUseCase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) : MviStore<SessionIntent, SessionState, SessionEffect> {

    private val _state = MutableStateFlow(SessionState())
    override val state: StateFlow<SessionState> = _state.asStateFlow()

    private val _effect = MutableStateFlow<SessionEffect?>(null)
    override val effect: StateFlow<SessionEffect?> = _effect.asStateFlow()

    override fun tangani(intent: SessionIntent) {
        when (intent) {
            is SessionIntent.Inisialisasi -> periksaSesi()
            is SessionIntent.Login -> login(intent.email, intent.kataSandi)
            is SessionIntent.Logout -> logout()
        }
    }

    override fun bersihkanEffect() {
        _effect.value = null
    }

    private fun periksaSesi() {
        scope.launch {
            when (val hasil = ambilSesiAktifUseCase.eksekusi()) {
                is HasilOperasi.Berhasil<*> -> {
                    val sesi = hasil.data as? id.primaraya.qcontrol.ranah.model.Autentikasi
                    if (sesi != null && sesi.peran == "HeadQC") {
                        _state.update { it.copy(sesiAktif = sesi) }
                        _effect.value = SessionEffect.NavigasiKeDashboard
                    } else if (sesi != null) {
                        logout()
                    }
                }
                is HasilOperasi.Gagal -> {}
            }
        }
    }

    private fun login(email: String, kataSandi: String) {
        _state.update { it.copy(sedangLogin = true, pesanLogin = "Sedang masuk...") }
        scope.launch {
            when (val hasil = masukSesiUseCase.eksekusi(email, kataSandi)) {
                is HasilOperasi.Berhasil<*> -> {
                    val sesi = hasil.data as id.primaraya.qcontrol.ranah.model.Autentikasi
                    if (sesi.peran == "HeadQC") {
                        _state.update { it.copy(sedangLogin = false, sesiAktif = sesi, pesanLogin = null) }
                        _effect.value = SessionEffect.NavigasiKeDashboard
                        _effect.emit(SessionEffect.TampilkanPesan("Selamat datang, ${sesi.namaPengguna}!", true))
                    } else {
                        _state.update { it.copy(sedangLogin = false, pesanLogin = "Akses ditolak: Bukan HeadQC") }
                        _effect.emit(SessionEffect.TampilkanPesan("Akses ditolak: Role Anda bukan HeadQC", false))
                        keluarSesiUseCase.eksekusi()
                    }
                }
                is HasilOperasi.Gagal -> {
                    _state.update { it.copy(sedangLogin = false, pesanLogin = hasil.kesalahan.pesan) }
                    _effect.emit(SessionEffect.TampilkanPesan("Gagal masuk: ${hasil.kesalahan.pesan}", false))
                }
            }
        }
    }

    private fun logout() {
        scope.launch {
            keluarSesiUseCase.eksekusi()
            _state.update { it.copy(sesiAktif = null) }
            _effect.value = SessionEffect.NavigasiKeLogin
            _effect.emit(SessionEffect.TampilkanPesan("Sesi telah diakhiri", true))
        }
    }
}
