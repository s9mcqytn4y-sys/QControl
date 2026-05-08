package id.primaraya.qcontrol.tampilan.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import id.primaraya.qcontrol.tampilan.mvi.*
import org.koin.compose.koinInject

class AplikasiGraph(
    val shellStore: ShellStore,
    val sessionStore: SessionStore,
    val masterDataStore: MasterDataStore,
    val inputHarianStore: InputHarianStore,
    val sinkronisasiStore: SinkronisasiStore
)

@Composable
fun rememberAplikasiGraph(): AplikasiGraph {
    val shellStore: ShellStore = koinInject()
    val sessionStore: SessionStore = koinInject()
    val masterDataStore: MasterDataStore = koinInject()
    val inputHarianStore: InputHarianStore = koinInject()
    val sinkronisasiStore: SinkronisasiStore = koinInject()
    
    return remember(shellStore, sessionStore, masterDataStore, inputHarianStore, sinkronisasiStore) {
        AplikasiGraph(shellStore, sessionStore, masterDataStore, inputHarianStore, sinkronisasiStore)
    }
}
