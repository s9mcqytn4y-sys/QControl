package id.azure.qcontrol.presentation.inspection

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.azure.qcontrol.domain.model.InspeksiHarian
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspeksiHarianScreen(viewModel: InspeksiHarianViewModel, onBack: () -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inspeksi Harian") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Rounded.Add, contentDescription = "Tambah")
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.items) { item ->
                    InspeksiItem(item)
                }
            }
        }

        if (showDialog) {
            AddInspeksiDialog(
                onDismiss = { showDialog = false },
                onConfirm = { totalCheck, totalDefect ->
                    viewModel.handleIntent(InspeksiHarianIntent.AddInspeksi(totalCheck, totalDefect))
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun InspeksiItem(item: InspeksiHarian) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = dateFormat.format(Date(item.timestamp)),
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = String.format("%.2f%% NG", item.rasioNg),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(8.dp))
            Text("Total Check: ${item.totalCheck}")
            Text("Total Defect: ${item.totalDefect}")
        }
    }
}

@Composable
fun AddInspeksiDialog(onDismiss: () -> Unit, onConfirm: (Int, Int) -> Unit) {
    var totalCheck by remember { mutableStateOf("") }
    var totalDefect by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Inspeksi") },
        text = {
            Column {
                OutlinedTextField(
                    value = totalCheck,
                    onValueChange = { totalCheck = it },
                    label = { Text("Total Check") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = totalDefect,
                    onValueChange = { totalDefect = it },
                    label = { Text("Total Defect") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val check = totalCheck.toIntOrNull() ?: 0
                val defect = totalDefect.toIntOrNull() ?: 0
                onConfirm(check, defect)
            }) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
