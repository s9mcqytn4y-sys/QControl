package id.azure.qcontrol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import id.azure.qcontrol.core.theme.QControlTheme
import id.azure.qcontrol.presentation.navigation.QControlNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QControlTheme {
                QControlNavGraph()
            }
        }
    }
}
