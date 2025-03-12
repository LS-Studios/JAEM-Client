package de.stubbe.jaem_client.model.network

import de.stubbe.jaem_client.model.enums.NetworkCallStatusType
import de.stubbe.jaem_client.network.NetworkConnectionState

data class NetworkCallStatus <T>(
    val exception: Exception?,
    val response: T?,
    val status: NetworkCallStatusType
) {

    companion object {

        suspend fun <T> create(
            connectionState: NetworkConnectionState,
            nullIsError: Boolean = true,
            block: suspend () -> T
        ): NetworkCallStatus<T> {
            when (connectionState) {
                NetworkConnectionState.Available -> {
                    return try {
                        val response = block()

                        if (response == null && nullIsError) {
                            NetworkCallStatus(
                                Exception("Response is null"),
                                null,
                                NetworkCallStatusType.ERROR
                            )
                        } else {
                            NetworkCallStatus(
                                null,
                                response,
                                NetworkCallStatusType.SUCCESS
                            )
                        }
                    } catch (e: Exception) {
                        NetworkCallStatus(
                            e,
                            null,
                            NetworkCallStatusType.ERROR
                        )
                    }
                }
                NetworkConnectionState.Unavailable -> {
                    return NetworkCallStatus(
                        Exception("No internet connection"),
                        null,
                        NetworkCallStatusType.NO_INTERNET
                    )
                }
            }
        }

    }

}