package de.stubbe.jaem_client.model.network

import de.stubbe.jaem_client.model.ED25519Client
import de.stubbe.jaem_client.model.encryption.SymmetricEncryption

/**
 * Hält die Verschlüsselungsdaten für eine Konversation
 */
data class EncryptionContext(
    val localClient: ED25519Client?,
    val remoteClient: ED25519Client?,
    val encryptionAlgorithm: SymmetricEncryption.ED25519,
)
