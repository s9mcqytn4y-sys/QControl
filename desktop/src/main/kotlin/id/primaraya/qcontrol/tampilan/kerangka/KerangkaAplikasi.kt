package id.primaraya.qcontrol.tampilan.kerangka

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KerangkaAplikasi(
    judul: String,
    konten: @Composable (PaddingValues) -> Unit
) {
    var menuTerpilih by remember { mutableStateOf("Dashboard") }

    Row(modifier = Modifier.fillMaxSize()) {
        // Bilah Samping (Sidebar)
        NavigationRail(
            modifier = Modifier.width(240.dp),
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            Spacer(Modifier.height(16.dp))
            Text(
                "QControl",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(16.dp)
            )
            Spacer(Modifier.height(16.dp))

            ItemMenu("Dashboard", Icons.Default.Dashboard, menuTerpilih == "Dashboard") { menuTerpilih = "Dashboard" }
            ItemMenu("Input Harian", Icons.Default.Edit, menuTerpilih == "Input Harian") { menuTerpilih = "Input Harian" }
            ItemMenu("Recording Data", Icons.Default.List, menuTerpilih == "Recording Data") { menuTerpilih = "Recording Data" }
            ItemMenu("Control Chart", Icons.Default.ShowChart, menuTerpilih == "Control Chart") { menuTerpilih = "Control Chart" }
            ItemMenu("Laporan Bulanan", Icons.Default.Assessment, menuTerpilih == "Laporan Bulanan") { menuTerpilih = "Laporan Bulanan" }
            ItemMenu("Master Data", Icons.Default.Storage, menuTerpilih == "Master Data") { menuTerpilih = "Master Data" }
            
            Spacer(Modifier.weight(1f))
            
            ItemMenu("Pengaturan", Icons.Default.Settings, menuTerpilih == "Pengaturan") { menuTerpilih = "Pengaturan" }
            Spacer(Modifier.height(16.dp))
        }

        // Konten Utama
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(judul) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        ) { padding ->
            konten(padding)
        }
    }
}

@Composable
fun ItemMenu(
    label: String,
    ikon: ImageVector,
    terpilih: Boolean,
    onClick: () -> Unit
) {
    NavigationRailItem(
        selected = terpilih,
        onClick = onClick,
        icon = { Icon(ikon, contentDescription = label) },
        label = { Text(label) },
        alwaysShowLabel = true,
        colors = NavigationRailItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}
