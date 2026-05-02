Act as a strict Kotlin Software Architect. Now that the base project is set up, I need to implement Clean Architecture and MVI (Model-View-Intent) pattern for the QControl app.

Rule: Use Indonesian for domain/business entities (e.g., InspeksiHarian, DataDefect) and English for technical suffixes (e.g., ViewModel, Repository, Screen).

Please generate the directory structure and boilerplate code for the first feature: "Dashboard".

1. Directory Structure: Instruct me how to organize packages into:
    - `core` (theme, di, network)
    - `data` (repository_impl, remote, local)
    - `domain` (model, repository_interface, usecase)
    - `presentation` (ui screens, viewmodels, contracts)

2. Generate MVI Boilerplate for Dashboard Feature (inside `presentation/dashboard/`):
    - `DashboardContract.kt`: Create a sealed interface `Intent` (e.g., LoadDashboardData), a data class `State` (holding isLoading, totalCheck, totalDefect, rasioNg), and a sealed interface `Effect` (e.g., ShowError). Use manufacturing copy like "Memuat Rasio NG...".
    - `DashboardViewModel.kt`: Create a ViewModel utilizing Kotlin Coroutines `StateFlow` to manage the `DashboardContract.State` and a `SharedFlow` for Effects.
    - `DashboardScreen.kt`: Create a simple Composable screen that observes the ViewModel's state and displays the total defect and NG ratio using the Material 3 typography and colors from our theme.

Ensure all code follows Kotlin 2.x standard syntax and best practices for Compose Desktop.