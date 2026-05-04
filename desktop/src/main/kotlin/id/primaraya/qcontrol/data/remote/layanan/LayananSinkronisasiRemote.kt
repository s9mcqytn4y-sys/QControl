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
                contentType(ContentType.Application.Json)
                
                if (metode != MetodeHttpSinkronisasi.GET) {
                    setBody(payloadJson)
                }
            }

            when {
                respon.status.isSuccess() -> {
                    HasilOperasi.Berhasil(respon.bodyAsText())
                }
                respon.status == HttpStatusCode.Conflict -> {
                    HasilOperasi.Gagal(
                        KesalahanAplikasi.Server(
                            pesan = "Terjadi konflik data (Idempotency Key mungkin sudah digunakan)",
                            kode = "409"
                        )
                    )
                }
                else -> {
                    HasilOperasi.Gagal(
                        KesalahanAplikasi.Server(
                            pesan = "Server mengembalikan status: ${respon.status.value} ${respon.status.description}",
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
