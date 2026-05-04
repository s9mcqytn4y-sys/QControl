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
import io.ktor.utils.io.errors.*
import kotlinx.serialization.SerializationException

class LayananAutentikasiRemote(private val klien: HttpClient) {
    
    suspend fun login(username: String, password: String): HasilOperasi<Autentikasi> {
        return try {
            val respon: AmplopResponApiDto<ResponAutentikasiDto> = klien.post("/api/v1/login") {
                contentType(ContentType.Application.Json)
                setBody(PermintaanLoginDto(username, password))
            }.body()

            if (respon.berhasil && respon.data != null) {
                HasilOperasi.Berhasil(
                    Autentikasi(
                        token = respon.data.token,
                        namaPengguna = respon.data.profil.namaPengguna,
                        peran = respon.data.profil.peran
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
        } catch (e: IOException) {
            HasilOperasi.Gagal(KesalahanAplikasi.KoneksiServer("Gagal terhubung ke server: ${e.message}"))
        } catch (e: SerializationException) {
            HasilOperasi.Gagal(KesalahanAplikasi.ResponTidakValid("Format respon autentikasi tidak dikenali"))
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.TidakDiketahui("Kesalahan autentikasi tidak terduga: ${e.message}"))
        }
    }
}
