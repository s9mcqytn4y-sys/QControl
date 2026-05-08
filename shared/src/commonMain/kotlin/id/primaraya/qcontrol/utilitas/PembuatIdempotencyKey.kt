package id.primaraya.qcontrol.utilitas

import java.util.UUID

object PembuatIdempotencyKey {
    /**
     * Membuat idempotency key dengan format:
     * qcontrol:<jenis_operasi_normalized>:<endpoint_normalized>:<uuid>
     */
    fun buat(jenisOperasi: String, endpointTujuan: String): String {
        val operasiBersih = jenisOperasi.trim().lowercase().replace(" ", "-")
        val endpointBersih = endpointTujuan.trim().lowercase().replace("/", "-").replace(" ", "-")
        val uuid = UUID.randomUUID().toString()
        
        return "qcontrol:$operasiBersih:$endpointBersih:$uuid"
    }
}
