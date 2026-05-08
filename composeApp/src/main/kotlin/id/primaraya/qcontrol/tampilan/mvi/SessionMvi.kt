package id.primaraya.qcontrol.tampilan.mvi

import id.primaraya.qcontrol.inti.mvi.MviEffect
import id.primaraya.qcontrol.inti.mvi.MviIntent
import id.primaraya.qcontrol.inti.mvi.MviState
import id.primaraya.qcontrol.ranah.model.Autentikasi

sealed class SessionIntent : MviIntent {
    object Inisialisasi : SessionIntent()
    data class Login(val email: String, val kataSandi: String) : SessionIntent()
    object Logout : SessionIntent()
}

data class SessionState(
    val sesiAktif: Autentikasi? = null,
    val sedangLogin: Boolean = false,
    val pesanLogin: String? = null
) : MviState

sealed class SessionEffect : MviEffect {
    data class TampilkanPesan(val pesan: String, val sukses: Boolean) : SessionEffect()
    object NavigasiKeDashboard : SessionEffect()
    object NavigasiKeLogin : SessionEffect()
}
