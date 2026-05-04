package id.primaraya.qcontrol.tampilan.halaman

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
        keadaan.pesanMasterData?.let { pesan ->
            val isError = pesan.startsWith("Sesi") || pesan.startsWith("Gagal") || pesan.contains("error", ignoreCase = true)
            val isLoading = pesan.contains("Menarik", ignoreCase = true) || pesan.contains("Memuat", ignoreCase = true)
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
                Text(
                    text = pesan,
                    color = warnaTeks,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }

        // ── Konten Tab ────────────────────────────────────────────────────────
        Box(modifier = Modifier.fillMaxSize()) {
            if (keadaan.sedangMenarikMasterData) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                when (keadaan.tabMasterDataAktif) {
                    TabMasterData.RINGKASAN -> TabRingkasan(keadaan, onAksi)
                    TabMasterData.PART -> TabDaftarPart(keadaan.daftarPartMaster, onAksi)
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
// Tab: Part
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun TabDaftarPart(daftar: List<Part>, onAksi: (AksiAplikasi) -> Unit) {
    if (daftar.isEmpty()) { KosongState("🔩", "Master data belum tersedia", "Tarik master data dari PGNServer agar QControl siap digunakan offline.", onAksi = { onAksi(AksiAplikasi.TarikMasterDataDariServer) }); return }
    val kolom = listOf(
        "Kode Unik" to 0.15f, 
        "Nama Part" to 0.25f, 
        "Nomor Part" to 0.15f, 
        "Material" to 0.2f, 
        "Proyek" to 0.1f, 
        "Line" to 0.15f
    )
    TabelMasterData(kolom = kolom, baris = daftar.map { listOf(it.kodeUnikPart, it.namaPart, it.nomorPart ?: "-", it.namaMaterial ?: "-", it.kodeProyek ?: "-", it.namaLineDefault ?: "-") })
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
    baris: List<List<String>>
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
                    val warnaLatar = if (indeks % 2 == 0) Color.Transparent
                                     else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.05f)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(warnaLatar)
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
