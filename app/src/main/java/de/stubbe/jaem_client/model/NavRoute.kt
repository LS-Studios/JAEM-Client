package de.stubbe.jaem_client.model

import kotlinx.serialization.Serializable

/**
 * Navigationsrouten für die App.
 */
sealed class NavRoute {

    @Serializable
    data object ChatOverview : NavRoute()

    @Serializable
    data object Chat

    @Serializable
    data class ChatMessages(
        val profileId: Int,
        val chatId: Int,
        var searchEnabled: Boolean
    ) : NavRoute() {
        constructor() : this(-1, -1, false)
    }

    @Serializable
    data object Profile : NavRoute()

    @Serializable
    data class EditProfile(
        val profileId: Int
    ) : NavRoute() {
        constructor() : this(-1)
    }

}