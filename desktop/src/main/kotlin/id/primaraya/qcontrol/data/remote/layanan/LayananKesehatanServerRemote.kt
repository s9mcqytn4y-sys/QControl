package id.primaraya.qcontrol.data.remote.layanan

import id.primaraya.qcontrol.data.remote.dto.AmplopResponApiDto
import id.primaraya.qcontrol.data.remote.dto.StatusKesehatanServerDto
import id.primaraya.qcontrol.data.remote.pemetaan.keDomain
import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.inti.kesalahan.KesalahanAplikasi
import id.primaraya.qcontrol.ranah.model.StatusKesehatanServer
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import java.io.IOException
import kotlinx.serialization.SerializationException

class LayananKesehatanServerRemote(private val klien: HttpClient) {
    suspend fun periksaKesehatanServer(): HasilOperasi<StatusKesehatanServer> {
        return try {
            val respon: AmplopResponApiDto<StatusKesehatanServerDto> = klien.get("/api/v1/kesehatan").body()
            
            if (respon.berhasil && respon.data != null) {
                HasilOperasi.Berhasil(respon.data.keDomain())
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
            HasilOperasi.Gagal(KesalahanAplikasi.ResponTidakValid("Format respon server tidak dikenali"))
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.TidakDiketahui("Kesalahan tidak terduga: ${e.message}"))
        }
    }
}
