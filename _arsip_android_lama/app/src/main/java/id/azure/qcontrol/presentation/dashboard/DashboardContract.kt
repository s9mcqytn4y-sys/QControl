package id.azure.qcontrol.presentation.dashboard

sealed interface DashboardIntent {
    data object LoadDashboardData : DashboardIntent
    data object RefreshData : DashboardIntent
}

data class DashboardState(
    val isLoading: Boolean = false,
    val totalCheck: Int = 0,
    val totalDefect: Int = 0,
    val rasioNg: Float = 0f,
    val error: String? = null
)

sealed interface DashboardEffect {
    data class ShowError(val message: String) : DashboardEffect
}
