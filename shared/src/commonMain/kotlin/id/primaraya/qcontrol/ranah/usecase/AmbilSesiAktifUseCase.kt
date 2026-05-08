package id.primaraya.qcontrol.ranah.usecase

import id.primaraya.qcontrol.data.repositori.RepositoriAutentikasiLokal
import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.ranah.model.Autentikasi

class AmbilSesiAktifUseCase(
    private val repositoriLokal: RepositoriAutentikasiLokal
) {
    suspend fun eksekusi(): HasilOperasi<Autentikasi?> {
        return repositoriLokal.bacaSesi()
    }
}
