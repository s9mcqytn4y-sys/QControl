package id.primaraya.qcontrol.ranah.usecase

import id.primaraya.qcontrol.data.repositori.RepositoriMasterDataQControl
import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.ranah.model.TemplateDefectPart

class BacaTemplateDefectPartUseCase(private val repositori: RepositoriMasterDataQControl) {
    suspend fun eksekusi(partId: String): HasilOperasi<List<TemplateDefectPart>> =
        repositori.bacaTemplateDefectPart(partId)
}
