package id.primaraya.qcontrol.tampilan.halaman

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import id.primaraya.qcontrol.tampilan.komponen.ChipStatusQControl
import id.primaraya.qcontrol.tampilan.komponen.FieldInputQControl
import id.primaraya.qcontrol.tampilan.komponen.PanelPremiumQControl
import id.primaraya.qcontrol.tampilan.komponen.TombolUtamaQControl
import id.primaraya.qcontrol.tampilan.mvi.SessionIntent
import id.primaraya.qcontrol.tampilan.mvi.SessionState
import id.primaraya.qcontrol.tampilan.state.StatusKoneksiServer
import id.primaraya.qcontrol.tema.BerhasilHijau
import id.primaraya.qcontrol.tema.GagalMerah
import id.primaraya.qcontrol.tema.GarisSubtle
import id.primaraya.qcontrol.tema.LatarBelakangSidebar
import id.primaraya.qcontrol.tema.LatarBelakangUtama
import id.primaraya.qcontrol.tema.PeringatanKuning
import id.primaraya.qcontrol.tema.TeksKontrasRendah
import id.primaraya.qcontrol.tema.TeksKontrasTinggi
import id.primaraya.qcontrol.tema.UkuranQControl

@Composable
fun HalamanLogin(
    state: SessionState,
    onIntent: (SessionIntent) -> Unit,
    statusKoneksi: StatusKoneksiServer,
    pesanKoneksi: String
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val emailValid = remember(email) { email.contains("@") && email.contains(".") }
    val passwordValid = remember(password) { password.length >= 8 }
    val formValid = emailValid && passwordValid && !state.sedangLogin

    fun laksanakanLogin() {
        if (formValid) {
            onIntent(SessionIntent.Login(email, password))
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
        Box(
            modifier = Modifier
                .size(600.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFFFFD75F).copy(alpha = 0.05f), Color.Transparent)
                    ),
                    RoundedCornerShape(999.dp)
                )
                .align(Alignment.TopStart)
                .offset(x = (-200).dp, y = (-200).dp)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(420.dp)
                .padding(UkuranQControl.SpasiNormal)
        ) {
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
                text = "Sistem manajemen kualitas produksi",
                style = MaterialTheme.typography.bodyMedium,
                color = TeksKontrasRendah
            )

            Spacer(modifier = Modifier.height(UkuranQControl.SpasiSangatBesar))

            PanelPremiumQControl(modifier = Modifier.fillMaxWidth()) {
                FieldInputQControl(
                    value = email,
                    onValueChange = { email = it.trim() },
                    label = "Email HeadQC",
                    placeholder = "nama@perusahaan.com",
                    leadingIcon = Icons.Default.Email,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    enabled = !state.sedangLogin,
                    isError = email.isNotEmpty() && !emailValid,
                    errorMessage = if (email.isNotEmpty() && !emailValid) "Format email tidak valid" else null
                )

                Spacer(modifier = Modifier.height(UkuranQControl.SpasiNormal))

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
                    keyboardActions = KeyboardActions(onDone = { laksanakanLogin() }),
                    enabled = !state.sedangLogin,
                    isError = password.isNotEmpty() && !passwordValid,
                    errorMessage = if (password.isNotEmpty() && !passwordValid) "Minimal 8 karakter" else null
                )

                Spacer(modifier = Modifier.height(UkuranQControl.SpasiBesar))

                TombolUtamaQControl(
                    text = "Masuk sebagai HeadQC",
                    onClick = { laksanakanLogin() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = formValid,
                    sedangMemuat = state.sedangLogin
                )
            }

            Spacer(modifier = Modifier.height(UkuranQControl.SpasiBesar))

            val (warnaServer, labelServer) = when (statusKoneksi) {
                StatusKoneksiServer.Tersambung -> BerhasilHijau to pesanKoneksi
                StatusKoneksiServer.Terputus -> GagalMerah to pesanKoneksi
                StatusKoneksiServer.Memeriksa -> PeringatanKuning to "Memeriksa koneksi..."
                StatusKoneksiServer.TidakDiperiksa -> TeksKontrasRendah to "Mode offline"
            }
            ChipStatusQControl(label = labelServer, warna = warnaServer)

            Spacer(modifier = Modifier.height(UkuranQControl.SpasiSangatBesar))

            Text(
                text = "QControl Desktop",
                style = MaterialTheme.typography.labelMedium,
                color = TeksKontrasRendah
            )
            Text(
                text = "PT Primaraya Lestari Makmur • 2026",
                style = MaterialTheme.typography.labelSmall,
                color = TeksKontrasRendah.copy(alpha = 0.5f)
            )
        }
    }
}
