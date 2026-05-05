package id.primaraya.qcontrol.tampilan.halaman

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.primaraya.qcontrol.ranah.model.*
import id.primaraya.qcontrol.tampilan.state.AksiAplikasi
import id.primaraya.qcontrol.tampilan.state.KeadaanAplikasi
import id.primaraya.qcontrol.tema.*

@Composable
fun HalamanInputHarian(
    keadaan: KeadaanAplikasi,
    onAksi: (AksiAplikasi) -> Unit
) {
    // Jalankan inisialisasi jika belum ada draft
    LaunchedEffect(Unit) {
        if (keadaan.draftPemeriksaanHarian == null) {
            onAksi(AksiAplikasi.MuatDraftInputHarian(keadaan.tanggalPemeriksaanHarian, keadaan.lineAktif))
        }
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(LatarBelakangKonten)
            .padding(UkuranQControl.SpasiNormal),
        horizontalArrangement = Arrangement.spacedBy(UkuranQControl.SpasiNormal)
    ) {
        // --- PANEL KIRI: DAFTAR PART ---
        PanelKiriPart(
            modifier = Modifier.weight(0.25f),
            keadaan = keadaan,
            onAksi = onAksi
        )

        // --- PANEL TENGAH: INPUT DEFECT ---
        PanelTengahDefect(
            modifier = Modifier.weight(0.5f),
            keadaan = keadaan,
            onAksi = onAksi
        )

        // --- PANEL KANAN: RINGKASAN ---
        PanelKananRingkasan(
            modifier = Modifier.weight(0.25f),
            keadaan = keadaan,
            onAksi = onAksi
        )
    }
}

@Composable
private fun PanelKiriPart(
    modifier: Modifier,
    keadaan: KeadaanAplikasi,
    onAksi: (AksiAplikasi) -> Unit
) {
    Card(
        modifier = modifier.fillMaxHeight(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(UkuranQControl.SpasiKecil),
            verticalArrangement = Arrangement.spacedBy(UkuranQControl.SpasiKecil)
        ) {
            Text(
                "Daftar Part",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = VibrantOrange
            )

            // Selector Line
            var expanded by remember { mutableStateOf(false) }
            val lineTerpilih = keadaan.daftarLineProduksiMaster.find { it.id == keadaan.draftPemeriksaanHarian?.lineId }?.namaLine ?: keadaan.lineAktif
            
            Box {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(lineTerpilih)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    keadaan.daftarLineProduksiMaster.forEach { line ->
                        DropdownMenuItem(
                            text = { Text(line.namaLine) },
                            onClick = {
                                onAksi(AksiAplikasi.MuatDraftInputHarian(keadaan.tanggalPemeriksaanHarian, line.id))
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = keadaan.kataKunciPartInputHarian,
                onValueChange = { onAksi(AksiAplikasi.UbahKataKunciInputPart(it)) },
                placeholder = { Text("Cari Part...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(keadaan.daftarInputPartDraft) { inputPart ->
                    ItemPartDraft(
                        inputPart = inputPart,
                        isTerpilih = keadaan.inputPartTerpilih?.id == inputPart.id,
                        onClick = { onAksi(AksiAplikasi.PilihInputPart(inputPart)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ItemPartDraft(
    inputPart: DraftInputPart,
    isTerpilih: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = if (isTerpilih) VibrantOrange.copy(alpha = 0.1f) else Color.Transparent,
        border = if (isTerpilih) BorderStroke(1.dp, VibrantOrange) else null
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(if (inputPart.totalDefect > 0) MaterialTheme.colorScheme.errorContainer else Color(0xFFF3F4F6), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = inputPart.qtyCheck.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (inputPart.totalDefect > 0) MaterialTheme.colorScheme.error else TeksAbuAbu
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    inputPart.namaPart,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    inputPart.nomorPart,
                    style = MaterialTheme.typography.bodySmall,
                    color = TeksAbuAbu
                )
            }
            if (inputPart.totalDefect > 0) {
                Badge(containerColor = MaterialTheme.colorScheme.error) {
                    Text(inputPart.totalDefect.toString(), color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun PanelTengahDefect(
    modifier: Modifier,
    keadaan: KeadaanAplikasi,
    onAksi: (AksiAplikasi) -> Unit
) {
    val part = keadaan.inputPartTerpilih

    Card(
        modifier = modifier.fillMaxHeight(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        if (part == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Silakan pilih Part terlebih dahulu", color = TeksAbuAbu)
            }
            return@Card
        }

        Column(modifier = Modifier.padding(UkuranQControl.SpasiNormal)) {
            // Header Part Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(part.namaPart, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(part.nomorPart, style = MaterialTheme.typography.bodyMedium, color = TeksAbuAbu)
                }
                
                // Qty Check Input
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Qty Check:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier
                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                            .padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { if (part.qtyCheck > 0) onAksi(AksiAplikasi.UpdateQtyCheckInputPart(part.partId, part.qtyCheck - 1)) }) {
                            Icon(Icons.Default.Remove, contentDescription = null)
                        }
                        Text(
                            part.qtyCheck.toString(),
                            modifier = Modifier.widthIn(min = 40.dp),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { onAksi(AksiAplikasi.UpdateQtyCheckInputPart(part.partId, part.qtyCheck + 1)) }) {
                            Icon(Icons.Default.Add, contentDescription = null)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(UkuranQControl.SpasiNormal))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(UkuranQControl.SpasiNormal))

            Text("Defect Slots", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(UkuranQControl.SpasiKecil))

            if (keadaan.daftarDefectSlotDraft.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada template defect untuk part ini", color = TeksAbuAbu)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 140.dp),
                    horizontalArrangement = Arrangement.spacedBy(UkuranQControl.SpasiKecil),
                    verticalArrangement = Arrangement.spacedBy(UkuranQControl.SpasiKecil)
                ) {
                    items(keadaan.daftarDefectSlotDraft) { slot ->
                        ItemDefectSlot(slot = slot, onAksi = onAksi)
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemDefectSlot(
    slot: DraftInputDefectSlot,
    onAksi: (AksiAplikasi) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (slot.jumlahDefect > 0) MaterialTheme.colorScheme.errorContainer else Color(0xFFF9FAFB),
        border = BorderStroke(1.dp, if (slot.jumlahDefect > 0) MaterialTheme.colorScheme.error else Color.LightGray)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                slot.namaDefect,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilledIconButton(
                    onClick = { 
                        if (slot.jumlahDefect > 0) {
                            onAksi(AksiAplikasi.UpdateDefectSlot(slot.inputPartId, slot.slotWaktuId, slot.relasiPartDefectId, slot.jumlahDefect - 1))
                        }
                    },
                    modifier = Modifier.size(32.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = if (slot.jumlahDefect > 0) MaterialTheme.colorScheme.error else Color.LightGray)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = null)
                }
                
                Text(
                    slot.jumlahDefect.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = if (slot.jumlahDefect > 0) MaterialTheme.colorScheme.error else TeksAbuAbu
                )
                
                FilledIconButton(
                    onClick = { 
                        onAksi(AksiAplikasi.UpdateDefectSlot(slot.inputPartId, slot.slotWaktuId, slot.relasiPartDefectId, slot.jumlahDefect + 1))
                    },
                    modifier = Modifier.size(32.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = if (slot.jumlahDefect > 0) MaterialTheme.colorScheme.error else VibrantOrange)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        }
    }
}

@Composable
private fun PanelKananRingkasan(
    modifier: Modifier,
    keadaan: KeadaanAplikasi,
    onAksi: (AksiAplikasi) -> Unit
) {
    Card(
        modifier = modifier.fillMaxHeight(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(UkuranQControl.SpasiNormal)) {
            Text("Ringkasan", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = VibrantOrange)
            Spacer(modifier = Modifier.height(UkuranQControl.SpasiNormal))

            // Total Qty Check & OK
            val totalCheck = keadaan.ringkasanInputHarian?.totalQtyCheck ?: 0
            val totalDefect = keadaan.ringkasanInputHarian?.totalQtyDefect ?: 0
            val totalOk = totalCheck - totalDefect
            
            Surface(
                color = SolarYellow.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    KolomStatistik("TOTAL CHECK", totalCheck.toString(), Color.Black)
                    KolomStatistik("TOTAL OK", totalOk.toString(), Color(0xFF16A34A))
                    KolomStatistik("TOTAL DEFECT", totalDefect.toString(), MaterialTheme.colorScheme.error)
                }
            }

            Spacer(modifier = Modifier.height(UkuranQControl.SpasiNormal))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(UkuranQControl.SpasiNormal))

            Text("Detail Defect", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(UkuranQControl.SpasiKecil))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(keadaan.ringkasanInputHarian?.daftarDefect ?: emptyList<DefectTerhitung>()) { defect ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(defect.namaDefect, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                        Text(
                            defect.jumlah.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(UkuranQControl.SpasiNormal))
            
            Button(
                onClick = { /* Submit belum diimplementasikan sesuai target fase ini */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = VibrantOrange),
                shape = RoundedCornerShape(8.dp),
                enabled = false // Belum diaktifkan di fase 2E-A
            ) {
                Icon(Icons.Default.CloudUpload, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Simpan & Kirim")
            }
            Text(
                "Submit dinonaktifkan pada fase ini (Fondasi Lokal Only)",
                style = MaterialTheme.typography.labelSmall,
                color = TeksAbuAbu,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun KolomStatistik(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = TeksAbuAbu)
        Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = color)
    }
}
