package id.primaraya.qcontrol.data.remote.layanan

import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.inti.kesalahan.KesalahanAplikasi
import id.primaraya.qcontrol.ranah.model.MetodeHttpSinkronisasi
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.errors.*

/**
 * Layanan untuk mengirimkan payload dari outbox lokal ke server pusat.
 */
class LayananSinkronisasiRemote(private val klien: HttpClient) {
    
    suspend fun kirimPayload(
        endpoint: String,
        metode: MetodeHttpSinkronisasi,
        payloadJson: String,
        idempotencyKey: String
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
                contentType(ContentType.Application.Json)
                
                if (metode != MetodeHttpSinkronisasi.GET) {
                    setBody(payloadJson)
                }
            }

            when (respon.status) {
                HttpStatusCode.OK, HttpStatusCode.Created, HttpStatusCode.Accepted, HttpStatusCode.NoContent -> {
                    HasilOperasi.Berhasil(respon.bodyAsText())
                }
                HttpStatusCode.NotFound -> {
                    HasilOperasi.Gagal(
                        KesalahanAplikasi.Server(
                            pesan = "Endpoint sinkronisasi belum tersedia di server (404)",
                            kode = "404"
                        )
                    )
                }
                HttpStatusCode.Conflict -> {
                    HasilOperasi.Gagal(
                        KesalahanAplikasi.Server(
                            pesan = "Terjadi konflik data (Idempotency Key sudah pernah diproses)",
                            kode = "409"
                        )
                    )
                }
                HttpStatusCode.Unauthorized, HttpStatusCode.Forbidden -> {
                    HasilOperasi.Gagal(
                        KesalahanAplikasi.Server(
                            pesan = "Sesi atau hak akses tidak valid untuk sinkronisasi ini",
                            kode = respon.status.value.toString()
                        )
                    )
                }
                else -> {
                    HasilOperasi.Gagal(
                        KesalahanAplikasi.Server(
                            pesan = "Server mengalami kendala (Status: ${respon.status.value})",
                            kode = respon.status.value.toString()
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
}
