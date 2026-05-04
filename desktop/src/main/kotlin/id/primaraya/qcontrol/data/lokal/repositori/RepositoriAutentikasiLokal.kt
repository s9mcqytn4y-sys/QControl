package id.primaraya.qcontrol.data.lokal.repositori

import id.primaraya.qcontrol.data.lokal.database.KoneksiDatabaseLokal
import id.primaraya.qcontrol.inti.hasil.HasilOperasi
import id.primaraya.qcontrol.inti.kesalahan.KesalahanAplikasi
import id.primaraya.qcontrol.ranah.model.Autentikasi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

class RepositoriAutentikasiLokal(
    private val koneksiDatabase: KoneksiDatabaseLokal
) {
    suspend fun simpanSesi(autentikasi: Autentikasi): HasilOperasi<Unit> = withContext(Dispatchers.IO) {
        try {
            koneksiDatabase.bukaKoneksi().use { koneksi ->
                val sql = """
                    INSERT INTO sesi_autentikasi (id, token, nama_pengguna, peran, dibuat_pada)
                    VALUES (1, ?, ?, ?, ?)
                    ON CONFLICT(id) DO UPDATE SET
                        token=excluded.token,
                        nama_pengguna=excluded.nama_pengguna,
                        peran=excluded.peran,
                        dibuat_pada=excluded.dibuat_pada
                """.trimIndent()

                koneksi.prepareStatement(sql).use { statement ->
                    statement.setString(1, autentikasi.token)
                    statement.setString(2, autentikasi.namaPengguna)
                    statement.setString(3, autentikasi.peran)
                    statement.setString(4, Instant.now().toString())
                    statement.executeUpdate()
                }
            }
            HasilOperasi.Berhasil(Unit)
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal menyimpan sesi: ${e.message}"))
        }
    }

    suspend fun bacaSesi(): HasilOperasi<Autentikasi?> = withContext(Dispatchers.IO) {
        try {
            koneksiDatabase.bukaKoneksi().use { koneksi ->
                val sql = "SELECT token, nama_pengguna, peran FROM sesi_autentikasi WHERE id = 1"
                koneksi.createStatement().use { statement ->
                    val resultSet = statement.executeQuery(sql)
                    if (resultSet.next()) {
                        val autentikasi = Autentikasi(
                            token = resultSet.getString("token"),
                            namaPengguna = resultSet.getString("nama_pengguna"),
                            peran = resultSet.getString("peran")
                        )
                        HasilOperasi.Berhasil(autentikasi)
                    } else {
                        HasilOperasi.Berhasil(null)
                    }
                }
            }
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal membaca sesi: ${e.message}"))
        }
    }

    suspend fun hapusSesi(): HasilOperasi<Unit> = withContext(Dispatchers.IO) {
        try {
            koneksiDatabase.bukaKoneksi().use { koneksi ->
                val sql = "DELETE FROM sesi_autentikasi WHERE id = 1"
                koneksi.createStatement().use { statement ->
                    statement.execute(sql)
                }
            }
            HasilOperasi.Berhasil(Unit)
        } catch (e: Exception) {
            HasilOperasi.Gagal(KesalahanAplikasi.PenyimpananLokal("Gagal menghapus sesi: ${e.message}"))
        }
    }
}
