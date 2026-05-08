package id.primaraya.qcontrol.data.remote.layanan

import id.primaraya.qcontrol.data.remote.dto.AmplopResponApiDto
import id.primaraya.qcontrol.data.remote.dto.PermintaanLoginDto
import id.primaraya.qcontrol.data.remote.dto.ResponAutentikasiDto
import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.inti.kesalahan.KesalahanAplikasi
import id.primaraya.qcontrol.ranah.model.Autentikasi
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.io.IOException
import kotlinx.serialization.SerializationException

import id.primaraya.qcontrol.data.remote.dto.ProfilPenggunaDto

import kotlinx.serialization.json.JsonElement

class LayananAutentikasiRemote(private val klien: HttpClient) {
    
    suspend fun masukSesi(email: String, kataSandi: String): HasilOperasi<Autentikasi> {
        return try {
            val responHttp = klien.post("/api/v1/login") {
                contentType(ContentType.Application.Json)
                setBody(PermintaanLoginDto(email, kataSandi))
                header(HttpHeaders.Accept, "application/json")
            }

            // Tangani HTTP 401 Unauthorized secara eksplisit jika perlu
            if (responHttp.status == HttpStatusCode.Unauthorized) {
                return HasilOperasi.Gagal(
                    KesalahanAplikasi.Server(
                        pesan = "Email atau password tidak sesuai",
                        kode = "AUTENTIKASI_GAGAL"
                    )
                )
            }

            val respon: AmplopResponApiDto<ResponAutentikasiDto> = responHttp.body()

            if (respon.berhasil && respon.data != null) {
                HasilOperasi.Berhasil(
                    Autentikasi(
                        token = respon.data.token,
                        namaPengguna = respon.data.profil.namaPengguna,
                        peran = respon.data.profil.peran,
                        email = respon.data.profil.email
                    )
                )
            } else {
                // Mapping kode AUTENTIKASI_GAGAL dari PGNServer
                val pesanError = if (respon.kesalahan?.kode == "AUTENTIKASI_GAGAL" || respon.kesalahan?.kode == "UNAUTHORIZED") {
                    "Email atau password tidak sesuai"
                } else {
                    respon.pesan
                }
                
                HasilOperasi.Gagal(
                    KesalahanAplikasi.Server(
                        pesan = pesanError,
                        kode = respon.kesalahan?.kode
                    )
                )
            }
        } catch (e: IOException) {
            HasilOperasi.Gagal(KesalahanAplikasi.KoneksiServer("Gagal terhubung ke server. Pastikan PGNServer aktif."))
        } catch (e: SerializationException) {
            HasilOperasi.Gagal(KesalahanAplikasi.ResponTidakValid("Format respon autentikasi tidak dikenali"))
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.TidakDiketahui("Kesalahan autentikasi tidak terduga: ${e.message}"))
        }
    }

    suspend fun ambilProfilSaya(token: String): HasilOperasi<Autentikasi> {
        return try {
            val respon: AmplopResponApiDto<ProfilPenggunaDto> = klien.get("/api/v1/profil-saya") {
                header(HttpHeaders.Authorization, "Bearer $token")
                header(HttpHeaders.Accept, "application/json")
            }.body()

            if (respon.berhasil && respon.data != null) {
                HasilOperasi.Berhasil(
                    Autentikasi(
                        token = token,
                        namaPengguna = respon.data.namaPengguna,
                        peran = respon.data.peran,
                        email = respon.data.email
                    )
                )
            } else {
                HasilOperasi.Gagal(
                    KesalahanAplikasi.Server(
                        pesan = respon.pesan,
                        kode = respon.kesalahan?.kode
                    )
                )
            }
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.Server("Gagal memuat profil: ${e.message}"))
        }
    }

    suspend fun keluarSesi(token: String): HasilOperasi<Unit> {
        return try {
            // Gunakan JsonElement? karena logout PGNServer mengembalikan data: null
            val respon: AmplopResponApiDto<JsonElement?> = klien.post("/api/v1/logout") {
                header(HttpHeaders.Authorization, "Bearer $token")
                header(HttpHeaders.Accept, "application/json")
            }.body()

            if (respon.berhasil) {
                HasilOperasi.Berhasil(Unit)
            } else {
                HasilOperasi.Gagal(KesalahanAplikasi.Server(respon.pesan))
            }
        } catch (e: Exception) {
            // Logout remote gagal (misal token kadaluarsa), local logout tetap sukses
            HasilOperasi.Berhasil(Unit)
        }
    }
}
