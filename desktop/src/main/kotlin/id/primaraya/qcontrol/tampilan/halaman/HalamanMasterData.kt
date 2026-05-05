package id.primaraya.qcontrol.tampilan.halaman

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import id.primaraya.qcontrol.ranah.model.*
import id.primaraya.qcontrol.tampilan.state.AksiAplikasi
import id.primaraya.qcontrol.tampilan.state.KeadaanAplikasi
import id.primaraya.qcontrol.tampilan.state.TabMasterData
import id.primaraya.qcontrol.tema.DeepAmber
import id.primaraya.qcontrol.tema.TeksAbuAbu
import id.primaraya.qcontrol.tema.LatarBelakangKonten
import id.primaraya.qcontrol.tema.SolarYellow
import id.primaraya.qcontrol.tema.UkuranQControl
import id.primaraya.qcontrol.tema.VibrantOrange

@Composable
fun HalamanMasterData(
    keadaan: KeadaanAplikasi,
    onAksi: (AksiAplikasi) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(UkuranQControl.SpasiNormal),
        verticalArrangement = Arrangement.spacedBy(UkuranQControl.SpasiNormal)
    ) {
        // ── Header ───────────────────────────────────────────────────────────
        BagianHeader(keadaan = keadaan, onAksi = onAksi)

        // ── Tab Bar ───────────────────────────────────────────────────────────
        BagianTabMasterData(tabAktif = keadaan.tabMasterDataAktif, onAksi = onAksi)

        // ── Search (hanya tampil untuk tab yang bisa difilter) ────────────────
        if (keadaan.tabMasterDataAktif in listOf(
                TabMasterData.PART, TabMasterData.JENIS_DEFECT, TabMasterData.MATERIAL
            )
        ) {
            OutlinedTextField(
                value = keadaan.kataKunciMasterData,
                onValueChange = { onAksi(AksiAplikasi.UbahKataKunciMasterData(it)) },
                label = { Text("Cari…") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // ── Pesan status (tarik / error) ─────────────────────────────────────
        val sesiInvalid = keadaan.sesiHeadQCTidakValid
        val pesanMaster = keadaan.pesanMasterData

        if (sesiInvalid || pesanMaster != null) {
            val pesan = if (sesiInvalid) "Sesi HeadQC sudah berakhir. Tarik data server perlu login ulang." else pesanMaster ?: ""
            val isError = sesiInvalid || pesan.startsWith("Gagal") || pesan.contains("error", ignoreCase = true) || pesan.contains("Unauthorized", ignoreCase = true)
            val isLoading = !sesiInvalid && (pesan.contains("Menarik", ignoreCase = true) || pesan.contains("Memuat", ignoreCase = true))
            
            val warnaTeks = if (isError) MaterialTheme.colorScheme.error 
                            else if (isLoading) Color(0xFFB45309) 
                            else Color(0xFF16A34A)
            val warnaLatar = if (isError) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                             else if (isLoading) Color(0xFFFEF3C7) 
                             else Color(0xFFDCFCE7)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = warnaLatar
            ) {
                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = pesan,
                        color = warnaTeks,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f)
                    )
                    if (sesiInvalid) {
                        Button(
                            onClick = { onAksi(AksiAplikasi.Logout) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.height(28.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("Logout & Login Ulang", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        // ── Konten Tab ────────────────────────────────────────────────────────
        Box(modifier = Modifier.fillMaxSize()) {
            if (keadaan.sedangMenarikMasterData) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                when (keadaan.tabMasterDataAktif) {
                    TabMasterData.RINGKASAN -> TabRingkasan(keadaan, onAksi)
                    TabMasterData.PART -> TabDaftarPart(keadaan, onAksi)
                    TabMasterData.JENIS_DEFECT -> TabDaftarJenisDefect(keadaan.daftarJenisDefectMaster, onAksi)
                    TabMasterData.MATERIAL -> TabDaftarMaterial(keadaan.daftarMaterialMaster, onAksi)
                    TabMasterData.SLOT_WAKTU -> TabDaftarSlotWaktu(keadaan.daftarSlotWaktuMaster, onAksi)
                    TabMasterData.LINE_PRODUKSI -> TabDaftarLineProduksi(keadaan.daftarLineProduksiMaster, onAksi)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Header
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun BagianHeader(keadaan: KeadaanAplikasi, onAksi: (AksiAplikasi) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Master Data Referensi",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "Read-only dari PGNServer",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            keadaan.ringkasanMasterData?.let { r ->
                Text(
                    text = "Terakhir diperbarui: ${r.ditarikPada}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TeksAbuAbu
                )
            } ?: Text(
                text = "Data belum pernah ditarik dari server.",
                style = MaterialTheme.typography.labelSmall,
                color = TeksAbuAbu
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { onAksi(AksiAplikasi.MuatMasterDataLokal) },
                enabled = !keadaan.sedangMenarikMasterData,
                colors = ButtonDefaults.outlinedButtonColors(),
                border = ButtonDefaults.outlinedButtonBorder
            ) {
                Text("Muat Data Lokal")
            }
            Button(
                onClick = { onAksi(AksiAplikasi.TarikMasterDataDariServer) },
                enabled = !keadaan.sedangMenarikMasterData,
                colors = ButtonDefaults.buttonColors(containerColor = DeepAmber)
            ) {
                if (keadaan.sedangMenarikMasterData) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Menarik…")
                } else {
                    Text("↓  Tarik dari Server")
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Tab Bar
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun BagianTabMasterData(tabAktif: TabMasterData, onAksi: (AksiAplikasi) -> Unit) {
    ScrollableTabRow(
        selectedTabIndex = TabMasterData.values().indexOf(tabAktif),
        edgePadding = 0.dp,
        containerColor = Color.Transparent,
        contentColor = DeepAmber,
        divider = { HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)) }
    ) {
        TabMasterData.values().forEach { tab ->
            Tab(
                selected = tab == tabAktif,
                onClick = { onAksi(AksiAplikasi.PilihTabMasterData(tab)) },
                text = {
                    Text(
                        text = tab.label,
                        fontWeight = if (tab == tabAktif) FontWeight.Bold else FontWeight.Normal,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Tab: Ringkasan
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun TabRingkasan(keadaan: KeadaanAplikasi, onAksi: (AksiAplikasi) -> Unit) {
    val ringkasan = keadaan.ringkasanMasterData

    if (ringkasan == null) {
        KosongState(
            ikon = "📂",
            judul = "Master data belum tersedia",
            pesan = "Tarik master data dari PGNServer agar QControl siap digunakan offline.",
            onAksi = { onAksi(AksiAplikasi.TarikMasterDataDariServer) }
        )
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KartuRingkasan(modifier = Modifier.weight(1f), label = "Part", nilai = "${ringkasan.jumlahPart}", warna = DeepAmber)
            KartuRingkasan(modifier = Modifier.weight(1f), label = "Jenis Defect", nilai = "${ringkasan.jumlahJenisDefect}", warna = VibrantOrange)
            KartuRingkasan(modifier = Modifier.weight(1f), label = "Material", nilai = "${ringkasan.jumlahMaterial}", warna = SolarYellow)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KartuRingkasan(modifier = Modifier.weight(1f), label = "Slot Waktu", nilai = "${ringkasan.jumlahSlotWaktu}", warna = Color(0xFF0EA5E9))
            KartuRingkasan(modifier = Modifier.weight(1f), label = "Line Produksi", nilai = "${ringkasan.jumlahLineProduksi}", warna = Color(0xFF8B5CF6))
            Spacer(modifier = Modifier.weight(1f))
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f),
            border = ButtonDefaults.outlinedButtonBorder
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("ℹ️  Informasi Cache", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                BarisMeta("Versi master data", ringkasan.versiMasterData)
                BarisMeta("Ditarik pada", ringkasan.ditarikPada)
            }
        }
    }
}

@Composable
private fun KartuRingkasan(modifier: Modifier = Modifier, label: String, nilai: String, warna: Color) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(nilai, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = warna)
            Text(label, style = MaterialTheme.typography.labelSmall, color = TeksAbuAbu, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun BarisMeta(kunci: String, nilai: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(kunci, style = MaterialTheme.typography.bodySmall, color = TeksAbuAbu, modifier = Modifier.width(130.dp))
        Text(nilai, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Tab: Part (dengan SplitView Detail Defect)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun TabDaftarPart(keadaan: KeadaanAplikasi, onAksi: (AksiAplikasi) -> Unit) {
    val daftar = keadaan.daftarPartMaster
    if (daftar.isEmpty()) { 
        KosongState("🔩", "Master data belum tersedia", "Tarik master data dari PGNServer agar QControl siap digunakan offline.", onAksi = { onAksi(AksiAplikasi.TarikMasterDataDariServer) })
        return 
    }

    val kolom = listOf(
        "Kode Unik" to 0.15f, 
        "Nama Part" to 0.35f, 
        "Nomor Part" to 0.20f, 
        "Line" to 0.30f
    )

    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(modifier = Modifier.weight(1f)) {
            TabelMasterData(
                kolom = kolom, 
                baris = daftar.map { listOf(it.kodeUnikPart, it.namaPart, it.nomorPart ?: "-", it.namaLineDefault ?: "-") },
                itemTerpilihIndeks = if (keadaan.partMasterTerpilih != null) daftar.indexOf(keadaan.partMasterTerpilih) else -1,
                onKlikBaris = { indeks ->
                    val part = daftar.getOrNull(indeks)
                    onAksi(AksiAplikasi.PilihPartMaster(if (part == keadaan.partMasterTerpilih) null else part))
                }
            )
        }

        if (keadaan.partMasterTerpilih != null) {
            PanelDetailPart(
                part = keadaan.partMasterTerpilih,
                daftarTemplate = keadaan.daftarTemplateDefectPart,
                pesanStatus = keadaan.pesanTemplateDefectPart,
                onTutup = { onAksi(AksiAplikasi.PilihPartMaster(null)) }
            )
        }
    }
}

@Composable
private fun PanelDetailPart(
    part: Part,
    daftarTemplate: List<TemplateDefectPart>,
    pesanStatus: String?,
    onTutup: () -> Unit
) {
    Surface(
        modifier = Modifier.width(450.dp).fillMaxHeight(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Panel
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Template Defect Part",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = VibrantOrange
                    )
                    Text(
                        text = "${part.kodeUnikPart} - ${part.namaPart}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TeksAbuAbu,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(onClick = onTutup, modifier = Modifier.size(24.dp)) {
                    Text("✕", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
            
            // Pesan Status (Loading/Error/Info)
            pesanStatus?.let { pesan ->
                Spacer(Modifier.height(8.dp))
                Surface(
                    color = SolarYellow.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = pesan,
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFB45309)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            if (daftarTemplate.isEmpty() && pesanStatus == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Belum ada template defect valid untuk part ini.\nPastikan relasi sudah diatur di server.",
                        style = MaterialTheme.typography.labelSmall,
                        color = TeksAbuAbu,
                        textAlign = TextAlign.Center
                    )
                }
            } else if (daftarTemplate.isNotEmpty()) {
                // Header Tabel Detail
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("POS", modifier = Modifier.width(40.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TeksAbuAbu)
                    Text("KODE", modifier = Modifier.width(60.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TeksAbuAbu)
                    Text("NAMA DEFECT", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TeksAbuAbu)
                    Text("AKTIF", modifier = Modifier.width(45.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TeksAbuAbu, textAlign = TextAlign.Center)
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(daftarTemplate) { t ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(4.dp),
                            color = if (t.aktif) Color.Transparent else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.05f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Posisi Tampilan (A-L, dll)
                                Surface(
                                    color = if (t.aktif) DeepAmber else Color.Gray,
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.width(36.dp)
                                ) {
                                    Text(
                                        text = t.kodeTampilanDefect ?: "-",
                                        modifier = Modifier.padding(vertical = 2.dp),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        textAlign = TextAlign.Center
                                    )
                                }

                                Spacer(Modifier.width(12.dp))

                                // Info Defect
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = t.kodeDefect ?: "-",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${t.namaDefect ?: "-"} (${t.namaKategori ?: "-"})",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TeksAbuAbu,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                // Status
                                Text(
                                    text = if (t.aktif) "Ya" else "Tdk",
                                    modifier = Modifier.width(45.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    textAlign = TextAlign.Center,
                                    color = if (t.aktif) Color(0xFF16A34A) else MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
                
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "* Data ini digunakan untuk layout input defect harian.",
                    style = MaterialTheme.typography.labelSmall,
                    color = TeksAbuAbu,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Tab: Jenis Defect
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun TabDaftarJenisDefect(daftar: List<JenisDefect>, onAksi: (AksiAplikasi) -> Unit) {
    if (daftar.isEmpty()) { KosongState("⚠️", "Master data belum tersedia", "Tarik master data dari PGNServer agar QControl siap digunakan offline.", onAksi = { onAksi(AksiAplikasi.TarikMasterDataDariServer) }); return }
    val kolom = listOf("Kode Defect" to 0.2f, "Nama Defect" to 0.4f, "Kategori" to 0.3f, "Status" to 0.1f)
    TabelMasterData(kolom = kolom, baris = daftar.map { listOf(it.kodeDefect, it.namaDefect, it.namaKategori ?: "-", if (it.aktif) "Aktif" else "Nonaktif") })
}

// ─────────────────────────────────────────────────────────────────────────────
// Tab: Material
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun TabDaftarMaterial(daftar: List<Material>, onAksi: (AksiAplikasi) -> Unit) {
    if (daftar.isEmpty()) { KosongState("🏭", "Master data belum tersedia", "Tarik master data dari PGNServer agar QControl siap digunakan offline.", onAksi = { onAksi(AksiAplikasi.TarikMasterDataDariServer) }); return }
    val kolom = listOf("Kode Material" to 0.2f, "Nama Material" to 0.6f, "Status" to 0.2f)
    TabelMasterData(kolom = kolom, baris = daftar.map { listOf(it.kodeMaterial ?: "-", it.namaMaterial, if (it.aktif) "Aktif" else "Nonaktif") })
}

// ─────────────────────────────────────────────────────────────────────────────
// Tab: Slot Waktu
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun TabDaftarSlotWaktu(daftar: List<SlotWaktu>, onAksi: (AksiAplikasi) -> Unit) {
    if (daftar.isEmpty()) { KosongState("🕐", "Master data belum tersedia", "Tarik master data dari PGNServer agar QControl siap digunakan offline.", onAksi = { onAksi(AksiAplikasi.TarikMasterDataDariServer) }); return }
    val kolom = listOf("Label Slot" to 0.3f, "Jam Mulai" to 0.2f, "Jam Selesai" to 0.2f, "Urutan" to 0.15f, "Status" to 0.15f)
    TabelMasterData(
        kolom = kolom,
        baris = daftar.map { listOf(it.labelSlot, it.jamMulai ?: "-", it.jamSelesai ?: "-", it.urutanTampil.toString(), if (it.aktif) "Aktif" else "Nonaktif") }
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Tab: Line Produksi
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun TabDaftarLineProduksi(daftar: List<LineProduksi>, onAksi: (AksiAplikasi) -> Unit) {
    if (daftar.isEmpty()) { KosongState("🏗️", "Master data belum tersedia", "Tarik master data dari PGNServer agar QControl siap digunakan offline.", onAksi = { onAksi(AksiAplikasi.TarikMasterDataDariServer) }); return }
    val kolom = listOf("Kode Line" to 0.2f, "Nama Line" to 0.6f, "Status" to 0.2f)
    TabelMasterData(
        kolom = kolom,
        baris = daftar.map { listOf(it.kodeLine, it.namaLine, if (it.aktif) "Aktif" else "Nonaktif") }
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Komponen Generic: Tabel
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun TabelMasterData(
    kolom: List<Pair<String, Float>>,
    baris: List<List<String>>,
    itemTerpilihIndeks: Int = -1,
    onKlikBaris: ((Int) -> Unit)? = null
) {
    val bentukKartu = RoundedCornerShape(12.dp)
    Surface(
        modifier = Modifier.fillMaxSize(),
        shape = bentukKartu,
        shadowElevation = 1.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column {
            // Header baris
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LatarBelakangKonten.copy(alpha = 0.7f))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                kolom.forEach { (label, bobot) ->
                    Text(
                        text = label.uppercase(),
                        modifier = Modifier.weight(bobot),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = TeksAbuAbu,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            // Data baris
            LazyColumn {
                itemsIndexed(baris) { indeks, kolBaris ->
                    val isTerpilih = indeks == itemTerpilihIndeks
                    val warnaLatar = if (isTerpilih) VibrantOrange.copy(alpha = 0.1f)
                                     else if (indeks % 2 == 0) Color.Transparent
                                     else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.05f)
                    
                    val borderModifier = if (isTerpilih) Modifier.border(1.dp, VibrantOrange.copy(alpha = 0.3f)) else Modifier

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(borderModifier)
                            .background(warnaLatar)
                            .then(if (onKlikBaris != null) Modifier.clickable { onKlikBaris(indeks) } else Modifier)
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        kolom.forEachIndexed { i, (_, bobot) ->
                            Text(
                                text = kolBaris.getOrElse(i) { "-" },
                                modifier = Modifier.weight(bobot),
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    if (indeks < baris.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Komponen Generic: Empty State
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun KosongState(ikon: String, judul: String, pesan: String, onAksi: (() -> Unit)? = null) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(ikon, style = MaterialTheme.typography.displayMedium)
            Text(judul, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(
                pesan,
                style = MaterialTheme.typography.bodySmall,
                color = TeksAbuAbu,
                textAlign = TextAlign.Center
            )
            if (onAksi != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onAksi,
                    colors = ButtonDefaults.buttonColors(containerColor = SolarYellow, contentColor = Color.Black)
                ) {
                    Text("Tarik Master Data dari Server", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
