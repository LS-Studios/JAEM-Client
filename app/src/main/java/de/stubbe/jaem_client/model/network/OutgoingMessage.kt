package de.stubbe.jaem_client.model.network

import de.stubbe.jaem_client.model.enums.AsymmetricEncryption
import de.stubbe.jaem_client.utils.getUnixTime
import de.stubbe.jaem_client.utils.toByteArray

/**
 * Repräsentiert eine zu sendende Nachricht
 */
data class OutgoingMessage(
    val algorithm: Byte,
    val recipientPublicKey: ByteArray,
    var encryptedContent: ByteArray,
) {
    fun toByteArray(): ByteArray = byteArrayOf(algorithm) + recipientPublicKey + encryptedContent

    companion object {
        fun create(
            context: EncryptionContext,
            messageParts: List<MessagePart>
        ): OutgoingMessage {
            val (client, otherClient, algorithm) = context
            val messageWithId = client!!.profileUid!!.toByteArray() + messageParts.flatMap { it.toByteArray().toList() }
            val signature = algorithm.sign(messageWithId, client.ed25519PrivateKey!!)
            val aesKey = algorithm.generateSymmetricKey(otherClient!!.x25519PublicKey!!, client.x25519PrivateKey!!)

            val timestamp = getUnixTime().toByteArray()
            val encryptedMessage = algorithm.encrypt(timestamp + messageWithId, signature, aesKey)
            val rsaEncrypted = AsymmetricEncryption.RSA.encrypt(client.profileUid!!.toByteArray() + encryptedMessage, otherClient.rsaPublicKey!!)

            return OutgoingMessage(client.encryption.code, otherClient.ed25519PublicKey!!.encoded, rsaEncrypted)
        }
    }
}