package id.primaraya.qcontrol.data.remote.layanan

import id.primaraya.qcontrol.data.remote.dto.AmplopResponApiDto
import id.primaraya.qcontrol.data.remote.dto.MasterDataQControlDto
import id.primaraya.qcontrol.data.remote.dto.MetadataMasterDataDto
import id.primaraya.qcontrol.data.remote.pemetaan.keDomain
import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.inti.kesalahan.KesalahanAplikasi
import id.primaraya.qcontrol.ranah.model.MasterDataQControl
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement

class LayananMasterDataRemote(
    private val httpClient: HttpClient
) {
    suspend fun tarikMasterData(token: String): HasilOperasi<MasterDataQControl> {
        if (token.isBlank()) {
            return HasilOperasi.Gagal(KesalahanAplikasi.Server("Sesi HeadQC tidak tersedia"))
        }

        return try {
            val response = httpClient.get("/api/v1/qcontrol/master-data") {
                header(HttpHeaders.Accept, "application/json")
                header(HttpHeaders.Authorization, "Bearer $token")
            }

            if (response.status == HttpStatusCode.Unauthorized || response.status == HttpStatusCode.Forbidden) {
                return HasilOperasi.Gagal(KesalahanAplikasi.Server("Sesi HeadQC tidak valid atau sudah berakhir"))
            }

            val body = response.body<AmplopResponApiDto<MasterDataQControlDto>>()
            
            if (!body.berhasil) {
                return HasilOperasi.Gagal(KesalahanAplikasi.Server(body.pesan ?: "Gagal menarik master data dari server"))
            }
            
            if (body.data != null && body.metadata != null) {
                val jsonParser = Json { ignoreUnknownKeys = true }
                val metadataObj = jsonParser.decodeFromJsonElement<MetadataMasterDataDto>(body.metadata)
                
                val masterData = body.data.keDomain(metadataObj.jumlahShiftOperasional)
                HasilOperasi.Berhasil(masterData)
            } else {
                HasilOperasi.Gagal(KesalahanAplikasi.ResponTidakValid("Format respon master data tidak sesuai"))
            }
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.KoneksiServer("Gagal terhubung ke server saat menarik master data"))
        }
    }
}
