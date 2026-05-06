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
    // Inisialisasi
    LaunchedEffect(Unit) {
        if (keadaan.draftPemeriksaanHarian == null) {
            onAksi(AksiAplikasi.MuatDraftInputHarian(keadaan.tanggalPemeriksaanHarian, keadaan.lineAktif))
        }
    }

    if (!keadaan.masterDataLokalTersedia) {
        StateKosongQControl(
            ikon = "📦",
            judul = "Master Data Belum Tersedia",
            pesan = "Tarik Master Data dari PGNServer terlebih dahulu agar Input Harian bisa digunakan secara offline.",
            onAksi = { onAksi(AksiAplikasi.PilihRute(id.primaraya.qcontrol.tampilan.navigasi.RuteAplikasi.MasterData)) },
            labelAksi = "Buka Master Data"
        )
        return
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(UkuranQControl.SpasiNormal),
        horizontalArrangement = Arrangement.spacedBy(UkuranQControl.SpasiNormal)
    ) {
        // --- PANEL KIRI: PILIH PART ---
        PanelPilihPart(
            modifier = Modifier.weight(0.25f),
            keadaan = keadaan,
            onAksi = onAksi
        )

        // --- PANEL TENGAH: MATRIX INPUT ---
        PanelMatrixDefect(
            modifier = Modifier.weight(0.5f),
            keadaan = keadaan,
            onAksi = onAksi
        )

        // --- PANEL KANAN: RINGKASAN ---
        PanelRingkasanQC(
            modifier = Modifier.weight(0.25f),
            keadaan = keadaan,
            onAksi = onAksi
        )
    }
}

@Composable
private fun PanelPilihPart(
    modifier: Modifier,
    keadaan: KeadaanAplikasi,
    onAksi: (AksiAplikasi) -> Unit
) {
    PanelPremiumQControl(modifier = modifier, judul = "Pilih Part Produksi") {
        // Selector Line
        var expanded by remember { mutableStateOf(false) }
        val lineTerpilih = keadaan.daftarLineProduksiMaster.find { it.id == keadaan.draftPemeriksaanHarian?.lineId }
        
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small,
                border = androidx.compose.foundation.BorderStroke(1.dp, GarisHalus)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(lineTerpilih?.namaLine ?: "Pilih Line...", color = TeksKontrasTinggi)
                    Icon(Icons.Default.ArrowDropDown, null, tint = TeksKontrasRendah)
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

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = keadaan.kataKunciPartInputHarian,
            onValueChange = { onAksi(AksiAplikasi.UbahKataKunciInputPart(it)) },
            placeholder = { Text("Cari part...", style = MaterialTheme.typography.bodySmall, color = TeksKontrasRendah) },
            leadingIcon = { Icon(Icons.Default.Search, null, modifier = Modifier.size(18.dp), tint = TeksKontrasRendah) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = GarisHalus)
        )

        Spacer(Modifier.height(16.dp))
        PembatasHalusQControl()
        Spacer(Modifier.height(16.dp))

        if (keadaan.daftarInputPartDraft.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Part tidak ditemukan.", style = MaterialTheme.typography.labelSmall, color = TeksKontrasRendah)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(keadaan.daftarInputPartDraft) { item ->
                    val isTerpilih = keadaan.inputPartTerpilih?.partId == item.partId
                    Surface(
                        onClick = { onAksi(AksiAplikasi.PilihInputPart(item)) },
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
                                Text(item.namaPart, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = if (isTerpilih) SolarYellow else TeksKontrasTinggi)
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
    keadaan: KeadaanAplikasi,
    onAksi: (AksiAplikasi) -> Unit
) {
    PanelPremiumQControl(modifier = modifier, judul = "Matrix Defect per Slot Waktu") {
        val part = keadaan.inputPartTerpilih
        val matrix = keadaan.matrixInputDefectPart

        if (part == null) {
            StateKosongQControl(
                ikon = "👈",
                judul = "Pilih Part",
                pesan = "Silakan pilih line dan part produksi dari panel kiri untuk mulai mencatat temuan defect."
            )
        } else if (matrix == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (matrix.barisDefect.isEmpty()) {
            StateKosongQControl(
                ikon = "⚠️",
                judul = "Template Kosong",
                pesan = "Belum ada template defect valid untuk part ini. Pastikan relasi sudah diatur di Master Data."
            )
        } else {
            Column {
                // Info Part Aktif
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(part.namaPart, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = SolarYellow)
                        Text("NOMOR PART: ${part.nomorPart}", style = MaterialTheme.typography.labelSmall, color = TeksKontrasRendah)
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("QTY CHECK", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TeksKontrasRendah)
                        Spacer(Modifier.width(12.dp))
                        Row(modifier = Modifier.background(LatarBelakangUtama, MaterialTheme.shapes.small).border(1.dp, GarisHalus, MaterialTheme.shapes.small), verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { onAksi(AksiAplikasi.UpdateQtyCheckInputPart(part.partId, part.qtyCheck - 1)) }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Remove, null, modifier = Modifier.size(16.dp), tint = TeksKontrasSedang)
                            }
                            Text(part.qtyCheck.toString(), modifier = Modifier.width(50.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Black, color = TeksKontrasTinggi)
                            IconButton(onClick = { onAksi(AksiAplikasi.UpdateQtyCheckInputPart(part.partId, part.qtyCheck + 1)) }, modifier = Modifier.size(32.dp)) {
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
                    // Header Matrix
                    Row(modifier = Modifier.fillMaxWidth().background(LatarBelakangUtama.copy(alpha = 0.5f)).padding(8.dp)) {
                        Text("JENIS DEFECT", modifier = Modifier.weight(0.3f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = TeksKontrasRendah)
                        matrix.kolomSlotWaktu.forEach { slot ->
                            Text(slot.labelSlot, modifier = Modifier.weight(0.12f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = TeksKontrasRendah)
                        }
                        Text("TOTAL", modifier = Modifier.weight(0.1f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, textAlign = TextAlign.Center, color = TeksKontrasRendah)
                    }
                    
                    LazyColumn {
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
                                            onTambah = { onAksi(AksiAplikasi.UpdateDefectSlot(part.partId, sel.slotWaktuId, baris.relasiPartDefectId, sel.jumlahDefect + 1)) },
                                            onKurang = { onAksi(AksiAplikasi.UpdateDefectSlot(part.partId, sel.slotWaktuId, baris.relasiPartDefectId, sel.jumlahDefect - 1)) }
                                        )
                                    }
                                }
                                Text(baris.subtotal.toString(), modifier = Modifier.weight(0.1f), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Black, textAlign = TextAlign.Center, color = if (baris.subtotal > 0) GagalMerah else TeksKontrasRendah)
                            }
                            PembatasHalusQControl()
                        }
                    }
                }
                
                // Footer Matrix
                Row(modifier = Modifier.fillMaxWidth().background(LatarBelakangUtama.copy(alpha = 0.3f)).padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("TOTAL DEFECT PER SLOT", modifier = Modifier.weight(0.3f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = SolarYellow)
                    matrix.kolomSlotWaktu.forEach { slot ->
                        val total = matrix.ringkasan.totalPerSlot[slot.slotWaktuId] ?: 0
                        Text(total.toString(), modifier = Modifier.weight(0.12f), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Black, textAlign = TextAlign.Center, color = if (total > 0) GagalMerah else TeksKontrasSedang)
                    }
                    Text(matrix.ringkasan.totalDefectPart.toString(), modifier = Modifier.weight(0.1f), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Black, textAlign = TextAlign.Center, color = GagalMerah)
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
    keadaan: KeadaanAplikasi,
    onAksi: (AksiAplikasi) -> Unit
) {
    PanelPremiumQControl(modifier = modifier, judul = "Ringkasan Pemeriksaan") {
        val totalCheck = keadaan.ringkasanInputHarian?.totalQtyCheck ?: 0
        val totalDefect = keadaan.ringkasanInputHarian?.totalQtyDefect ?: 0
        val totalOk = totalCheck - totalDefect
        val rasio = if (totalCheck > 0) (totalDefect.toDouble() / totalCheck.toDouble() * 100.0) else 0.0

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            KartuInfoPemeriksaan(label = "Total Check", nilai = totalCheck.toString(), warna = TeksKontrasTinggi)
            KartuInfoPemeriksaan(label = "Total OK", nilai = totalOk.toString(), warna = BerhasilHijau)
            KartuInfoPemeriksaan(label = "Total Defect", nilai = totalDefect.toString(), warna = GagalMerah)
            KartuInfoPemeriksaan(label = "Rasio Defect", nilai = String.format("%.2f%%", rasio), warna = if (rasio > 0) PeringatanKuning else TeksKontrasSedang)
            
            Spacer(Modifier.height(16.dp))
            PembatasHalusQControl()
            Spacer(Modifier.height(16.dp))
            
            TombolSekunderQControl(
                text = "Reset Draft Lokal",
                onClick = { onAksi(AksiAplikasi.ResetDraftInputHarian) },
                modifier = Modifier.fillMaxWidth(),
                ikon = Icons.Default.DeleteSweep
            )
            
            TombolUtamaQControl(
                text = "Kirim ke PGNServer",
                onClick = { /* Fase berikutnya */ },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                ikon = Icons.Default.CloudUpload
            )
            
            Text(
                text = "Draft tersimpan otomatis di SQLite lokal.",
                style = MaterialTheme.typography.labelSmall,
                color = TeksKontrasRendah,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}
