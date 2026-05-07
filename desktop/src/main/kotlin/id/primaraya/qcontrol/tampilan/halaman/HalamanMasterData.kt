package id.primaraya.qcontrol.tampilan.halaman

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
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
import id.primaraya.qcontrol.tampilan.state.TabMasterData
import id.primaraya.qcontrol.tema.*

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
        HeaderHalamanQControl(
            judul = "Master Data QC",
            subtitle = "Referensi resmi dari PGNServer untuk operasional offline.",
            aksi = {
                TombolSekunderQControl(
                    text = "Muat Lokal",
                    onClick = { onAksi(AksiAplikasi.MuatMasterDataLokal) },
                    enabled = !keadaan.sedangMenarikMasterData
                )
                Spacer(Modifier.width(12.dp))
                TombolUtamaQControl(
                    text = "Tarik dari Server",
                    onClick = { onAksi(AksiAplikasi.TarikMasterDataDariServer) },
                    ikon = Icons.Default.CloudDownload,
                    sedangMemuat = keadaan.sedangMenarikMasterData
                )
            }
        )

        // ── Status Bar ───────────────────────────────────────────────────────
        PanelPremiumQControl {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ChipStatusQControl(label = "HANYA BACA", warna = TeksKontrasRendah)
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = "Terakhir diperbarui: ${keadaan.ringkasanMasterData?.ditarikPada ?: "Belum pernah"}",
                        style = MaterialTheme.typography.labelMedium,
                        color = TeksKontrasSedang
                    )
                }
                
                if (keadaan.sesiHeadQCTidakValid) {
                    ChipStatusQControl(label = "PERLU LOGIN ULANG", warna = GagalMerah)
                }
            }
        }

        // ── Tab Bar & Search ────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BagianTabMasterData(tabAktif = keadaan.tabMasterDataAktif, onAksi = onAksi)
            
            if (keadaan.tabMasterDataAktif in listOf(TabMasterData.PART, TabMasterData.JENIS_DEFECT, TabMasterData.MATERIAL)) {
                OutlinedTextField(
                    value = keadaan.kataKunciMasterData,
                    onValueChange = { onAksi(AksiAplikasi.UbahKataKunciMasterData(it)) },
                    placeholder = { Text("Cari...", color = TeksKontrasRendah) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = TeksKontrasRendah) },
                    singleLine = true,
                    modifier = Modifier.width(300.dp),
                    shape = MaterialTheme.shapes.small,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = LatarBelakangUtama.copy(alpha = 0.3f),
                        focusedContainerColor = LatarBelakangUtama.copy(alpha = 0.5f),
                        unfocusedBorderColor = GarisHalus
                    )
                )
            }
        }

        // ── Konten Tab ────────────────────────────────────────────────────────
        Box(modifier = Modifier.fillMaxSize().weight(1f)) {
            if (keadaan.sedangMenarikMasterData) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                when (keadaan.tabMasterDataAktif) {
                    TabMasterData.RINGKASAN -> TabRingkasan(keadaan, onAksi)
                    TabMasterData.PART -> TabDaftarPart(keadaan, onAksi)
                    TabMasterData.JENIS_DEFECT -> TabDaftarJenisDefect(keadaan.daftarJenisDefectMaster)
                    TabMasterData.MATERIAL -> TabDaftarMaterial(keadaan.daftarMaterialMaster)
                    TabMasterData.SLOT_WAKTU -> TabDaftarSlotWaktu(keadaan.daftarSlotWaktuMaster)
                    TabMasterData.LINE_PRODUKSI -> TabDaftarLineProduksi(keadaan.daftarLineProduksiMaster)
                }
            }
        }
    }
}

@Composable
private fun BagianTabMasterData(tabAktif: TabMasterData, onAksi: (AksiAplikasi) -> Unit) {
    ScrollableTabRow(
        selectedTabIndex = TabMasterData.values().indexOf(tabAktif),
        edgePadding = 0.dp,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.primary,
        divider = {}
    ) {
        TabMasterData.values().forEach { tab ->
            Tab(
                selected = tab == tabAktif,
                onClick = { onAksi(AksiAplikasi.PilihTabMasterData(tab)) },
                text = {
                    Text(
                        text = tab.label,
                        fontWeight = if (tab == tabAktif) FontWeight.Black else FontWeight.Medium,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            )
        }
    }
}

@Composable
private fun TabRingkasan(keadaan: KeadaanAplikasi, onAksi: (AksiAplikasi) -> Unit) {
    val ringkasan = keadaan.ringkasanMasterData

    if (ringkasan == null) {
        StateKosongQControl(
            ikon = Icons.Default.FolderOpen,
            judul = "Master Data Belum Tersedia",
            pesan = "Tarik master data dari PGNServer agar QControl siap digunakan offline.",
            onAksi = { onAksi(AksiAplikasi.TarikMasterDataDariServer) },
            labelAksi = "Tarik Sekarang"
        )
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            KartuInfoPemeriksaan(modifier = Modifier.weight(1f), label = "Total Part", nilai = "${ringkasan.jumlahPart}", warna = SolarYellow)
            KartuInfoPemeriksaan(modifier = Modifier.weight(1f), label = "Jenis Defect", nilai = "${ringkasan.jumlahJenisDefect}", warna = GagalMerah)
            KartuInfoPemeriksaan(modifier = Modifier.weight(1f), label = "Material", nilai = "${ringkasan.jumlahMaterial}", warna = VibrantOrange)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            KartuInfoPemeriksaan(modifier = Modifier.weight(1f), label = "Slot Waktu", nilai = "${ringkasan.jumlahSlotWaktu}", warna = InfoBiru)
            KartuInfoPemeriksaan(modifier = Modifier.weight(1f), label = "Line Produksi", nilai = "${ringkasan.jumlahLineProduksi}", warna = BerhasilHijau)
            Spacer(modifier = Modifier.weight(1f))
        }

        PanelPremiumQControl(judul = "Informasi Sinkronisasi Lokal") {
            BarisMeta("Versi Skema", ringkasan.versiMasterData)
            PembatasHalusQControl(Modifier.padding(vertical = 8.dp))
            BarisMeta("Waktu Tarik", ringkasan.ditarikPada)
        }
    }
}

@Composable
private fun TabDaftarPart(keadaan: KeadaanAplikasi, onAksi: (AksiAplikasi) -> Unit) {
    val daftar = keadaan.daftarPartMaster
    if (daftar.isEmpty()) { 
        StateKosongQControl(ikon = Icons.Default.PrecisionManufacturing, judul = "Part Belum Tersedia", pesan = "Belum ada data part pada cache lokal.")
        return 
    }

    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        PanelPremiumQControl(modifier = Modifier.weight(1f), judul = "Daftar Part Produksi") {
            TabelMasterData(
                kolom = listOf("Kode" to 0.2f, "Nama Part" to 0.5f, "Line" to 0.3f),
                baris = daftar.map { listOf(it.kodeUnikPart, it.namaPart, it.namaLineDefault ?: "-") },
                itemTerpilihIndeks = if (keadaan.partMasterTerpilih != null) daftar.indexOf(keadaan.partMasterTerpilih) else -1,
                onKlikBaris = { idx -> onAksi(AksiAplikasi.PilihPartMaster(daftar[idx])) }
            )
        }

        if (keadaan.partMasterTerpilih != null) {
            PanelPremiumQControl(modifier = Modifier.width(400.dp), judul = "Detail Template Defect") {
                Text(text = keadaan.partMasterTerpilih.namaPart, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = SolarYellow)
                Text(text = keadaan.partMasterTerpilih.kodeUnikPart, style = MaterialTheme.typography.labelSmall, color = TeksKontrasRendah)
                Spacer(Modifier.height(16.dp))
                PembatasHalusQControl()
                Spacer(Modifier.height(16.dp))
                
                if (keadaan.daftarTemplateDefectPart.isEmpty()) {
                    Text("Belum ada template defect untuk part ini.", style = MaterialTheme.typography.bodySmall, color = TeksKontrasSedang)
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(keadaan.daftarTemplateDefectPart) { item ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(24.dp).background(VibrantOrange, MaterialTheme.shapes.extraSmall),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(item.kodeTampilanDefect ?: "-", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                }
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(item.namaDefect ?: "-", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                    Text(item.kodeDefect ?: "-", style = MaterialTheme.typography.labelSmall, color = TeksKontrasRendah)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TabDaftarJenisDefect(daftar: List<JenisDefect>) {
    PanelPremiumQControl(judul = "Daftar Jenis Kerusakan") {
        TabelMasterData(
            kolom = listOf("Kode" to 0.2f, "Nama Defect" to 0.5f, "Kategori" to 0.3f),
            baris = daftar.map { listOf(it.kodeDefect, it.namaDefect, it.namaKategori ?: "-") }
        )
    }
}

@Composable
private fun TabDaftarMaterial(daftar: List<Material>) {
    PanelPremiumQControl(judul = "Daftar Material Produksi") {
        TabelMasterData(
            kolom = listOf("Kode" to 0.3f, "Nama Material" to 0.7f),
            baris = daftar.map { listOf(it.kodeMaterial ?: "-", it.namaMaterial) }
        )
    }
}

@Composable
private fun TabDaftarSlotWaktu(daftar: List<SlotWaktu>) {
    PanelPremiumQControl(judul = "Konfigurasi Slot Waktu") {
        TabelMasterData(
            kolom = listOf("Label" to 0.4f, "Waktu" to 0.6f),
            baris = daftar.map { listOf(it.labelSlot, "${it.jamMulai} - ${it.jamSelesai}") }
        )
    }
}

@Composable
private fun TabDaftarLineProduksi(daftar: List<LineProduksi>) {
    PanelPremiumQControl(judul = "Daftar Lini Produksi") {
        TabelMasterData(
            kolom = listOf("Kode" to 0.3f, "Nama Line" to 0.7f),
            baris = daftar.map { listOf(it.kodeLine, it.namaLine) }
        )
    }
}

@Composable
private fun TabelMasterData(
    kolom: List<Pair<String, Float>>,
    baris: List<List<String>>,
    itemTerpilihIndeks: Int = -1,
    onKlikBaris: ((Int) -> Unit)? = null
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().background(LatarBelakangUtama.copy(alpha = 0.5f)).padding(12.dp)
        ) {
            kolom.forEach { (label, bobot) ->
                Text(
                    text = label.uppercase(),
                    modifier = Modifier.weight(bobot),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Black,
                    color = TeksKontrasRendah
                )
            }
        }
        
        LazyColumn {
            itemsIndexed(baris) { idx, item ->
                val isTerpilih = idx == itemTerpilihIndeks
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isTerpilih) SolarYellow.copy(alpha = 0.1f) else Color.Transparent)
                        .clickable(enabled = onKlikBaris != null) { onKlikBaris?.invoke(idx) }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item.forEachIndexed { i, text ->
                        Text(
                            text = text,
                            modifier = Modifier.weight(kolom[i].second),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isTerpilih) SolarYellow else TeksKontrasTinggi,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                PembatasHalusQControl()
            }
        }
    }
}

@Composable
private fun BarisMeta(kunci: String, nilai: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(kunci, style = MaterialTheme.typography.bodySmall, color = TeksKontrasRendah)
        Text(nilai, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = TeksKontrasTinggi)
    }
}
