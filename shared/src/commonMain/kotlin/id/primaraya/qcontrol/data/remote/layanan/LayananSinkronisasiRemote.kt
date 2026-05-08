package id.primaraya.qcontrol.data.remote.layanan

import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.inti.kesalahan.KesalahanAplikasi
import id.primaraya.qcontrol.data.remote.dto.AmplopResponApiDto
import id.primaraya.qcontrol.data.remote.dto.KesalahanApiDto
import id.primaraya.qcontrol.ranah.model.MetodeHttpSinkronisasi
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import java.io.IOException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

/**
 * Layanan untuk mengirimkan payload dari outbox lokal ke server pusat.
 */
class LayananSinkronisasiRemote(private val klien: HttpClient) {
    private val json = Json { ignoreUnknownKeys = true }
    
    suspend fun kirimPayload(
        endpoint: String,
        metode: MetodeHttpSinkronisasi,
        payloadJson: String,
        idempotencyKey: String,
        token: String? = null
    ): HasilOperasi<String> {
        return try {
            val respon = klien.request(endpoint) {
                method = when (metode) {
                    MetodeHttpSinkronisasi.GET -> HttpMethod.Get
                    MetodeHttpSinkronisasi.POST -> HttpMethod.Post
                    MetodeHttpSinkronisasi.PUT -> HttpMethod.Put
                    MetodeHttpSinkronisasi.PATCH -> HttpMethod.Patch
                    MetodeHttpSinkronisasi.DELETE -> HttpMethod.Delete
                }
                
                header("X-Idempotency-Key", idempotencyKey)
                header(HttpHeaders.Accept, ContentType.Application.Json.toString())
                if (token != null) {
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
                contentType(ContentType.Application.Json)
                
                if (metode != MetodeHttpSinkronisasi.GET) {
                    setBody(payloadJson)
                }
            }

            val isiRespon = respon.bodyAsText()
            val amplop = isiRespon.bacaAmplopAtauNull()
            val pesanRespon = amplop?.pesan?.takeIf { it.isNotBlank() }
            val kodeRespon = amplop?.kesalahan?.kode

            when (respon.status) {
                HttpStatusCode.OK, HttpStatusCode.Created, HttpStatusCode.Accepted, HttpStatusCode.NoContent -> {
                    HasilOperasi.Berhasil(isiRespon)
                }
                HttpStatusCode.NotFound -> {
                    HasilOperasi.Gagal(
                        KesalahanAplikasi.Server(
                            pesan = pesanRespon ?: "Endpoint sinkronisasi belum tersedia di server.",
                            kode = kodeRespon ?: "404"
                        )
                    )
                }
                HttpStatusCode.Conflict -> {
                    HasilOperasi.Gagal(
                        KesalahanAplikasi.Server(
                            pesan = pesanRespon ?: "Data yang sama sudah pernah dikirim dan memerlukan peninjauan HeadQC.",
                            kode = kodeRespon ?: "409"
                        )
                    )
                }
                HttpStatusCode.Unauthorized, HttpStatusCode.Forbidden -> {
                    HasilOperasi.Gagal(
                        KesalahanAplikasi.Server(
                            pesan = pesanRespon ?: "Sesi atau hak akses tidak valid untuk sinkronisasi ini.",
                            kode = kodeRespon ?: respon.status.value.toString()
                        )
                    )
                }
                else -> {
                    HasilOperasi.Gagal(
                        KesalahanAplikasi.Server(
                            pesan = pesanRespon ?: "Server mengalami kendala saat memproses data sinkronisasi.",
                            kode = kodeRespon ?: respon.status.value.toString()
                        )
                    )
                }
            }
        } catch (e: IOException) {
            HasilOperasi.Gagal(KesalahanAplikasi.KoneksiServer("Gagal terhubung ke server: ${e.message}"))
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.TidakDiketahui("Kesalahan sinkronisasi tidak terduga: ${e.message}"))
        }
    }

    private fun String.bacaAmplopAtauNull(): AmplopResponApiDto<JsonElement>? = try {
        json.decodeFromString<AmplopResponApiDto<JsonElement>>(this)
    } catch (_: Exception) {
        null
    }
}
