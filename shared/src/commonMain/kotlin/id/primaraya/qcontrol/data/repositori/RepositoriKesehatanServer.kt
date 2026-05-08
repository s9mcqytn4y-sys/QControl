package id.primaraya.qcontrol.data.repositori

import id.primaraya.qcontrol.data.remote.layanan.LayananKesehatanServerRemote
import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.ranah.model.StatusKesehatanServer

class RepositoriKesehatanServer(private val layananRemote: LayananKesehatanServerRemote) {
    suspend fun periksaKesehatanServer(): HasilOperasi<StatusKesehatanServer> {
        return layananRemote.periksaKesehatanServer()
    }
}
