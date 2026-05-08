package id.primaraya.qcontrol.tampilan.mvi

import id.primaraya.qcontrol.inti.mvi.MviEffect
import id.primaraya.qcontrol.inti.mvi.MviIntent
import id.primaraya.qcontrol.inti.mvi.MviState
import id.primaraya.qcontrol.ranah.model.RingkasanOutboxSinkronisasi

sealed class SinkronisasiIntent : MviIntent {
    object MuatRingkasan : SinkronisasiIntent()
    object SinkronkanSekarang : SinkronisasiIntent()
    object ResetStuck : SinkronisasiIntent()
}

data class SinkronisasiState(
    val sedangMemuat: Boolean = false,
    val ringkasan: RingkasanOutboxSinkronisasi? = null,
    val sedangSinkronisasi: Boolean = false,
    val pesanTerakhir: String? = null
) : MviState

sealed class SinkronisasiEffect : MviEffect
