package id.primaraya.qcontrol.data.repositori

import id.primaraya.qcontrol.database.QControlDatabase
import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.inti.kesalahan.KesalahanAplikasi
import id.primaraya.qcontrol.konfigurasi.KonfigurasiSinkronisasi
import id.primaraya.qcontrol.ranah.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

class RepositoriOutboxSinkronisasi(
    private val database: QControlDatabase
) {
    private val queries = database.qControlQueries

    suspend fun tambah(item: ItemOutboxSinkronisasi): HasilOperasi<Unit> = withContext(Dispatchers.IO) {
        try {
            queries.simpanOutbox(
                id = item.id,
                jenis_operasi = item.jenisOperasi,
                endpoint_tujuan = item.endpointTujuan,
                metode_http = item.metodeHttp.name,
                payload_json = item.payloadJson,
                idempotency_key = item.idempotencyKey,
                hash_payload = item.hashPayload,
                status = item.status.name,
                jumlah_percobaan = item.jumlahPercobaan,
                maks_percobaan = item.maksPercobaan,
                next_retry_at = item.nextRetryAt,
                last_http_status = item.lastHttpStatus,
                last_error_code = item.lastErrorCode,
                pesan_gagal_terakhir = item.pesanGagalTerakhir,
                dibuat_pada = item.dibuatPada,
                diperbarui_pada = item.diperbaruiPada,
                dikirim_pada = item.dikirimPada
            )
            HasilOperasi.Berhasil(Unit)
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal menambah item outbox: ${e.message}"))
        }
    }

    suspend fun bacaMenunggu(batas: Int = 50): HasilOperasi<List<ItemOutboxSinkronisasi>> = withContext(Dispatchers.IO) {
        try {
            val result = queries.dapatkanDaftarOutboxMenunggu(Instant.now().toString())
                .executeAsList()
                .take(batas)
            HasilOperasi.Berhasil(result.map {
                ItemOutboxSinkronisasi(
                    id = it.id,
                    jenisOperasi = it.jenis_operasi,
                    endpointTujuan = it.endpoint_tujuan,
                    metodeHttp = MetodeHttpSinkronisasi.valueOf(it.metode_http),
                    payloadJson = it.payload_json,
                    idempotencyKey = it.idempotency_key,
                    hashPayload = it.hash_payload,
                    status = StatusOutboxSinkronisasi.valueOf(it.status),
                    jumlahPercobaan = it.jumlah_percobaan.toInt(),
                    maksPercobaan = it.maks_percobaan.toInt(),
                    nextRetryAt = it.next_retry_at,
                    lastHttpStatus = it.last_http_status?.toInt(),
                    lastErrorCode = it.last_error_code,
                    pesanGagalTerakhir = it.pesan_gagal_terakhir,
                    dibuatPada = it.dibuat_pada,
                    diperbaruiPada = it.diperbarui_pada,
                    dikirimPada = it.dikirim_pada
                )
            })
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal membaca outbox menunggu: ${e.message}"))
        }
    }

    suspend fun bacaRingkasan(): HasilOperasi<RingkasanOutboxSinkronisasi> = withContext(Dispatchers.IO) {
        try {
            val stats = queries.hitungRingkasanOutbox().executeAsList()
            val map = stats.associate { it.status to it.jumlah.toInt() }
            
            val ringkasan = RingkasanOutboxSinkronisasi(
                jumlahMenunggu = map["MENUNGGU"] ?: 0,
                jumlahSedangDikirim = map["SEDANG_DIKIRIM"] ?: 0,
                jumlahBerhasil = map["BERHASIL"] ?: 0,
                jumlahGagal = map["GAGAL_SEMENTARA"] ?: 0,
                jumlahKonflik = map["KONFLIK"] ?: 0,
                jumlahTotal = stats.sumOf { it.jumlah }.toInt()
            )
            HasilOperasi.Berhasil(ringkasan)
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal membaca ringkasan outbox: ${e.message}"))
        }
    }

    suspend fun updateStatus(
        id: String,
        status: StatusOutboxSinkronisasi,
        jumlahPercobaan: Int? = null,
        nextRetryAt: String? = null,
        lastHttpStatus: Int? = null,
        lastErrorCode: String? = null,
        pesanGagal: String? = null,
        dikirimPada: String? = null,
        kosongkanMetadataGagal: Boolean = false
    ): HasilOperasi<Unit> = withContext(Dispatchers.IO) {
        try {
            val waktuSekarang = Instant.now().toString()
            val existing = queries.dapatkanOutboxById(id).executeAsOne()
            
            queries.updateStatusOutbox(
                status = status.name,
                jumlah_percobaan = jumlahPercobaan ?: existing.jumlah_percobaan,
                next_retry_at = if (kosongkanMetadataGagal) null else nextRetryAt ?: existing.next_retry_at,
                last_http_status = if (kosongkanMetadataGagal) null else lastHttpStatus ?: existing.last_http_status,
                last_error_code = if (kosongkanMetadataGagal) null else lastErrorCode ?: existing.last_error_code,
                pesan_gagal_terakhir = if (kosongkanMetadataGagal) null else pesanGagal ?: existing.pesan_gagal_terakhir,
                diperbarui_pada = waktuSekarang,
                dikirim_pada = dikirimPada,
                id = id
            )
            HasilOperasi.Berhasil(Unit)
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal update status outbox: ${e.message}"))
        }
    }

    suspend fun tandaiSedangDikirim(id: String): HasilOperasi<Unit> = updateStatus(
        id = id,
        status = StatusOutboxSinkronisasi.SEDANG_DIKIRIM,
        nextRetryAt = null,
        kosongkanMetadataGagal = true
    )

    suspend fun tandaiBerhasil(id: String): HasilOperasi<Unit> = updateStatus(
        id = id,
        status = StatusOutboxSinkronisasi.BERHASIL,
        dikirimPada = Instant.now().toString(),
        nextRetryAt = null,
        kosongkanMetadataGagal = true
    )

    suspend fun tandaiKonflik(
        id: String,
        pesan: String?,
        lastHttpStatus: Int? = 409,
        lastErrorCode: String? = "409"
    ): HasilOperasi<Unit> = updateStatus(
        id = id,
        status = StatusOutboxSinkronisasi.KONFLIK,
        pesanGagal = pesan,
        nextRetryAt = null,
        lastHttpStatus = lastHttpStatus,
        lastErrorCode = lastErrorCode
    )

    suspend fun tandaiGagal(
        id: String,
        pesan: String?,
        lastHttpStatus: Int? = null,
        lastErrorCode: String? = null
    ): HasilOperasi<Unit> = withContext(Dispatchers.IO) {
        try {
            val existing = queries.dapatkanOutboxById(id).executeAsOne()
            val jumlahPercobaan = existing.jumlah_percobaan.toInt() + 1
            val maksPercobaan = existing.maks_percobaan.toInt()
            
            val jedaMenit = when (jumlahPercobaan) {
                1 -> 1L
                2 -> 5L
                3 -> 15L
                4 -> 30L
                else -> 60L
            }
            val nextRetryAt = if (jumlahPercobaan >= maksPercobaan) {
                null
            } else {
                Instant.now().plusSeconds(jedaMenit * 60).toString()
            }
            val pesanFinal = if (nextRetryAt == null) {
                buildString {
                    append(pesan ?: "Batas kirim otomatis tercapai.")
                    append(" Pengiriman otomatis dihentikan dan menunggu tindakan HeadQC.")
                }
            } else {
                pesan
            }

            updateStatus(
                id = id,
                status = StatusOutboxSinkronisasi.GAGAL_SEMENTARA,
                jumlahPercobaan = jumlahPercobaan,
                nextRetryAt = nextRetryAt,
                lastHttpStatus = lastHttpStatus,
                lastErrorCode = lastErrorCode,
                pesanGagal = pesanFinal
            )
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal update status gagal: ${e.message}"))
        }
    }

    suspend fun resetSedangDikirim(): HasilOperasi<Unit> = withContext(Dispatchers.IO) {
        try {
            queries.resetOutboxSedangDikirim()
            HasilOperasi.Berhasil(Unit)
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal reset outbox: ${e.message}"))
        }
    }

    suspend fun bacaBerhasilTerakhir(): HasilOperasi<ItemOutboxSinkronisasi?> = withContext(Dispatchers.IO) {
        try {
            val it = queries.dapatkanOutboxBerhasilTerakhir().executeAsOneOrNull()
            if (it != null) {
                HasilOperasi.Berhasil(
                    ItemOutboxSinkronisasi(
                        id = it.id,
                        jenisOperasi = it.jenis_operasi,
                        endpointTujuan = it.endpoint_tujuan,
                        metodeHttp = MetodeHttpSinkronisasi.valueOf(it.metode_http),
                        payloadJson = it.payload_json,
                        idempotencyKey = it.idempotency_key,
                        hashPayload = it.hash_payload,
                        status = StatusOutboxSinkronisasi.valueOf(it.status),
                        jumlahPercobaan = it.jumlah_percobaan.toInt(),
                        maksPercobaan = it.maks_percobaan.toInt(),
                        nextRetryAt = it.next_retry_at,
                        lastHttpStatus = it.last_http_status?.toInt(),
                        lastErrorCode = it.last_error_code,
                        pesanGagalTerakhir = it.pesan_gagal_terakhir,
                        dibuatPada = it.dibuat_pada,
                        diperbaruiPada = it.diperbarui_pada,
                        dikirimPada = it.dikirim_pada
                    )
                )
            } else {
                HasilOperasi.Berhasil(null)
            }
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal baca outbox terakhir: ${e.message}"))
        }
    }
}
