package id.primaraya.qcontrol.tampilan.halaman

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import id.primaraya.qcontrol.ranah.model.*
import id.primaraya.qcontrol.tampilan.komponen.*
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
        if (!keadaan.masterDataLokalTersedia) {
            onAksi(AksiAplikasi.MuatMasterDataLokal)
        }
    }

    if (!keadaan.masterDataLokalTersedia) {
        Box(modifier = Modifier.fillMaxSize().background(LatarBelakangKonten), contentAlignment = Alignment.Center) {
            StateKosongQControl(
                ikon = "📦",
                judul = "Master Data Belum Tersedia",
                pesan = "Tarik Master Data dari PGNServer terlebih dahulu agar Input Harian bisa digunakan secara offline.",
                onAksi = { onAksi(AksiAplikasi.PilihRute(id.primaraya.qcontrol.tampilan.navigasi.RuteAplikasi.MasterData)) },
                labelAksi = "Buka Master Data"
            )
        }
        return
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
                "Daftar Part Produksi",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = VibrantOrange
            )

            // Selector Line
            var expanded by remember { mutableStateOf(false) }
            val lineTerpilih = keadaan.daftarLineProduksiMaster.find { it.id == keadaan.draftPemeriksaanHarian?.lineId }
            
            Box {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(UkuranQControl.RadiusNormal),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TeksGelap)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(lineTerpilih?.namaLine ?: "Pilih Line...", maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    if (keadaan.daftarLineProduksiMaster.isEmpty()) {
                        DropdownMenuItem(text = { Text("Master Line Kosong") }, onClick = {})
                    }
                    keadaan.daftarLineProduksiMaster.forEach { line ->
                        DropdownMenuItem(
                            text = { 
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (line.id == lineTerpilih?.id) Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp), tint = VibrantOrange)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(line.namaLine)
                                }
                            },
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
                placeholder = { Text("Cari Part...", style = MaterialTheme.typography.bodySmall) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(UkuranQControl.RadiusNormal),
                textStyle = MaterialTheme.typography.bodyMedium
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            if (keadaan.daftarInputPartDraft.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Part tidak ditemukan", style = MaterialTheme.typography.labelSmall, color = TeksAbuAbu)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(keadaan.daftarInputPartDraft) { inputPart ->
                        ItemPartDraft(
                            inputPart = inputPart,
                            isTerpilih = keadaan.inputPartTerpilih?.partId == inputPart.partId,
                            onClick = { onAksi(AksiAplikasi.PilihInputPart(inputPart)) }
                        )
                    }
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
    val matrix = keadaan.matrixInputDefectPart

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
                Column(modifier = Modifier.weight(1f)) {
                    Text(part.namaPart, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(part.nomorPart, style = MaterialTheme.typography.bodyMedium, color = TeksAbuAbu)
                }
                
                // Info Ringkasan Part Terpilih
                if (matrix != null) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        KolomStatistikKecil("Total Check", part.qtyCheck.toString(), Color.Black)
                        KolomStatistikKecil("Total OK", matrix.ringkasan.totalOkPart.toString(), Color(0xFF16A34A))
                        KolomStatistikKecil("Total Defect", matrix.ringkasan.totalDefectPart.toString(), MaterialTheme.colorScheme.error)
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Qty Check Input
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Qty Check:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier
                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                            .padding(horizontal = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { onAksi(AksiAplikasi.UpdateQtyCheckInputPart(part.partId, part.qtyCheck - 1)) }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                        Text(
                            part.qtyCheck.toString(),
                            modifier = Modifier.widthIn(min = 40.dp),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        IconButton(onClick = { onAksi(AksiAplikasi.UpdateQtyCheckInputPart(part.partId, part.qtyCheck + 1)) }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            keadaan.pesanValidasiInputHarian?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(it, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Spacer(modifier = Modifier.height(UkuranQControl.SpasiNormal))
            HorizontalDivider()
            
            if (matrix == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = VibrantOrange)
                }
            } else if (matrix.barisDefect.isEmpty()) {
                StateKosongQControl(
                    ikon = "⚠️",
                    judul = "Template Defect Kosong",
                    pesan = keadaan.pesanKesiapanInputHarian ?: "Belum ada template defect untuk part ini. Periksa Master Data."
                )
            } else {
                Box(modifier = Modifier.fillMaxSize().padding(top = 8.dp)) {
                    MatrixDefectTable(matrix = matrix, part = part, onAksi = onAksi)
                }
            }
        }
    }
}

@Composable
private fun MatrixDefectTable(
    matrix: MatrixInputDefectPart,
    part: DraftInputPart,
    onAksi: (AksiAplikasi) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Header Row
        Row(
            modifier = Modifier.background(LatarBelakangSidebar.copy(alpha = 0.05f)).padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Jenis Defect", modifier = Modifier.weight(0.3f).padding(start = 8.dp), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
            matrix.kolomSlotWaktu.forEach { kolom ->
                Text(
                    kolom.labelSlot,
                    modifier = Modifier.weight(0.12f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Text("Total", modifier = Modifier.weight(0.1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(matrix.barisDefect) { baris ->
                BarisDefectMatrix(baris = baris, part = part, onAksi = onAksi)
                PembatasHalusQControl()
            }
        }

        // Footer Row (Summary per Slot)
        Row(
            modifier = Modifier.background(LatarBelakangSidebar.copy(alpha = 0.03f)).padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("TOTAL PER SLOT", modifier = Modifier.weight(0.3f).padding(start = 8.dp), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, color = VibrantOrange)
            matrix.kolomSlotWaktu.forEach { kolom ->
                val totalSlot = matrix.ringkasan.totalPerSlot[kolom.slotWaktuId] ?: 0
                Text(
                    totalSlot.toString(),
                    modifier = Modifier.weight(0.12f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (totalSlot > 0) GagalMerah else TeksGelap
                )
            }
            Text(
                matrix.ringkasan.totalDefectPart.toString(),
                modifier = Modifier.weight(0.1f),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.bodyLarge,
                color = GagalMerah
            )
        }
    }
}

@Composable
private fun BarisDefectMatrix(
    baris: BarisInputDefect,
    part: DraftInputPart,
    onAksi: (AksiAplikasi) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(0.3f).padding(start = 8.dp)) {
            Text(baris.namaDefect, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text(baris.kodeDefect, style = MaterialTheme.typography.labelSmall, color = TeksAbuAbu)
        }

        baris.nilaiPerSlot.forEach { sel ->
            Box(modifier = Modifier.weight(0.12f), contentAlignment = Alignment.Center) {
                CellInputDefect(
                    jumlah = sel.jumlahDefect,
                    onTambah = { onAksi(AksiAplikasi.UpdateDefectSlot(part.partId, sel.slotWaktuId, baris.relasiPartDefectId, sel.jumlahDefect + 1)) },
                    onKurang = { if (sel.jumlahDefect > 0) onAksi(AksiAplikasi.UpdateDefectSlot(part.partId, sel.slotWaktuId, baris.relasiPartDefectId, sel.jumlahDefect - 1)) }
                )
            }
        }

        Text(
            baris.subtotal.toString(),
            modifier = Modifier.weight(0.1f),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge,
            color = if (baris.subtotal > 0) GagalMerah else TeksGelap
        )
    }
}

@Composable
private fun CellInputDefect(
    jumlah: Int,
    onTambah: () -> Unit,
    onKurang: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(if (jumlah > 0) GagalMerah.copy(alpha = 0.1f) else Color.Transparent, RoundedCornerShape(UkuranQControl.RadiusKecil))
            .border(1.dp, if (jumlah > 0) GagalMerah.copy(alpha = 0.3f) else Color.Transparent, RoundedCornerShape(UkuranQControl.RadiusKecil))
            .padding(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onKurang, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(12.dp), tint = if (jumlah > 0) GagalMerah else TeksAbuAbu)
        }
        Text(
            jumlah.toString(),
            modifier = Modifier.widthIn(min = 20.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (jumlah > 0) FontWeight.Bold else FontWeight.Normal,
            color = if (jumlah > 0) GagalMerah else TeksGelap
        )
        IconButton(onClick = onTambah, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(12.dp), tint = if (jumlah > 0) GagalMerah else VibrantOrange)
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(UkuranQControl.SpasiNormal)) {
            Text("Ringkasan Pemeriksaan", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = VibrantOrange)
            Spacer(modifier = Modifier.height(UkuranQControl.SpasiNormal))

            // Stats Cards
            val totalCheck = keadaan.ringkasanInputHarian?.totalQtyCheck ?: 0
            val totalDefect = keadaan.ringkasanInputHarian?.totalQtyDefect ?: 0
            val totalOk = totalCheck - totalDefect
            val rasio = if (totalCheck > 0) (totalDefect.toDouble() / totalCheck.toDouble() * 100.0) else 0.0
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ItemStatistikRingkasan("TOTAL CHECK", totalCheck.toString(), TeksGelap)
                ItemStatistikRingkasan("TOTAL OK", totalOk.toString(), BerhasilHijau)
                ItemStatistikRingkasan("TOTAL DEFECT", totalDefect.toString(), GagalMerah)
                ItemStatistikRingkasan("RASIO DEFECT", String.format("%.2f%%", rasio), if (rasio > 0) PeringatanKuning else TeksAbuAbu)
            }

            Spacer(modifier = Modifier.height(UkuranQControl.SpasiNormal))
            HorizontalDivider(color = GarisHalus)
            Spacer(modifier = Modifier.height(UkuranQControl.SpasiNormal))

            Text("Detail Defect per Slot", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(UkuranQControl.SpasiKecil))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val daftarPerSlot = keadaan.ringkasanInputHarian?.daftarPerSlot ?: emptyList()
                if (daftarPerSlot.isEmpty()) {
                    item {
                        Text("Belum ada temuan defect harian.", style = MaterialTheme.typography.bodySmall, color = TeksAbuAbu, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 16.dp))
                    }
                } else {
                    items(daftarPerSlot) { slot ->
                        Row(
                            modifier = Modifier.fillMaxWidth().background(LatarBelakangKonten, RoundedCornerShape(UkuranQControl.RadiusKecil)).padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(slot.labelSlot, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                            Text(
                                slot.jumlah.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = GagalMerah
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(UkuranQControl.SpasiNormal))
            
            // Operational Buttons
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { onAksi(AksiAplikasi.ResetDraftInputHarian) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(UkuranQControl.RadiusNormal),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TeksAbuAbu)
                ) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reset Draft Lokal")
                }

                Button(
                    onClick = { /* Submit belum diimplementasikan */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = VibrantOrange),
                    shape = RoundedCornerShape(UkuranQControl.RadiusNormal),
                    enabled = false
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Kirim ke PGNServer")
                }
            }
            
            Text(
                "Draft tersimpan di perangkat (SQLite Lokal)",
                style = MaterialTheme.typography.labelSmall,
                color = TeksAbuAbu,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ItemStatistikRingkasan(label: String, value: String, color: Color) {
    Surface(
        color = Color(0xFFF9FAFB),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TeksAbuAbu)
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = color)
        }
    }
}

@Composable
private fun KolomStatistikKecil(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = TeksAbuAbu)
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
    }
}
