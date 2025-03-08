package de.stubbe.jaem_client.model.network

import de.stubbe.jaem_client.model.ED25519Client
import de.stubbe.jaem_client.model.enums.AsymmetricEncryption
import de.stubbe.jaem_client.model.enums.MessageType
import de.stubbe.jaem_client.utils.toInt
import de.stubbe.jaem_client.utils.toLong
import de.stubbe.jaem_client.utils.toShort

/**
 * Repräsentiert eine empfangene Nachricht
 */
data class ReceivedMessage(
    val senderUid: String,
    val timestamp: Long,
    val messageParts: MutableList<MessagePart>,
) {
    companion object {
        private const val UID_LENGTH = 36
        private const val SIGNATURE_LENGTH = 64
        private const val TIMESTAMP_LENGTH = 8
        private const val MESSAGE_SIZE_BYTES = 8
        private const val TYPE_BYTES = 2
        private const val SIZE_BYTES = 4

        fun extractMessages(data: ByteArray): List<ByteArray> {
            val messages = mutableListOf<ByteArray>()
            var offset = 0
            while (offset < data.size) {
                val messageSize = data.copyOfRange(offset, offset + MESSAGE_SIZE_BYTES).toLong()
                offset += MESSAGE_SIZE_BYTES
                messages.add(data.copyOfRange(offset, offset + messageSize.toInt()))
                offset += messageSize.toInt()
            }
            return messages
        }

        suspend fun fromByteArray(
            encryptedData: ByteArray,
            localClient: ED25519Client?,
            fetchEncryptionContext: suspend (String) -> EncryptionContext
        ): ReceivedMessage {
            val decryptedData = AsymmetricEncryption.RSA.decrypt(encryptedData, localClient?.rsaPrivateKey!!)
            val senderUid = String(decryptedData.copyOfRange(0, UID_LENGTH))
            val encryptedMessage = decryptedData.copyOfRange(UID_LENGTH, decryptedData.size)

            val encryptionContext = fetchEncryptionContext(senderUid)
            val (client, otherClient, algorithm) = encryptionContext
            val aesKey = algorithm.generateSymmetricKey(otherClient!!.x25519PublicKey!!, client!!.x25519PrivateKey!!)
            val decryptedMessage = algorithm.decrypt(encryptedMessage, aesKey)

            val signature = decryptedMessage.copyOfRange(0, SIGNATURE_LENGTH)
            val timestamp = decryptedMessage.copyOfRange(SIGNATURE_LENGTH, SIGNATURE_LENGTH + TIMESTAMP_LENGTH).toLong()
            val messageContent = decryptedMessage.copyOfRange(SIGNATURE_LENGTH + TIMESTAMP_LENGTH, decryptedMessage.size)

            if (!algorithm.checkSign(messageContent, signature, otherClient.ed25519PublicKey!!)) {
                throw SecurityException("Message integrity compromised")
            }

            val messageParts = mutableListOf<MessagePart>()
            var offset = UID_LENGTH
            while (offset < messageContent.size) {
                val type = messageContent.copyOfRange(offset, offset + TYPE_BYTES).toShort()
                offset += TYPE_BYTES
                val size = messageContent.copyOfRange(offset, offset + SIZE_BYTES).toInt()
                offset += SIZE_BYTES
                val content = messageContent.copyOfRange(offset, offset + size)
                offset += size
                messageParts.add(MessagePart(MessageType.entries[type.toInt()], size, content))
            }

            return ReceivedMessage(senderUid, timestamp, messageParts)
        }
    }
}