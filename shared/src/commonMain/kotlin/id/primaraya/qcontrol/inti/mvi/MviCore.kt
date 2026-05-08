package id.primaraya.qcontrol.inti.mvi

import kotlinx.coroutines.flow.StateFlow

interface MviIntent

interface MviState

interface MviEffect

interface MviStore<Intent : MviIntent, State : MviState, Effect : MviEffect> {
    val state: StateFlow<State>
    val effect: StateFlow<Effect?>
    fun tangani(intent: Intent)
    fun bersihkanEffect()
}
