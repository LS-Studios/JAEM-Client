package de.stubbe.jaem_client.model

import kotlinx.serialization.Serializable

/**
 * Navigationsrouten für die App.
 */
sealed class NavRoute {

    @Serializable
    data object ChatOverview : NavRoute()

    @Serializable
    data class Chat(
        val chatId: Int = -1,
        var searchEnabled: Boolean = false
    ) : NavRoute()

    @Serializable
    data class ProfileInfo(val profileId: Int = -1) : NavRoute()

    @Serializable
    data object CreateChat : NavRoute()

}