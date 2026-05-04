package id.primaraya.qcontrol.tampilan.kerangka

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import id.primaraya.qcontrol.konfigurasi.KonfigurasiAplikasi
import id.primaraya.qcontrol.tampilan.navigasi.RuteAplikasi
import id.primaraya.qcontrol.tampilan.state.KeadaanAplikasi
import id.primaraya.qcontrol.tampilan.state.StatusKoneksiServer
import id.primaraya.qcontrol.tema.UkuranQControl

@Composable
fun SidebarAplikasi(
    keadaan: KeadaanAplikasi,
    onPilihRute: (RuteAplikasi) -> Unit,
    onLogout: () -> Unit
) {
    NavigationRail(
        modifier = Modifier.width(UkuranQControl.LebarSidebar),
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(UkuranQControl.SpasiNormal)
        ) {
            // Logo & Nama Aplikasi
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = UkuranQControl.SpasiSangatBesar)
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "Q",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(Modifier.width(UkuranQControl.SpasiSedang))
                Column {
                    Text(
                        text = KonfigurasiAplikasi.NAMA_APLIKASI,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "QUALITY ASSURANCE DEPT.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }

            // Menu Items
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(UkuranQControl.SpasiKecil)
            ) {
                keadaan.daftarRute.forEach { rute ->
                    ItemMenuSidebar(
                        rute = rute,
                        terpilih = keadaan.ruteAktif == rute,
                        onClick = { onPilihRute(rute) }
                    )
                }
            }

            // Info Pengguna & Line
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = UkuranQControl.SpasiNormal)
            ) {
                // Line Aktif
                Text(
                    text = "Line Aktif",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = UkuranQControl.SpasiKecil)
                ) {
                    Icon(
                        imageVector = Icons.Default.Circle,
                        contentDescription = null,
                        modifier = Modifier.size(10.dp),
                        tint = if (keadaan.statusKoneksi == StatusKoneksiServer.Tersambung) Color.Green else Color.Red
                    )
                    Spacer(Modifier.width(UkuranQControl.SpasiSedang))
                    Text(
                        text = keadaan.lineAktif,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(UkuranQControl.SpasiBesar))

                // Profil Singkat
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(UkuranQControl.RadiusBesar))
                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
                        .padding(UkuranQControl.SpasiSedang),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(32.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            val inisial = (keadaan.sesiAktif?.namaPengguna ?: keadaan.namaPengguna).take(1)
                            Text(
                                text = inisial,
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    Spacer(Modifier.width(UkuranQControl.SpasiSedang))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = keadaan.sesiAktif?.namaPengguna ?: keadaan.namaPengguna,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = keadaan.sesiAktif?.peran ?: keadaan.peranPengguna,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }

                Spacer(Modifier.height(UkuranQControl.SpasiSedang))

                // Tombol Logout
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(UkuranQControl.RadiusBesar))
                        .clickable(onClick = onLogout)
                        .padding(UkuranQControl.SpasiSedang),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Keluar Sesi",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(UkuranQControl.SpasiSedang))
                    Text(
                        text = "Keluar Sesi",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun ItemMenuSidebar(
    rute: RuteAplikasi,
    terpilih: Boolean,
    onClick: () -> Unit
) {
    val warnaLatar = if (terpilih) MaterialTheme.colorScheme.primary else Color.Transparent
    val warnaKonten = if (terpilih) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(UkuranQControl.RadiusNormal))
            .background(warnaLatar)
            .clickable(onClick = onClick)
            .padding(horizontal = UkuranQControl.SpasiNormal),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = rute.ikon,
            contentDescription = rute.labelMenu,
            tint = warnaKonten,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(UkuranQControl.SpasiNormal))
        Text(
            text = rute.labelMenu,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (terpilih) FontWeight.Bold else FontWeight.Normal,
            color = warnaKonten
        )
    }
}
