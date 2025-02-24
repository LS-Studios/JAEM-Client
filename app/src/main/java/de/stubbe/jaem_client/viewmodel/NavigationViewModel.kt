package de.stubbe.jaem_client.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.toRoute
import de.stubbe.jaem_client.model.NavRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class NavigationViewModel: ViewModel() {

    @PublishedApi
    internal val navHostController: MutableStateFlow<NavHostController?> = MutableStateFlow(null)

    /**
     * Initialisiert das Navigation view model
     *
     * @param navHostController Navigation host controller
     */
    fun init(navHostController: NavHostController) {
        this.navHostController.value = navHostController
    }

    inline fun <reified T> getCurrentRouteFlow(): StateFlow<T?> {
        return navHostController.value?.currentBackStackEntryFlow
            ?.map { backStackEntry ->
                try {
                    backStackEntry.toRoute<T>()
                } catch (e: Exception) {
                    null
                }
            }
            ?.stateIn(viewModelScope, SharingStarted.Eagerly, null)
            ?: MutableStateFlow(null)
    }

    /**
     * Wechselt den Bildschirm
     *
     * @param navRoute Bildschirm zu dem gewechselt werden soll
     */
    fun changeScreen(navRoute: NavRoute) {
        navHostController.value?.navigate(navRoute)
    }

    /**
     * Navigiert zurück zum vorherigen Bildschirm.
     */
    fun goBack() {
        navHostController.value?.popBackStack()
    }

    /**
     * Erweiterungsfunktion für [NavController], um zum letzten Bildschirm zurückzukehren und dabei
     * die Argumente des vorherigen Bildschirms zu aktualisieren.
     *
     * Diese Methode stellt sicher, dass nur die übergebenen Argumente überschrieben werden, während
     * alle anderen unverändert bleiben.
     *
     * @param update Lambda-Funktion zum partiellen Aktualisieren der Argumente des vorherigen Bildschirms.
     */
    inline fun <reified T : Any> goBack(update: T.() -> T) {
        navHostController.value?.let { navHostController ->
            val previousBackStackEntry = navHostController.previousBackStackEntry
            navHostController.popBackStack()

            previousBackStackEntry?.let { navBackStackEntry ->
                val currentData = navBackStackEntry.toRoute<T>()
                val updatedData = currentData.update()

                val currentRoute = navHostController.currentBackStackEntry?.destination?.route
                if (currentRoute != null) {
                    navHostController.popBackStack(currentRoute, inclusive = true)
                }

                // Navigiere zur aktualisierten Route
                navHostController.navigate(updatedData) {
                    launchSingleTop = true // Verhindert doppelte Navigation
                    restoreState = true    // Behält den Zustand des Fragments bei
                }
            }
        }
    }

}