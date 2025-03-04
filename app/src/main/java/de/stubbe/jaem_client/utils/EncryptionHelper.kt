package de.stubbe.jaem_client.utils


/**
 * Hilfsklasse für die Verschlüsselung und Entschlüsselung von Daten.
 */
import ED25519Client
import de.stubbe.jaem_client.model.enums.SymmetricEncryption
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security
import java.security.SignatureException

class EncryptionHelper(
    val encryption: SymmetricEncryption,
    val otherClient: ED25519Client? = null
) {
    val client: ED25519Client? = if (encryption == SymmetricEncryption.ED25519) ED25519Client() else null
    var aesKey: ByteArray? = null
        private set

    init {
        Security.addProvider(BouncyCastleProvider())
        otherClient?.let { setCommunicationPartner(it) }
    }

    fun setCommunicationPartner(otherClient: ED25519Client) {
        this.aesKey = requireNotNull(client) {
            "Client must be initialized before setting a communication partner."
        }.let { encryption.generateSymmetricKey(otherClient.x25519PublicKey!!, it.x25519PrivateKey!!) }
    }

    /**
     * Verschlüsselt die übergebenen Daten.
     *
     * @param data Zu verschlüsselnde Daten
     * @return Verschlüsselte Daten
     */
    fun encrypt(data: ByteArray): ByteArray {
        val client = requireNotNull(client) { "Client must be initialized before encrypting." }
        val aesKey = requireNotNull(aesKey) { "AES Key must be set before encrypting." }

        val signature = encryption.sign(data, client.ed25519PrivateKey!!)
        return encryption.encrypt(data, signature, aesKey)
    }

    /**
     * Entschlüsselt die übergebenen Daten.
     *
     * @param data Zu entschlüsselnde Daten
     * @return Entschlüsselte Daten
     * @throws SignatureException wenn die Signaturprüfung fehlschlägt
     */
    fun decrypt(data: ByteArray): ByteArray {
        val aesKey = requireNotNull(aesKey) { "AES Key must be set before decrypting." }
        val otherClient = requireNotNull(otherClient) { "Communication partner must be set before decrypting." }

        val clearBytes = encryption.decrypt(data, aesKey)
        val (signature, clearText) = clearBytes.sliceArray(0 until 64) to clearBytes.sliceArray(64 until clearBytes.size)

        if (!encryption.checkSign(clearText, signature, otherClient.ed25519PublicKey!!)) {
            throw SignatureException("Signature verification failed.")
        }

        return clearText
    }
}