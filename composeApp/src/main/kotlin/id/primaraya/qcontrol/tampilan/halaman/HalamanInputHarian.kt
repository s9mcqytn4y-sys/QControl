package id.primaraya.qcontrol.tampilan.halaman

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import id.primaraya.qcontrol.ranah.model.*
import id.primaraya.qcontrol.tampilan.komponen.*
import id.primaraya.qcontrol.tampilan.mvi.*
import id.primaraya.qcontrol.tampilan.state.AplikasiGraph
import id.primaraya.qcontrol.tampilan.state.StatusKoneksiServer
import id.primaraya.qcontrol.tampilan.state.TipePesanFlash
import id.primaraya.qcontrol.tema.*
import id.primaraya.qcontrol.utilitas.FormatWaktu

@Composable
fun HalamanInputHarian(graph: AplikasiGraph) {
    val state by graph.inputHarianStore.state.collectAsState()
    val masterDataState by graph.masterDataStore.state.collectAsState()
    val sinkronisasiState by graph.sinkronisasiStore.state.collectAsState()
    val shellState by graph.shellStore.state.collectAsState()

    // Inisialisasi
    LaunchedEffect(Unit) {
        if (state.draft == null) {
            val tgl = state.tanggal.ifEmpty { java.time.LocalDate.now().toString() }
            val line = state.lineId.ifEmpty { masterDataState.daftarLineProduksi.firstOrNull()?.id ?: "" }
            graph.inputHarianStore.tangani(InputHarianIntent.Inisialisasi(tgl, line))
        }
        graph.sinkronisasiStore.tangani(SinkronisasiIntent.MuatRingkasan)
    }

    // Effect Handling
    LaunchedEffect(graph.inputHarianStore) {
        graph.inputHarianStore.effect.collect { effect ->
            when (effect) {
                is InputHarianEffect.TampilkanPesan -> {
                    graph.shellStore.tangani(ShellIntent.TampilkanPesan(effect.pesan, effect.tipe))
                }
                null -> Unit
            }
        }
    }

    if (masterDataState.ringkasan == null) {
        StateKosongQControl(
            ikon = Icons.Default.Storage,
            judul = "Data Acuan Belum Tersedia",
            pesan = "Tarik data acuan dari server perusahaan terlebih dahulu agar sistem dapat bekerja offline.",
            onAksi = { graph.shellStore.tangani(ShellIntent.PilihRute(id.primaraya.qcontrol.tampilan.navigasi.RuteAplikasi.MasterData)) },
            labelAksi = "Buka Data Acuan"
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize().padding(UkuranQControl.SpasiNormal)) {
        // --- HEADER HALAMAN ---
        HeaderInputHarian(state, masterDataState, graph.inputHarianStore)
        
        Spacer(Modifier.height(UkuranQControl.SpasiNormal))

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(UkuranQControl.SpasiNormal)
        ) {
            // --- PANEL KIRI: PILIH PART ---
            PanelPilihPart(
                modifier = Modifier.weight(0.25f),
                state = state,
                onIntent = { graph.inputHarianStore.tangani(it) }
            )

            // --- PANEL TENGAH: MATRIX INPUT ---
            PanelMatrixDefect(
                modifier = Modifier.weight(0.5f),
                state = state,
                onIntent = { graph.inputHarianStore.tangani(it) }
            )

            // --- PANEL KANAN: RINGKASAN ---
            PanelRingkasanQC(
                modifier = Modifier.weight(0.25f),
                state = state,
                shellState = shellState,
                sinkronisasiState = sinkronisasiState,
                onIntent = { graph.inputHarianStore.tangani(it) },
                onShellIntent = { graph.shellStore.tangani(it) }
            )
        }
    }
}

@Composable
private fun HeaderInputHarian(
    state: InputHarianState,
    masterDataState: MasterDataState,
    store: InputHarianStore
) {
    var ekspansiLine by remember { mutableStateOf(false) }
    var ekspansiTanggal by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Digital Checksheet QC", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = TeksKontrasTinggi)
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Selector Tanggal
                Box {
                    Surface(
                        onClick = { ekspansiTanggal = true },
                        color = Color.Transparent,
                        shape = MaterialTheme.shapes.extraSmall
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(4.dp)) {
                            Icon(Icons.Default.Event, null, modifier = Modifier.size(16.dp), tint = SolarYellow)
                            Spacer(Modifier.width(8.dp))
                            Text(FormatWaktu.formatTanggalIndo(state.tanggal.ifEmpty { java.time.LocalDate.now().toString() }), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = TeksKontrasTinggi)
                            Icon(Icons.Default.ArrowDropDown, null, tint = TeksKontrasRendah)
                        }
                    }
                    DropdownMenu(expanded = ekspansiTanggal, onDismissRequest = { ekspansiTanggal = false }) {
                        (0..6).forEach { i ->
                            val tgl = java.time.LocalDate.now().minusDays(i.toLong()).toString()
                            DropdownMenuItem(
                                text = { Text(FormatWaktu.formatTanggalIndo(tgl)) },
                                onClick = {
                                    store.tangani(InputHarianIntent.GantiTanggal(tgl))
                                    ekspansiTanggal = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(Modifier.width(16.dp))
                
                // Selector Line
                Box {
                    Surface(
                        onClick = { ekspansiLine = true },
                        color = Color.White.copy(alpha = 0.05f),
                        shape = MaterialTheme.shapes.extraSmall,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GarisSubtle)
                    ) {
                        val lineAktif = masterDataState.daftarLineProduksi.find { it.id == state.lineId }?.namaLine ?: "Pilih Line"
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                            Icon(Icons.Default.PrecisionManufacturing, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(8.dp))
                            Text("Line: $lineAktif", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = TeksKontrasTinggi)
                            Icon(Icons.Default.ArrowDropDown, null, tint = TeksKontrasRendah)
                        }
                    }
                    
                    DropdownMenu(
                        expanded = ekspansiLine,
                        onDismissRequest = { ekspansiLine = false },
                        modifier = Modifier.background(LatarBelakangSidebar)
                    ) {
                        masterDataState.daftarLineProduksi.forEach { line ->
                            DropdownMenuItem(
                                text = { 
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (state.lineId == line.id) {
                                            Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp), tint = SolarYellow)
                                            Spacer(Modifier.width(8.dp))
                                        }
                                        Text(line.namaLine, color = if (state.lineId == line.id) SolarYellow else TeksKontrasTinggi)
                                    }
                                },
                                onClick = {
                                    ekspansiLine = false
                                    store.tangani(InputHarianIntent.GantiLine(line.id))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PanelPilihPart(
    modifier: Modifier,
    state: InputHarianState,
    onIntent: (InputHarianIntent) -> Unit
) {
    PanelPremiumQControl(modifier = modifier, judul = "Daftar Part") {
        OutlinedTextField(
            value = state.kataKunciPart,
            onValueChange = { onIntent(InputHarianIntent.CariPart(it)) },
            placeholder = { Text("Cari part...", style = MaterialTheme.typography.bodySmall, color = TeksKontrasRendah) },
            leadingIcon = { Icon(Icons.Default.Search, null, modifier = Modifier.size(18.dp), tint = TeksKontrasRendah) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = GarisHalus)
        )

        Spacer(Modifier.height(16.dp))

        if (state.daftarPart.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Tidak ada part ditemukan", color = TeksKontrasRendah)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.daftarPart) { item ->
                    val isTerpilih = state.partTerpilih?.partId == item.partId
                    Surface(
                        onClick = { onIntent(InputHarianIntent.PilihPart(item)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.small,
                        color = if (isTerpilih) SolarYellow.copy(alpha = 0.1f) else Color.Transparent,
                        border = if (isTerpilih) androidx.compose.foundation.BorderStroke(1.dp, SolarYellow.copy(alpha = 0.5f)) else null
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(36.dp).background(if (item.totalDefect > 0) GagalMerah.copy(alpha = 0.2f) else LatarBelakangUtama, MaterialTheme.shapes.extraSmall),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = item.qtyCheck.toString(), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = if (item.totalDefect > 0) GagalMerah else TeksKontrasSedang)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.namaPart, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = if (isTerpilih) SolarYellow else TeksKontrasTinggi, maxLines = 1)
                                Text(item.nomorPart, style = MaterialTheme.typography.labelSmall, color = TeksKontrasRendah)
                            }
                            if (item.totalDefect > 0) {
                                Badge(containerColor = GagalMerah) { Text(item.totalDefect.toString(), color = Color.White) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PanelMatrixDefect(
    modifier: Modifier,
    state: InputHarianState,
    onIntent: (InputHarianIntent) -> Unit
) {
    PanelPremiumQControl(modifier = modifier, judul = "Tabel Temuan Defect") {
        val part = state.partTerpilih
        val matrix = state.matrixDefect

        if (part == null) {
            StateKosongQControl(
                ikon = Icons.Default.TouchApp,
                judul = "Pilih Part",
                pesan = "Silakan pilih part produksi dari panel kiri untuk mulai mencatat temuan defect."
            )
        } else if (matrix == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            Column {
                // Info Part Aktif
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(part.namaPart, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = SolarYellow)
                        Text("NOMOR PART: ${part.nomorPart}", style = MaterialTheme.typography.labelSmall, color = TeksKontrasRendah)
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Total Check", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TeksKontrasRendah)
                        Spacer(Modifier.width(12.dp))
                        Row(modifier = Modifier.background(LatarBelakangUtama, MaterialTheme.shapes.small).border(1.dp, GarisHalus, MaterialTheme.shapes.small), verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { onIntent(InputHarianIntent.UpdateQtyCheck(part.partId, part.qtyCheck - 1)) }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Remove, null, modifier = Modifier.size(16.dp), tint = TeksKontrasSedang)
                            }
                            
                            BasicTextField(
                                value = if (part.qtyCheck == 0) "" else part.qtyCheck.toString(),
                                onValueChange = { 
                                    val newVal = it.filter { char -> char.isDigit() }.toIntOrNull() ?: 0
                                    onIntent(InputHarianIntent.UpdateQtyCheck(part.partId, newVal))
                                },
                                modifier = Modifier.width(60.dp),
                                textStyle = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Black, 
                                    color = TeksKontrasTinggi,
                                    textAlign = TextAlign.Center
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )

                            IconButton(onClick = { onIntent(InputHarianIntent.UpdateQtyCheck(part.partId, part.qtyCheck + 1)) }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp), tint = SolarYellow)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                PembatasHalusQControl()
                Spacer(Modifier.height(16.dp))

                // Tabel Matrix
                Column(modifier = Modifier.weight(1f)) {
                    Row(modifier = Modifier.fillMaxWidth().background(LatarBelakangUtama.copy(alpha = 0.5f)).padding(8.dp)) {
                        Text("JENIS DEFECT", modifier = Modifier.weight(0.3f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = TeksKontrasRendah)
                        matrix.kolomSlotWaktu.forEach { slot ->
                            Text(slot.labelSlot, modifier = Modifier.weight(0.12f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = TeksKontrasRendah)
                        }
                        Text("TOTAL", modifier = Modifier.weight(0.1f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, textAlign = TextAlign.Center, color = TeksKontrasRendah)
                    }
                    
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(matrix.barisDefect) { baris ->
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(0.3f)) {
                                    Text(baris.namaDefect, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = TeksKontrasTinggi)
                                    Text(baris.kodeDefect, style = MaterialTheme.typography.labelSmall, color = TeksKontrasRendah)
                                }
                                baris.nilaiPerSlot.forEach { sel ->
                                    Box(modifier = Modifier.weight(0.12f), contentAlignment = Alignment.Center) {
                                        CellInput(
                                            jumlah = sel.jumlahDefect,
                                            onTambah = { onIntent(InputHarianIntent.UpdateDefect(part.partId, sel.slotWaktuId, baris.relasiPartDefectId, sel.jumlahDefect + 1)) },
                                            onKurang = { onIntent(InputHarianIntent.UpdateDefect(part.partId, sel.slotWaktuId, baris.relasiPartDefectId, sel.jumlahDefect - 1)) }
                                        )
                                    }
                                }
                                Text(baris.subtotal.toString(), modifier = Modifier.weight(0.1f), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Black, textAlign = TextAlign.Center, color = if (baris.subtotal > 0) GagalMerah else TeksKontrasRendah)
                            }
                            PembatasHalusQControl()
                        }
                    }
                }
                
                Row(modifier = Modifier.fillMaxWidth().background(LatarBelakangUtama.copy(alpha = 0.3f)).padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("TOTAL DEFECT", modifier = Modifier.weight(0.3f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = SolarYellow)
                    matrix.kolomSlotWaktu.forEach { slot ->
                        val total = matrix.ringkasan.totalPerSlot[slot.slotWaktuId] ?: 0
                        Text(total.toString(), modifier = Modifier.weight(0.12f), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Black, textAlign = TextAlign.Center, color = if (total > 0) GagalMerah else TeksKontrasSedang)
                    }
                    Text(matrix.ringkasan.totalDefectPart.toString(), modifier = Modifier.weight(0.1f), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Black, textAlign = TextAlign.Center, color = GagalMerah)
                }

                if (state.pesanValidasi != null) {
                    Spacer(Modifier.height(8.dp))
                    Surface(color = GagalMerah.copy(alpha = 0.1f), shape = MaterialTheme.shapes.extraSmall, modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Error, null, tint = GagalMerah, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(state.pesanValidasi, style = MaterialTheme.typography.labelSmall, color = GagalMerah)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CellInput(jumlah: Int, onTambah: () -> Unit, onKurang: () -> Unit) {
    Row(
        modifier = Modifier.background(if (jumlah > 0) GagalMerah.copy(alpha = 0.1f) else Color.Transparent, MaterialTheme.shapes.extraSmall).border(1.dp, if (jumlah > 0) GagalMerah.copy(alpha = 0.3f) else Color.Transparent, MaterialTheme.shapes.extraSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(24.dp).clickable { onKurang() }, contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Remove, null, modifier = Modifier.size(12.dp), tint = if (jumlah > 0) GagalMerah else TeksKontrasRendah)
        }
        Text(jumlah.toString(), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = if (jumlah > 0) GagalMerah else TeksKontrasSedang, modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
        Box(modifier = Modifier.size(24.dp).clickable { onTambah() }, contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Add, null, modifier = Modifier.size(12.dp), tint = if (jumlah > 0) GagalMerah else VibrantOrange)
        }
    }
}

@Composable
private fun PanelRingkasanQC(
    modifier: Modifier,
    state: InputHarianState,
    shellState: ShellState,
    sinkronisasiState: SinkronisasiState,
    onIntent: (InputHarianIntent) -> Unit,
    onShellIntent: (ShellIntent) -> Unit
) {
    PanelPremiumQControl(modifier = modifier, judul = "Ringkasan Harian") {
        val ringkasan = state.ringkasan
        val totalCheck = ringkasan?.totalQtyCheck ?: 0
        val totalDefect = ringkasan?.totalQtyDefect ?: 0
        val totalOk = totalCheck - totalDefect
        val rasio = if (totalCheck > 0) (totalDefect.toDouble() / totalCheck.toDouble() * 100.0) else 0.0

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ItemRingkasan(label = "Total Check", nilai = totalCheck.toString(), warna = TeksKontrasTinggi)
                ItemRingkasan(label = "Total OK", nilai = totalOk.toString(), warna = BerhasilHijau)
                ItemRingkasan(label = "Total Defect", nilai = totalDefect.toString(), warna = if (totalDefect > 0) GagalMerah else TeksKontrasSedang)
                ItemRingkasan(label = "Rasio Defect", nilai = String.format("%.2f%%", rasio), warna = if (rasio > 5.0) GagalMerah else if (rasio > 0) PeringatanKuning else TeksKontrasSedang)
            }

            sinkronisasiState.ringkasan?.let { ringkasanSinkronisasi ->
                Surface(
                    color = LatarBelakangUtama.copy(alpha = 0.25f),
                    shape = MaterialTheme.shapes.extraSmall,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GarisSubtle)
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Status antrean kirim",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = TeksKontrasRendah
                        )
                        Text(
                            text = "Menunggu ${ringkasanSinkronisasi.jumlahMenunggu} • Gagal ${ringkasanSinkronisasi.jumlahGagal} • Konflik ${ringkasanSinkronisasi.jumlahKonflik}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TeksKontrasTinggi
                        )
                        if (ringkasanSinkronisasi.jumlahKonflik > 0) {
                            Text(
                                text = "Ada data konflik yang perlu ditinjau sebelum dikirim ulang.",
                                style = MaterialTheme.typography.labelSmall,
                                color = PeringatanKuning
                            )
                        }
                    }
                }
            }
             
            Spacer(Modifier.weight(1f))

            TombolUtamaQControl(
                text = "Kirim Data",
                onClick = { onIntent(InputHarianIntent.KirimKeServer) },
                modifier = Modifier.fillMaxWidth(),
                enabled = totalCheck > 0 && !state.sedangMemuat,
                ikon = Icons.Default.CloudUpload
            )

            TombolSekunderQControl(
                text = "Reset Draft",
                onClick = { onIntent(InputHarianIntent.ResetDraft) },
                modifier = Modifier.fillMaxWidth(),
                enabled = totalCheck > 0,
                ikon = Icons.Default.DeleteSweep
            )
        }
    }
}

@Composable
private fun ItemRingkasan(label: String, nilai: String, warna: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = LatarBelakangUtama.copy(alpha = 0.3f),
        shape = MaterialTheme.shapes.extraSmall,
        border = androidx.compose.foundation.BorderStroke(1.dp, GarisSubtle)
    ) {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = TeksKontrasRendah, fontWeight = FontWeight.Bold)
            Text(nilai, style = MaterialTheme.typography.titleMedium, color = warna, fontWeight = FontWeight.Black)
        }
    }
}
