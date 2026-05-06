package id.primaraya.qcontrol.tampilan.halaman

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import id.primaraya.qcontrol.tampilan.komponen.*
import id.primaraya.qcontrol.tampilan.state.AksiAplikasi
import id.primaraya.qcontrol.tampilan.state.KeadaanAplikasi
import id.primaraya.qcontrol.tampilan.state.StatusKoneksiServer
import id.primaraya.qcontrol.tema.*

@Composable
fun HalamanLogin(
    keadaan: KeadaanAplikasi,
    onAksi: (AksiAplikasi) -> Unit
) {
    var email by remember { mutableStateOf("headqc@pgn.local") }
    var password by remember { mutableStateOf("HeadQC@12345") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Client-side validation state
    val emailValid = remember(email) { email.contains("@") && email.contains(".") }
    val passwordValid = remember(password) { password.length >= 8 }
    val formValid = emailValid && passwordValid && !keadaan.sedangLogin

    fun laksanakanLogin() {
        if (formValid) {
            onAksi(AksiAplikasi.Login(email, password))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        LatarBelakangUtama,
                        LatarBelakangSidebar
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Decorative background elements (subtle gradients)
        Box(
            modifier = Modifier
                .size(600.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(SolarYellow.copy(alpha = 0.05f), Color.Transparent)
                    )
                )
                .align(Alignment.TopStart)
                .offset(x = (-200).dp, y = (-200).dp)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(420.dp).padding(UkuranQControl.SpasiNormal)
        ) {
            // Logo Utama
            Surface(
                modifier = Modifier.size(120.dp),
                shape = MaterialTheme.shapes.extraLarge,
                color = Color.White.copy(alpha = 0.05f),
                border = androidx.compose.foundation.BorderStroke(1.dp, GarisSubtle)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(24.dp)) {
                    Image(
                        painter = painterResource("logo_qcontrol.png"),
                        contentDescription = "Logo QControl",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(UkuranQControl.SpasiBesar))

            Text(
                text = "Akses HeadQC",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = TeksKontrasTinggi
            )
            Text(
                text = "Sistem Manajemen Kualitas Produksi",
                style = MaterialTheme.typography.bodyMedium,
                color = TeksKontrasRendah
            )

            Spacer(modifier = Modifier.height(UkuranQControl.SpasiSangatBesar))

            PanelPremiumQControl(modifier = Modifier.fillMaxWidth()) {
                // Email Field
                FieldInputQControl(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email HeadQC",
                    placeholder = "headqc@pgn.local",
                    leadingIcon = Icons.Default.Email,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    enabled = !keadaan.sedangLogin,
                    isError = email.isNotEmpty() && !emailValid,
                    errorMessage = if (email.isNotEmpty() && !emailValid) "Format email tidak valid" else null
                )

                Spacer(modifier = Modifier.height(UkuranQControl.SpasiNormal))

                // Password Field
                FieldInputQControl(
                    value = password,
                    onValueChange = { password = it },
                    label = "Kata Sandi",
                    placeholder = "Minimal 8 karakter",
                    leadingIcon = Icons.Default.Lock,
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = TeksKontrasRendah
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { laksanakanLogin() }
                    ),
                    enabled = !keadaan.sedangLogin,
                    isError = password.isNotEmpty() && !passwordValid,
                    errorMessage = if (password.isNotEmpty() && !passwordValid) "Minimal 8 karakter" else null
                )

                Spacer(modifier = Modifier.height(UkuranQControl.SpasiBesar))

                TombolUtamaQControl(
                    text = "Masuk sebagai HeadQC",
                    onClick = { laksanakanLogin() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = formValid,
                    sedangMemuat = keadaan.sedangLogin
                )
            }

            Spacer(modifier = Modifier.height(UkuranQControl.SpasiBesar))

            // Status Server & Sesi Mini
            val (warnaServer, labelServer) = when (keadaan.statusKoneksi) {
                StatusKoneksiServer.Tersambung -> BerhasilHijau to "PGNServer Tersambung"
                StatusKoneksiServer.Terputus -> GagalMerah to "PGNServer Terputus"
                StatusKoneksiServer.Memeriksa -> PeringatanKuning to "Memeriksa Koneksi..."
                StatusKoneksiServer.TidakDiperiksa -> TeksKontrasRendah to "Status Offline"
            }
            ChipStatusQControl(label = labelServer, warna = warnaServer)

            Spacer(modifier = Modifier.height(UkuranQControl.SpasiSangatBesar))

            Text(
                text = "QControl Desktop • QA/QC System",
                style = MaterialTheme.typography.labelMedium,
                color = TeksKontrasRendah
            )
            Text(
                text = "© 2026 PT Primaraya Lestari Makmur",
                style = MaterialTheme.typography.labelSmall,
                color = TeksKontrasRendah.copy(alpha = 0.5f)
            )
        }
    }
}
