package id.primaraya.qcontrol.data.lokal.pemetaan

import id.primaraya.qcontrol.ranah.model.ItemOutboxSinkronisasi
import id.primaraya.qcontrol.ranah.model.MetodeHttpSinkronisasi
import id.primaraya.qcontrol.ranah.model.StatusOutboxSinkronisasi
import java.sql.ResultSet

fun ResultSet.keItemOutboxSinkronisasi(): ItemOutboxSinkronisasi {
    return ItemOutboxSinkronisasi(
        id = getString("id"),
        jenisOperasi = getString("jenis_operasi"),
        endpointTujuan = getString("endpoint_tujuan"),
        metodeHttp = getString("metode_http").keMetodeHttpSinkronisasi(),
        payloadJson = getString("payload_json"),
        idempotencyKey = getString("idempotency_key"),
        hashPayload = getString("hash_payload"),
        status = getString("status").keStatusOutboxSinkronisasi(),
        jumlahPercobaan = getInt("jumlah_percobaan"),
        pesanGagalTerakhir = getString("pesan_gagal_terakhir"),
        dibuatPada = getString("dibuat_pada"),
        diperbaruiPada = getString("diperbarui_pada"),
        dikirimPada = getString("dikirim_pada")
    )
}

fun String?.keStatusOutboxSinkronisasi(): StatusOutboxSinkronisasi {
    return try {
        StatusOutboxSinkronisasi.valueOf(this ?: "GAGAL")
    } catch (e: Exception) {
        StatusOutboxSinkronisasi.GAGAL
    }
}

fun String?.keMetodeHttpSinkronisasi(): MetodeHttpSinkronisasi {
    return try {
        MetodeHttpSinkronisasi.valueOf(this ?: "POST")
    } catch (e: Exception) {
        MetodeHttpSinkronisasi.POST
    }
}
