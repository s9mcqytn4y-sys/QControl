package id.primaraya.qcontrol.ranah.usecase

import id.primaraya.qcontrol.data.lokal.repositori.RepositoriStatusDatabaseLokal
import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.ranah.model.InformasiDatabaseLokal

class PeriksaDatabaseLokalUseCase(
    private val repositori: RepositoriStatusDatabaseLokal
) {
    suspend operator fun invoke(): HasilOperasi<InformasiDatabaseLokal> {
        return repositori.periksaStatus()
    }
}
