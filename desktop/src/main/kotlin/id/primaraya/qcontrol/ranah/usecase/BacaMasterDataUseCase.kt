package id.primaraya.qcontrol.ranah.usecase

import id.primaraya.qcontrol.data.repositori.RepositoriMasterDataQControl
import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.ranah.model.*

class BacaRingkasanMasterDataUseCase(private val repositori: RepositoriMasterDataQControl) {
    fun eksekusi(): HasilOperasi<RingkasanMasterData> = repositori.bacaRingkasanMasterDataLokal()
}

class BacaDaftarPartMasterUseCase(private val repositori: RepositoriMasterDataQControl) {
    fun eksekusi(kataKunci: String = ""): HasilOperasi<List<Part>> = repositori.bacaDaftarPart(kataKunci)
}

class BacaDaftarJenisDefectMasterUseCase(private val repositori: RepositoriMasterDataQControl) {
    fun eksekusi(kataKunci: String = ""): HasilOperasi<List<JenisDefect>> = repositori.bacaDaftarJenisDefect(kataKunci)
}

class BacaDaftarMaterialMasterUseCase(private val repositori: RepositoriMasterDataQControl) {
    fun eksekusi(kataKunci: String = ""): HasilOperasi<List<Material>> = repositori.bacaDaftarMaterial(kataKunci)
}

class BacaDaftarSlotWaktuMasterUseCase(private val repositori: RepositoriMasterDataQControl) {
    fun eksekusi(): HasilOperasi<List<SlotWaktu>> = repositori.bacaDaftarSlotWaktu()
}

class BacaDaftarLineProduksiMasterUseCase(private val repositori: RepositoriMasterDataQControl) {
    fun eksekusi(): HasilOperasi<List<LineProduksi>> = repositori.bacaDaftarLineProduksi()
}

class BacaRelasiPartDefectMasterUseCase(private val repositori: RepositoriMasterDataQControl) {
    fun eksekusi(partId: String): HasilOperasi<List<RelasiPartDefect>> = repositori.bacaRelasiPartDefect(partId)
}

class BacaDiagnostikMasterDataUseCase(private val repositori: RepositoriMasterDataQControl) {
    fun eksekusi(): Map<String, Int> = repositori.bacaDiagnostikMasterData()
}
