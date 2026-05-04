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
            val warnaTeks = if (isError) MaterialTheme.colorScheme.error else Color(0xFF16A34A)
            val warnaLatar = if (isError) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
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
                    TabMasterData.RINGKASAN -> TabRingkasan(keadaan)
                    TabMasterData.PART -> TabDaftarPart(keadaan.daftarPartMaster)
                    TabMasterData.JENIS_DEFECT -> TabDaftarJenisDefect(keadaan.daftarJenisDefectMaster)
                    TabMasterData.MATERIAL -> TabDaftarMaterial(keadaan.daftarMaterialMaster)
                    TabMasterData.SLOT_WAKTU -> TabDaftarSlotWaktu(keadaan.daftarSlotWaktuMaster)
                    TabMasterData.LINE_PRODUKSI -> TabDaftarLineProduksi(keadaan.daftarLineProduksiMaster)
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
        Column {
            Text(
                text = "Master Data Referensi",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
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
private fun TabRingkasan(keadaan: KeadaanAplikasi) {
    val ringkasan = keadaan.ringkasanMasterData

    if (ringkasan == null) {
        KosongState(
            ikon = "📂",
            judul = "Belum Ada Master Data",
            pesan = "Tekan tombol \"Tarik dari Server\" untuk mengunduh\nmaster data referensi dari PGNServer."
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
private fun TabDaftarPart(daftar: List<Part>) {
    if (daftar.isEmpty()) { KosongState("🔩", "Tidak Ada Part", "Tarik master data atau coba kata kunci lain."); return }
    val kolom = listOf("Kode Part" to 0.25f, "Nama Part" to 0.45f, "Kode Material" to 0.3f)
    TabelMasterData(kolom = kolom, baris = daftar.map { listOf(it.kodeUnikPart, it.namaPart, it.kodeMaterial ?: "-") })
}

// ─────────────────────────────────────────────────────────────────────────────
// Tab: Jenis Defect
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun TabDaftarJenisDefect(daftar: List<JenisDefect>) {
    if (daftar.isEmpty()) { KosongState("⚠️", "Tidak Ada Jenis Defect", "Tarik master data atau coba kata kunci lain."); return }
    val kolom = listOf("Kode Defect" to 0.25f, "Nama Defect" to 0.45f, "Kategori" to 0.3f)
    TabelMasterData(kolom = kolom, baris = daftar.map { listOf(it.kodeDefect, it.namaDefect, it.namaKategori ?: "-") })
}

// ─────────────────────────────────────────────────────────────────────────────
// Tab: Material
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun TabDaftarMaterial(daftar: List<Material>) {
    if (daftar.isEmpty()) { KosongState("🏭", "Tidak Ada Material", "Tarik master data atau coba kata kunci lain."); return }
    val kolom = listOf("Kode Material" to 0.35f, "Nama Material" to 0.65f)
    TabelMasterData(kolom = kolom, baris = daftar.map { listOf(it.kodeMaterial ?: "-", it.namaMaterial) })
}

// ─────────────────────────────────────────────────────────────────────────────
// Tab: Slot Waktu
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun TabDaftarSlotWaktu(daftar: List<SlotWaktu>) {
    if (daftar.isEmpty()) { KosongState("🕐", "Tidak Ada Slot Waktu", "Tarik master data dari server."); return }
    val kolom = listOf("Kode Slot" to 0.25f, "Label" to 0.35f, "Mulai" to 0.2f, "Selesai" to 0.2f)
    TabelMasterData(
        kolom = kolom,
        baris = daftar.map { listOf(it.kodeSlot, it.labelSlot, it.jamMulai ?: "-", it.jamSelesai ?: "-") }
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Tab: Line Produksi
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun TabDaftarLineProduksi(daftar: List<LineProduksi>) {
    if (daftar.isEmpty()) { KosongState("🏗️", "Tidak Ada Line Produksi", "Tarik master data dari server."); return }
    val kolom = listOf("Kode Line" to 0.25f, "Nama Line" to 0.75f)
    TabelMasterData(
        kolom = kolom,
        baris = daftar.map { listOf(it.kodeLine, it.namaLine) }
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
private fun KosongState(ikon: String, judul: String, pesan: String) {
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
        }
    }
}
