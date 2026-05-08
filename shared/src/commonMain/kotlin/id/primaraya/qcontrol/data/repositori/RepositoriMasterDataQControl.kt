package id.primaraya.qcontrol.data.repositori

import id.primaraya.qcontrol.data.remote.layanan.LayananMasterDataRemote
import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.ranah.model.*

class RepositoriMasterDataQControl(
    private val layananRemote: LayananMasterDataRemote,
    private val repositoriLokal: RepositoriMasterDataLokal
) {
    suspend fun tarikDanSimpanMasterData(token: String): HasilOperasi<RingkasanMasterData> {
        return when (val hasilTarik = layananRemote.tarikMasterData(token)) {
            is HasilOperasi.Berhasil -> {
                when (val hasilSimpan = repositoriLokal.simpanMasterData(hasilTarik.data)) {
                    is HasilOperasi.Berhasil -> bacaRingkasanMasterDataLokal()
                    is HasilOperasi.Gagal -> hasilSimpan
                }
            }
            is HasilOperasi.Gagal -> hasilTarik
        }
    }

    suspend fun bacaRingkasanMasterDataLokal(): HasilOperasi<RingkasanMasterData> =
        repositoriLokal.bacaRingkasanMasterData()

    suspend fun bacaDaftarPart(kataKunci: String = "", lineIdAtauKode: String? = null): HasilOperasi<List<Part>> =
        repositoriLokal.bacaDaftarPart(kataKunci, lineIdAtauKode)

    suspend fun bacaDaftarJenisDefect(kataKunci: String = ""): HasilOperasi<List<JenisDefect>> =
        repositoriLokal.bacaDaftarJenisDefect(kataKunci)

    suspend fun bacaDaftarMaterial(kataKunci: String = ""): HasilOperasi<List<Material>> =
        repositoriLokal.bacaDaftarMaterial(kataKunci)

    suspend fun bacaDaftarSlotWaktu(): HasilOperasi<List<SlotWaktu>> =
        repositoriLokal.bacaDaftarSlotWaktu()

    suspend fun bacaDaftarLineProduksi(): HasilOperasi<List<LineProduksi>> =
        repositoriLokal.bacaDaftarLineProduksi()

    suspend fun bacaRelasiPartDefect(partId: String): HasilOperasi<List<RelasiPartDefect>> =
        repositoriLokal.bacaRelasiPartDefect(partId)

    suspend fun bacaTemplateDefectPart(partId: String): HasilOperasi<List<TemplateDefectPart>> =
        repositoriLokal.bacaTemplateDefectPart(partId)

    suspend fun bacaDiagnostikMasterData(): Map<String, Int> = repositoriLokal.bacaDiagnostikMasterData()
}
