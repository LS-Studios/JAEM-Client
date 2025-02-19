package de.stubbe.jaem_client.viewmodel

import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import de.stubbe.jaem_client.model.NavRoute
import kotlinx.coroutines.flow.MutableStateFlow

class NavigationViewModel: ViewModel() {

    private val currentScreen: MutableStateFlow<NavRoute> = MutableStateFlow(NavRoute.ChatOverview)

    private val navHostController: MutableStateFlow<NavHostController?> = MutableStateFlow(null)

    /**
     * Initialisiert das Navigation view model
     *
     * @param navHostController Navigation host controller
     */
    fun init(navHostController: NavHostController) {
        this.navHostController.value = navHostController
    }

    fun getCurrentScreenFlow() = currentScreen

    /**
     * Wechselt den Bildschirm
     *
     * @param navRoute Bildschirm zu dem gewechselt werden soll
     */
    fun changeScreen(navRoute: NavRoute) {
        if (currentScreen.value == navRoute) return

        navHostController.value?.navigate(navRoute)
    }

    /**
     * Zum letzten Bildschirm zurückkehren
     */
    fun goBack() {
        navHostController.value?.popBackStack()
    }
}