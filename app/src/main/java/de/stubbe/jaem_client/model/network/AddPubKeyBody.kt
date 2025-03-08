package de.stubbe.jaem_client.model.network

import kotlinx.serialization.Serializable

@Serializable
data class AddPubKeyBody(
    val uid: String,
    val pubKey: PubKey
)
