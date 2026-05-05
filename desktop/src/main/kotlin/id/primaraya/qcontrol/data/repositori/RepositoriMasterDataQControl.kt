package id.primaraya.qcontrol.data.repositori

import id.primaraya.qcontrol.data.lokal.repositori.RepositoriMasterDataLokal
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

    fun bacaRingkasanMasterDataLokal(): HasilOperasi<RingkasanMasterData> =
        repositoriLokal.bacaRingkasanMasterData()

    fun bacaDaftarPart(kataKunci: String = ""): HasilOperasi<List<Part>> =
        repositoriLokal.bacaDaftarPart(kataKunci)

    fun bacaDaftarJenisDefect(kataKunci: String = ""): HasilOperasi<List<JenisDefect>> =
        repositoriLokal.bacaDaftarJenisDefect(kataKunci)

    fun bacaDaftarMaterial(kataKunci: String = ""): HasilOperasi<List<Material>> =
        repositoriLokal.bacaDaftarMaterial(kataKunci)

    fun bacaDaftarSlotWaktu(): HasilOperasi<List<SlotWaktu>> =
        repositoriLokal.bacaDaftarSlotWaktu()

    fun bacaDaftarLineProduksi(): HasilOperasi<List<LineProduksi>> =
        repositoriLokal.bacaDaftarLineProduksi()

    fun bacaRelasiPartDefect(partId: String): HasilOperasi<List<RelasiPartDefect>> =
        repositoriLokal.bacaRelasiPartDefect(partId)

    fun bacaTemplateDefectPart(partId: String): HasilOperasi<List<TemplateDefectPart>> =
        repositoriLokal.bacaTemplateDefectPart(partId)
}
