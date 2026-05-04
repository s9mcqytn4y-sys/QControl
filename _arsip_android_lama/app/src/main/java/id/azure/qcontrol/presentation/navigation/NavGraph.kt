package id.azure.qcontrol.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import id.azure.qcontrol.presentation.dashboard.DashboardScreen
import id.azure.qcontrol.presentation.dashboard.DashboardViewModel
import id.azure.qcontrol.presentation.dashboard.DashboardViewModelFactory
import id.azure.qcontrol.presentation.defect.DataDefectScreen
import id.azure.qcontrol.presentation.defect.DataDefectViewModel
import id.azure.qcontrol.presentation.inspection.InspeksiHarianScreen
import id.azure.qcontrol.presentation.inspection.InspeksiHarianViewModel
import id.azure.qcontrol.presentation.navigation.Route

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun QControlNavGraph() {
    val backStack = rememberNavBackStack(Route.Dashboard)
    
    val windowAdaptiveInfo = currentWindowAdaptiveInfo()
    val directive = remember(windowAdaptiveInfo) {
        calculatePaneScaffoldDirective(windowAdaptiveInfo)
            .copy(horizontalPartitionSpacerSize = 0.dp)
    }
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>(directive = directive)

    NavDisplay(
        backStack = backStack,
        onBack = { if (backStack.isNotEmpty()) backStack.removeAt(backStack.lastIndex) },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
            rememberViewModelStoreNavEntryDecorator<NavKey>()
        ),
        sceneStrategy = listDetailStrategy,
        entryProvider = entryProvider {
            entry<Route.Dashboard>(
                metadata = ListDetailSceneStrategy.listPane(
                    detailPlaceholder = {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Pilih menu untuk melihat detail")
                        }
                    }
                )
            ) {
                val dashboardViewModel: DashboardViewModel = viewModel(
                    factory = DashboardViewModelFactory.Factory
                )
                DashboardScreen(
                    viewModel = dashboardViewModel,
                    onNavigateToInspection = { backStack.add(Route.InspeksiHarian) },
                    onNavigateToDefect = { backStack.add(Route.DataDefect) }
                )
            }
            entry<Route.InspeksiHarian>(
                metadata = ListDetailSceneStrategy.detailPane()
            ) {
                val viewModel: InspeksiHarianViewModel = viewModel(
                    factory = DashboardViewModelFactory.Factory
                )
                InspeksiHarianScreen(
                    viewModel = viewModel,
                    onBack = { backStack.removeAt(backStack.lastIndex) }
                )
            }
            entry<Route.DataDefect>(
                metadata = ListDetailSceneStrategy.detailPane()
            ) {
                val viewModel: DataDefectViewModel = viewModel(
                    factory = DashboardViewModelFactory.Factory
                )
                DataDefectScreen(
                    viewModel = viewModel,
                    onBack = { backStack.removeAt(backStack.lastIndex) }
                )
            }
        }
    )
}
