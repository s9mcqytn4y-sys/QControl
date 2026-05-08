package id.primaraya.qcontrol.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File
import java.nio.file.Paths

class JvmDatabaseDriverFactory : DatabaseDriverFactory {
    override fun createDriver(): SqlDriver {
        val databasePath = dapatkanPathDatabase()
        val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:${databasePath.absolutePath}")
        
        // Buat tabel jika belum ada (hanya sekali)
        if (!databasePath.exists()) {
            QControlDatabase.Schema.create(driver)
        }
        
        return driver
    }

    private fun dapatkanPathDatabase(): File {
        val os = System.getProperty("os.name").lowercase()
        val folder = if (os.contains("win")) {
            val appData = System.getenv("APPDATA")
            if (appData != null) {
                Paths.get(appData, "QControl")
            } else {
                Paths.get(System.getProperty("user.home"), ".qcontrol")
            }
        } else {
            Paths.get(System.getProperty("user.home"), ".qcontrol")
        }
        
        if (!folder.toFile().exists()) {
            folder.toFile().mkdirs()
        }
        
        return folder.resolve("qcontrol_v2.db").toFile()
    }
}
