package id.primaraya.qcontrol.data.remote.pemetaan

import id.primaraya.qcontrol.data.remote.dto.KoneksiDatabaseDto
import id.primaraya.qcontrol.data.remote.dto.StatusKesehatanServerDto
import id.primaraya.qcontrol.ranah.model.StatusKesehatanServer
import id.primaraya.qcontrol.ranah.model.StatusKoneksiDatabase

fun StatusKesehatanServerDto.keDomain(): StatusKesehatanServer {
    return StatusKesehatanServer(
        status = this.status,
        namaAplikasi = this.namaAplikasi,
        versiApi = this.versiApi,
        waktuServer = this.waktuServer,
        zonaWaktu = this.zonaWaktu,
        koneksiDatabase = this.koneksiDatabase.keDomain()
    )
}

fun KoneksiDatabaseDto.keDomain(): StatusKoneksiDatabase {
    return StatusKoneksiDatabase(
        status = this.status,
        driver = this.driver
    )
}
