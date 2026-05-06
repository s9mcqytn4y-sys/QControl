package id.primaraya.qcontrol.tema

import androidx.compose.ui.graphics.Color

/**
 * Design Tokens - Warna Premium QControl
 * Tema: Premium Dark Manufacturing Control System
 */

// Brand Colors
val SolarYellow = Color(0xFFFFD700)
val VibrantOrange = Color(0xFFFF8C00)
val DeepAmber = Color(0xFFFF4500)

// Status Colors (Premium Palette)
val BerhasilHijau = Color(0xFF10B981) // Emerald 500
val GagalMerah = Color(0xFFF43F5E)    // Rose 500 (Lebih premium)
val PeringatanKuning = Color(0xFFFBBF24) // Amber 400
val InfoBiru = Color(0xFF60A5FA)      // Blue 400

// Background & Surface (Premium Dark Navy/Charcoal)
val LatarBelakangUtama = Color(0xFF0F172A) // Slate 900
val LatarBelakangSidebar = Color(0xFF1E293B) // Slate 800
val LatarBelakangPanel = Color(0xFF1E293B)  // Sama dengan sidebar
val LatarBelakangKartu = Color(0xFF334155).copy(alpha = 0.4f) // Slate 700 with transparency for glassmorphism
val GarisHalus = Color(0xFF334155) // Slate 700
val GarisSubtle = Color(0x1AFFFFFF) // White 10%

// Text Colors
val TeksKontrasTinggi = Color(0xFFF8FAFC) // Slate 50
val TeksKontrasSedang = Color(0xFFCBD5E1) // Slate 300
val TeksKontrasRendah = Color(0xFF94A3B8) // Slate 400
val TeksDisabled = Color(0xFF475569)     // Slate 600

// Shorthand untuk kompatibilitas lama bila diperlukan
val TeksTerang = TeksKontrasTinggi
val TeksGelap = LatarBelakangUtama
val TeksAbuAbu = TeksKontrasRendah
val LatarBelakangKonten = LatarBelakangUtama
val LatarBelakangGelap = LatarBelakangUtama
