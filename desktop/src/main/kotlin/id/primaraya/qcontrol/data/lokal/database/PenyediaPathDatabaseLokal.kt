package id.primaraya.qcontrol.data.lokal.database

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class PenyediaPathDatabaseLokal {
    fun dapatkanPathDatabase(): Path {
        val folderAplikasi = dapatkanFolderAplikasi()
        if (!Files.exists(folderAplikasi)) {
            Files.createDirectories(folderAplikasi)
        }
        return folderAplikasi.resolve("qcontrol_lokal.db")
    }

    private fun dapatkanFolderAplikasi(): Path {
        val os = System.getProperty("os.name").lowercase()
        return if (os.contains("win")) {
            val appData = System.getenv("APPDATA")
            if (appData != null) {
                Paths.get(appData, "QControl")
            } else {
                Paths.get(System.getProperty("user.home"), ".qcontrol")
            }
        } else {
            Paths.get(System.getProperty("user.home"), ".qcontrol")
        }
    }
}
