package de.stubbe.jaem_client.model.network

import de.stubbe.jaem_client.model.encryption.EncryptionContext
import de.stubbe.jaem_client.model.enums.AsymmetricEncryption
import de.stubbe.jaem_client.model.enums.ContentMessageType
import de.stubbe.jaem_client.model.enums.MessageType
import de.stubbe.jaem_client.utils.getUnixTime
import de.stubbe.jaem_client.utils.toByteArray

/**
 * Repräsentiert eine zu sendende Nachricht
 */
data class OutgoingMessageDto(
    val algorithm: Byte,
    val recipientPublicKey: ByteArray,
    var encryptedContent: ByteArray,
) {
    fun toByteArray(): ByteArray = byteArrayOf(algorithm) + recipientPublicKey + encryptedContent

    companion object {
        fun create(
            context: EncryptionContext,
            messagePart: List<MessagePartDto>,
            messageType: MessageType = MessageType.CONTENT,
        ): OutgoingMessageDto {
            val (client, otherClient, algorithm) = context

            val timestamp = getUnixTime().toByteArray()

            val messageWithUid = client!!.profileUid!!.toByteArray() + messagePart.flatMap { it.toByteArray().toList() }
            val signature = algorithm.sign(messageWithUid, client.ed25519PrivateKey!!)
            val aesKey = algorithm.generateSymmetricKey(
                otherClient!!.x25519PublicKey!!,
                client.x25519PrivateKey!!
            )

            val message = timestamp + messageWithUid
            val encryptedMessage = algorithm.encrypt(signature, message, aesKey)
            val rsaEncryptedUid = AsymmetricEncryption.RSA.encrypt(client.profileUid!!.toByteArray(), otherClient.rsaPublicKey!!)
            val rsaEncryptedMessageType = AsymmetricEncryption.RSA.encrypt(messageType.ordinal.toShort().toByteArray(), otherClient.rsaPublicKey!!)
            val finalMessage = rsaEncryptedUid + rsaEncryptedMessageType + encryptedMessage

            return OutgoingMessageDto(client.encryption.code, otherClient.ed25519PublicKey!!.encoded, finalMessage)
        }

        fun createKeyExchange(
            context: EncryptionContext,
            content: ByteArray,
        ): OutgoingMessageDto {
            val (client, otherClient, algorithm) = context

            val timestamp = getUnixTime().toByteArray()

            val messageWithUid = client!!.profileUid!!.toByteArray() + MessagePartDto(ContentMessageType.NONE, content.size, content).toByteArray()
            val aesKey = algorithm.generateSymmetricKey(
                otherClient!!.x25519PublicKey!!,
                client.x25519PrivateKey!!
            )

            val message = timestamp + messageWithUid
            val encryptedMessage = algorithm.encrypt(message, aesKey)
            val rsaEncryptedUid = AsymmetricEncryption.RSA.encrypt(client.profileUid!!.toByteArray(), otherClient.rsaPublicKey!!)
            val rsaEncryptedMessageType = AsymmetricEncryption.RSA.encrypt(MessageType.KEY_EXCHANGE.ordinal.toShort().toByteArray(), otherClient.rsaPublicKey!!)
            val rsaEncryptedAesKey = AsymmetricEncryption.RSA.encrypt(aesKey, otherClient.rsaPublicKey!!)
            val finalMessage = rsaEncryptedUid + rsaEncryptedMessageType + rsaEncryptedAesKey + encryptedMessage

            return OutgoingMessageDto(client.encryption.code, otherClient.ed25519PublicKey!!.encoded, finalMessage)
        }
    }
}