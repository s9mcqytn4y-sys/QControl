package id.primaraya.qcontrol.tampilan.kerangka

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.primaraya.qcontrol.tampilan.mvi.*
import id.primaraya.qcontrol.tampilan.navigasi.RuteAplikasi
import id.primaraya.qcontrol.tema.*

@Composable
fun SidebarAplikasi(
    shellState: ShellState,
    sessionState: SessionState,
    inputHarianState: InputHarianState,
    onShellIntent: (ShellIntent) -> Unit,
    onSessionIntent: (SessionIntent) -> Unit
) {
    Surface(
        modifier = Modifier.width(UkuranQControl.LebarSidebar).fillMaxHeight(),
        color = LatarBelakangSidebar,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(UkuranQControl.SpasiNormal)
        ) {
            // Logo & Header Sidebar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = UkuranQControl.SpasiSangatBesar)
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = MaterialTheme.shapes.small,
                    color = Color.White.copy(alpha = 0.05f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GarisSubtle)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(6.dp)) {
                        Image(
                            painter = painterResource("logo_qcontrol.png"),
                            contentDescription = "Logo QControl",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
                Spacer(Modifier.width(UkuranQControl.SpasiSedang))
                Column {
                    Text(
                        text = "QControl",
                        style = MaterialTheme.typography.titleLarge,
                        color = TeksKontrasTinggi,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "MANUFACTURING OS",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                }
            }

            // Menu Utama
            Text(
                text = "MENU UTAMA",
                style = MaterialTheme.typography.labelSmall,
                color = TeksKontrasRendah,
                modifier = Modifier.padding(start = 8.dp, bottom = 12.dp),
                fontWeight = FontWeight.Bold
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                RuteAplikasi.dapatkanDaftarRute().forEach { rute ->
                    ItemMenuSidebar(
                        rute = rute,
                        terpilih = shellState.ruteAktif == rute,
                        onClick = { onShellIntent(ShellIntent.PilihRute(rute)) }
                    )
                }
            }

            // Footer Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = UkuranQControl.SpasiNormal)
            ) {
                // User & Line Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    color = LatarBelakangUtama.copy(alpha = 0.4f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GarisSubtle)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.size(32.dp),
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    val inisial = (sessionState.sesiAktif?.namaPengguna ?: "H").take(1)
                                    Text(
                                        text = inisial,
                                        color = LatarBelakangUtama,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = sessionState.sesiAktif?.namaPengguna ?: "Head QC",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TeksKontrasTinggi,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "HeadQC",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TeksKontrasRendah
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Line Aktif:",
                                style = MaterialTheme.typography.labelSmall,
                                color = TeksKontrasRendah
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(6.dp).background(BerhasilHijau, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = inputHarianState.draft?.lineId ?: "-",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TeksKontrasTinggi,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Logout Action
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.small)
                        .clickable(onClick = { onSessionIntent(SessionIntent.Logout) })
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Keluar Sesi",
                        tint = GagalMerah,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "Keluar Sesi",
                        style = MaterialTheme.typography.bodySmall,
                        color = GagalMerah,
                        fontWeight = FontWeight.Bold
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
    val warnaLatar = if (terpilih) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent
    val warnaKonten = if (terpilih) MaterialTheme.colorScheme.primary else TeksKontrasSedang
    val borderModifier = if (terpilih) {
        Modifier.border(width = 1.dp, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), shape = MaterialTheme.shapes.small)
    } else Modifier

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(MaterialTheme.shapes.small)
            .background(warnaLatar)
            .then(borderModifier)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = rute.ikon,
            contentDescription = rute.labelMenu,
            tint = warnaKonten,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = rute.labelMenu,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (terpilih) FontWeight.ExtraBold else FontWeight.Medium,
            color = warnaKonten
        )
        if (terpilih) {
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier.size(4.dp).background(MaterialTheme.colorScheme.primary, CircleShape)
            )
        }
    }
}
