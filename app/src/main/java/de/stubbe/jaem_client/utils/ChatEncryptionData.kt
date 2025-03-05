package de.stubbe.jaem_client.utils


/**
 * Storage Class to store a chats encryption type and the clients used for encryption (including keys)
 */
import ED25519Client
import de.stubbe.jaem_client.model.enums.SymmetricEncryption
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security
import java.security.SignatureException

data class ChatEncryptionData(
    val encryption: SymmetricEncryption,
    val client: ED25519Client? = null,
    val otherClient: ED25519Client? = null
)
