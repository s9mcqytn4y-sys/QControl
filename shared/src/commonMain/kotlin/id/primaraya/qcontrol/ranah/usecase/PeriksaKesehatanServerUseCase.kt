package id.primaraya.qcontrol.ranah.usecase

import id.primaraya.qcontrol.data.repositori.RepositoriKesehatanServer
import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.ranah.model.StatusKesehatanServer

class PeriksaKesehatanServerUseCase(private val repositori: RepositoriKesehatanServer) {
    suspend operator fun invoke(): HasilOperasi<StatusKesehatanServer> {
        return repositori.periksaKesehatanServer()
    }
}
