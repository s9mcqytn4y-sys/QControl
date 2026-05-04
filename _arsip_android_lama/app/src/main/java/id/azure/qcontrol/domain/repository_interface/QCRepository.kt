package id.azure.qcontrol.domain.repository_interface

import id.azure.qcontrol.domain.model.DataDefect
import id.azure.qcontrol.domain.model.InspeksiHarian
import kotlinx.coroutines.flow.Flow

interface QCRepository {
    fun getLatestInspeksi(): Flow<InspeksiHarian?>
    fun getAllInspeksi(): Flow<List<InspeksiHarian>>
    fun getDefectHistory(): Flow<List<DataDefect>>
    suspend fun addInspeksi(inspeksi: InspeksiHarian)
    suspend fun addDefect(defect: DataDefect)
}
