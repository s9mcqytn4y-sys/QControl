package id.primaraya.qcontrol.tampilan

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import id.primaraya.qcontrol.tampilan.halaman.HalamanLogin
import id.primaraya.qcontrol.tampilan.kerangka.KerangkaAplikasi
import id.primaraya.qcontrol.tampilan.mvi.*
import id.primaraya.qcontrol.tampilan.state.TipePesanFlash
import id.primaraya.qcontrol.tampilan.state.rememberAplikasiGraph
import id.primaraya.qcontrol.tema.TemaQControl

@Composable
fun AplikasiQControl() {
    val graph = rememberAplikasiGraph()
    
    val shellState by graph.shellStore.state.collectAsState()
    val sessionState by graph.sessionStore.state.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }

    // Efek untuk Flash Message
    LaunchedEffect(shellState.pesanFlash) {
        shellState.pesanFlash?.let { flash ->
            val labelTombol = if (flash.tipe == TipePesanFlash.ERROR) "TUTUP" else "OK"
            val hasil = snackbarHostState.showSnackbar(
                message = flash.pesan,
                actionLabel = labelTombol,
                duration = if (flash.tipe == TipePesanFlash.ERROR) SnackbarDuration.Long else SnackbarDuration.Short
            )
            if (hasil == SnackbarResult.ActionPerformed || hasil == SnackbarResult.Dismissed) {
                graph.shellStore.tangani(ShellIntent.BersihkanPesan)
            }
        }
    }

    // Pantau Effect dari SessionStore untuk pesan flash
    LaunchedEffect(graph.sessionStore) {
        graph.sessionStore.effect.collect { effect ->
            when (effect) {
                is SessionEffect.TampilkanPesan -> {
                    graph.shellStore.tangani(
                        ShellIntent.TampilkanPesan(
                            effect.pesan, 
                            if (effect.sukses) TipePesanFlash.SUKSES else TipePesanFlash.ERROR
                        )
                    )
                }
                null,
                SessionEffect.NavigasiKeDashboard,
                SessionEffect.NavigasiKeLogin -> Unit
            }
        }
    }

    // Inisialisasi
    LaunchedEffect(Unit) {
        graph.sessionStore.tangani(SessionIntent.Inisialisasi)
        graph.shellStore.tangani(ShellIntent.PeriksaKoneksi)
    }

    TemaQControl {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            Surface(
                modifier = Modifier.fillMaxSize().padding(padding),
                color = MaterialTheme.colorScheme.background
            ) {
                if (sessionState.sesiAktif == null) {
                    HalamanLogin(
                        state = sessionState,
                        onIntent = { graph.sessionStore.tangani(it) },
                        statusKoneksi = shellState.statusKoneksi,
                        pesanKoneksi = shellState.pesanStatusKoneksi
                    )
                } else {
                    KerangkaAplikasi(graph = graph)
                }
            }
        }
    }
}
