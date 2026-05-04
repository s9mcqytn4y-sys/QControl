package id.primaraya.qcontrol.ranah.usecase

import id.primaraya.qcontrol.data.lokal.repositori.RepositoriAutentikasiLokal
import id.primaraya.qcontrol.inti.hasil.HasilOperasi

class KeluarSesiUseCase(
    private val repositoriLokal: RepositoriAutentikasiLokal
) {
    suspend fun eksekusi(): HasilOperasi<Unit> {
        return repositoriLokal.hapusSesi()
    }
}
