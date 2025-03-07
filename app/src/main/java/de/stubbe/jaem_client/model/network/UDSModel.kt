package de.stubbe.jaem_client.model.network

import de.stubbe.jaem_client.model.enums.SymmetricEncryption
import kotlinx.serialization.Serializable
import java.security.PublicKey

@Serializable
data class UDSModel(
    val uid : String,
    val username : String,
    val publicKeys : List<PubKey>,
    val profilePicture: String
)

@Serializable
data class PubKey(
    val algorithm: SymmetricEncryption,
    val signatureKey: String,
    val exchangeKey: String,
    val rsaKey: PublicKey
)
