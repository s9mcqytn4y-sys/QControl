package id.primaraya.qcontrol.utilitas

import java.time.OffsetDateTime

object PembuatWaktuSekarang {
    /**
     * Membuat waktu sekarang dalam format ISO Offset (misal: 2026-05-04T20:23:31+07:00)
     */
    fun buatIsoOffsetSekarang(): String {
        return OffsetDateTime.now().toString()
    }
}
