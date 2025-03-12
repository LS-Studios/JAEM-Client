package de.stubbe.jaem_client.model

import kotlinx.serialization.Serializable

/**
 * Navigationsrouten für die App.
 */
sealed class NavRoute {

    @Serializable
    data object InitDevice : NavRoute()

    @Serializable
    data object UDS: NavRoute()

    @Serializable
    data object Settings : NavRoute()

    @Serializable
    data object ChatOverview : NavRoute()

    @Serializable
    data object Chat

    @Serializable
    data class ChatMessages(
        val profileUid: String,
        val chatId: Int,
        var searchEnabled: Boolean
    ) : NavRoute() {
        constructor() : this("", -1, false)
    }

    @Serializable
    data object Profile : NavRoute()

    @Serializable
    data class EditProfile(
        val profileUid: String?,
        val sharedCode: String?
    ) : NavRoute() {
        constructor() : this(null, null)
    }

}