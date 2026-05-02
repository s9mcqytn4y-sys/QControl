package id.azure.qcontrol.data.repository_impl

import id.azure.qcontrol.domain.model.DataDefect
import id.azure.qcontrol.domain.model.InspeksiHarian
import id.azure.qcontrol.domain.repository_interface.QCRepository
import kotlinx.coroutines.flow.*

class QCRepositoryImpl : QCRepository {
    private val mockInspeksi = mutableListOf(
        InspeksiHarian("1", 1500, 45, 3.0f, System.currentTimeMillis()),
        InspeksiHarian("2", 1200, 36, 3.0f, System.currentTimeMillis() - 86400000)
    )

    private val mockDefects = mutableListOf(
        DataDefect("1", "Goresan", 15, "Area A", "1"),
        DataDefect("2", "Penyok", 10, "Area B", "1"),
        DataDefect("3", "Salah Warna", 20, "Area C", "1")
    )

    private val inspeksiFlow = MutableStateFlow(mockInspeksi.toList())
    private val defectFlow = MutableStateFlow(mockDefects.toList())

    override fun getLatestInspeksi(): Flow<InspeksiHarian?> = inspeksiFlow.map { it.firstOrNull() }

    override fun getAllInspeksi(): Flow<List<InspeksiHarian>> = inspeksiFlow.asStateFlow()

    override fun getDefectHistory(): Flow<List<DataDefect>> = defectFlow.asStateFlow()

    override suspend fun addInspeksi(inspeksi: InspeksiHarian) {
        mockInspeksi.add(0, inspeksi)
        inspeksiFlow.value = mockInspeksi.toList()
    }

    override suspend fun addDefect(defect: DataDefect) {
        mockDefects.add(0, defect)
        defectFlow.value = mockDefects.toList()
    }
}
