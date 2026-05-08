package id.primaraya.qcontrol.tampilan.kerangka

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import id.primaraya.qcontrol.tampilan.mvi.*
import id.primaraya.qcontrol.tampilan.state.AplikasiGraph

@Composable
fun KerangkaAplikasi(
    graph: AplikasiGraph
) {
    val shellState by graph.shellStore.state.collectAsState()
    val sessionState by graph.sessionStore.state.collectAsState()
    val inputHarianState by graph.inputHarianStore.state.collectAsState()
    
    Row(modifier = Modifier.fillMaxSize()) {
        SidebarAplikasi(
            shellState = shellState,
            sessionState = sessionState,
            inputHarianState = inputHarianState,
            onShellIntent = { graph.shellStore.tangani(it) },
            onSessionIntent = { graph.sessionStore.tangani(it) }
        )

        Scaffold(
            topBar = {
                HeaderAplikasi(
                    shellState = shellState,
                    onShellIntent = { graph.shellStore.tangani(it) }
                )
            }
        ) { padding ->
            KontenAplikasi(
                graph = graph,
                padding = padding
            )
        }
    }
}
