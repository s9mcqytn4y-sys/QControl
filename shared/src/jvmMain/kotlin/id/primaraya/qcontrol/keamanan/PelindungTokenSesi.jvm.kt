package id.primaraya.qcontrol.keamanan

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

actual object PelindungTokenSesi {
    private const val ALGORITMA = "AES/GCM/NoPadding"
    private const val PANJANG_IV = 12
    private const val PANJANG_TAG = 128

    actual fun lindungi(tokenAsli: String): String = try {
        val iv = ByteArray(PANJANG_IV).also { SecureRandom().nextBytes(it) }
        val cipher = Cipher.getInstance(ALGORITMA)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey(), GCMParameterSpec(PANJANG_TAG, iv))
        val terenkripsi = cipher.doFinal(tokenAsli.toByteArray(StandardCharsets.UTF_8))
        Base64.getEncoder().encodeToString(iv + terenkripsi)
    } catch (_: Exception) {
        Base64.getEncoder().encodeToString(tokenAsli.toByteArray(StandardCharsets.UTF_8))
    }

    actual fun buka(tokenTerlindungi: String): String {
        return try {
            val gabungan = Base64.getDecoder().decode(tokenTerlindungi)
            if (gabungan.size <= PANJANG_IV) {
                String(gabungan, StandardCharsets.UTF_8)
            } else {
                val iv = gabungan.copyOfRange(0, PANJANG_IV)
                val payload = gabungan.copyOfRange(PANJANG_IV, gabungan.size)
                val cipher = Cipher.getInstance(ALGORITMA)
                cipher.init(Cipher.DECRYPT_MODE, secretKey(), GCMParameterSpec(PANJANG_TAG, iv))
                String(cipher.doFinal(payload), StandardCharsets.UTF_8)
            }
        } catch (_: Exception) {
            try {
                String(Base64.getDecoder().decode(tokenTerlindungi), StandardCharsets.UTF_8)
            } catch (_: Exception) {
                tokenTerlindungi
            }
        }
    }

    private fun secretKey(): SecretKeySpec {
        val fingerprint = listOf(
            System.getProperty("user.name").orEmpty(),
            System.getProperty("user.home").orEmpty(),
            System.getProperty("os.name").orEmpty()
        ).joinToString("|")
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(fingerprint.toByteArray(StandardCharsets.UTF_8))
        return SecretKeySpec(digest.copyOf(16), "AES")
    }
}
