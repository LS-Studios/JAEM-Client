package de.stubbe.jaem_client.utils

import ED25519Client
import de.stubbe.jaem_client.model.enums.SymmetricEncryption
import org.bouncycastle.util.encoders.Hex
import java.security.AsymmetricKey
import java.security.Security
import java.security.SignatureException
import java.util.Base64


/**
 * Hilfsklasse für die Verschlüsselung und Entschlüsselung von Daten.
 */
class EncryptionHelper() {
    var client: ED25519Client? = null
    var aesKey: ByteArray? = null
    var otherClient: ED25519Client? = null

    init {
        Security.addProvider(org.bouncycastle.jce.provider.BouncyCastleProvider())
        if(encryption == SymmetricEncryption.ED25519){
            this.client = ED25519Client("Bob")
        }
    }

    constructor(encryption: SymmetricEncryption, otherClient: ED25519Client) : this(encryption) {
        this.otherClient = otherClient
        this.aesKey = encryption.generateSymmetricKey(otherClient.x25519PublicKey!! , client!!.x25519PrivateKey!!)
    }

    fun setCommunicationPartner(otherClient: ED25519Client){
        this.otherClient = otherClient
        this.aesKey = encryption.generateSymmetricKey(otherClient.x25519PublicKey!! , client!!.x25519PrivateKey!!)
    }

    /**
     * Verschlüsselt die übergebene Daten.
     *
     * @param data: Zu verschlüsselnde Daten
     * @return Verschlüsselte Daten
     */
    fun encrypt(data: ByteArray): ByteArray {
        val signature = encryption.sign(data, client!!.ed25519PrivateKey!!)
        val encryptedMessage = encryption.encrypt(data, signature, this.aesKey!!)
        println(Base64.getEncoder().encodeToString(encryptedMessage))
        return encryptedMessage
    }

    /**
     * Entschlüsselt die übergebene Daten.
     *
     * @param data: Zu entschlüsselnde Daten
     * @return Entschlüsselte Daten
     */
    fun decrypt(data: ByteArray): ByteArray {
        val clearBytes = encryption.decrypt(data, this.aesKey!!)
        val signature = clearBytes.copyOfRange(0,64)
        val clearText = clearBytes.copyOfRange(64, clearBytes.size)
        val isValidSignature = encryption.checkSign(clearText, signature, otherClient!!.ed25519PublicKey!!)
        if(!isValidSignature) {
            throw SignatureException("Signature verification failed.")
        }

        return clearText
    }
}