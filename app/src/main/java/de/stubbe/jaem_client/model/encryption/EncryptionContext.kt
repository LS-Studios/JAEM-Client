package de.stubbe.jaem_client.model.encryption

import de.stubbe.jaem_client.model.ED25519Client

/**
 * Hält die Verschlüsselungsdaten für eine Konversation
 */
data class EncryptionContext(
    val localClient: ED25519Client?,
    val remoteClient: ED25519Client?,
    val encryptionAlgorithm: SymmetricEncryption.ED25519,
)
