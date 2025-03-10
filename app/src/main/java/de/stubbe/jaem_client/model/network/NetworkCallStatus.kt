package de.stubbe.jaem_client.model.network

import de.stubbe.jaem_client.model.enums.NetworkCallStatusType
import de.stubbe.jaem_client.network.NetworkConnectionState

data class NetworkCallStatus <T>(
    val response: T?,
    val status: NetworkCallStatusType
) {

    companion object {

        suspend fun <T> create(connectionState: NetworkConnectionState, block: suspend () -> T): NetworkCallStatus<T> {
            when (connectionState) {
                NetworkConnectionState.Available -> {
                    return try {
                        NetworkCallStatus(
                            block(),
                            NetworkCallStatusType.SUCCESS
                        )
                    } catch (e: Exception) {
                        NetworkCallStatus(
                            null,
                            NetworkCallStatusType.ERROR
                        )
                    }
                }
                NetworkConnectionState.Unavailable -> {
                    return NetworkCallStatus(
                        null,
                        NetworkCallStatusType.NO_INTERNET
                    )
                }
            }
        }

    }

}