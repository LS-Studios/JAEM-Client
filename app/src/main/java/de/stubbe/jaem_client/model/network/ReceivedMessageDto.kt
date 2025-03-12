package de.stubbe.jaem_client.model.network

import de.stubbe.jaem_client.data.INT_BYTES
import de.stubbe.jaem_client.data.MESSAGE_SIZE_BYTES
import de.stubbe.jaem_client.data.RSA_ENCRYPTION_LENGTH
import de.stubbe.jaem_client.data.SHORT_BYTES
import de.stubbe.jaem_client.data.SIGNATURE_LENGTH
import de.stubbe.jaem_client.data.TIMESTAMP_LENGTH
import de.stubbe.jaem_client.data.UID_LENGTH
import de.stubbe.jaem_client.model.ED25519Client
import de.stubbe.jaem_client.model.encryption.EncryptionContext
import de.stubbe.jaem_client.model.enums.AsymmetricEncryption
import de.stubbe.jaem_client.model.enums.ContentMessageType
import de.stubbe.jaem_client.model.enums.MessageType
import de.stubbe.jaem_client.utils.toInt
import de.stubbe.jaem_client.utils.toLong
import de.stubbe.jaem_client.utils.toShort
import org.bouncycastle.crypto.params.X25519PublicKeyParameters

/**
 * Repräsentiert eine empfangene Nachricht
 */
data class ReceivedMessageDto(
    val messageType: MessageType,
    val senderUid: String,
    val timestamp: Long,
    val messagePartDtos: MutableList<MessagePartDto>,
) {
    companion object {
        fun extractMessageBytes(data: ByteArray): List<ByteArray> {
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
        ): ReceivedMessageDto {
            val encryptedSenderUid = encryptedData.copyOfRange(0, RSA_ENCRYPTION_LENGTH)
            val decryptedSenderUid = AsymmetricEncryption.RSA.decrypt(encryptedSenderUid, localClient?.rsaPrivateKey!!)
            val senderUid = String(decryptedSenderUid)

            val encryptedMessageType = encryptedData.copyOfRange(RSA_ENCRYPTION_LENGTH, RSA_ENCRYPTION_LENGTH + RSA_ENCRYPTION_LENGTH)
            val decryptedMessageType = AsymmetricEncryption.RSA.decrypt(encryptedMessageType, localClient.rsaPrivateKey!!)
            val messageType = MessageType.entries[decryptedMessageType.toShort().toInt()]

            val encryptedMessage = encryptedData.copyOfRange(RSA_ENCRYPTION_LENGTH + RSA_ENCRYPTION_LENGTH, encryptedData.size)

            var timestamp: Long = 0
            var messageContent: ByteArray = byteArrayOf()

            val encryptionContext = fetchEncryptionContext(senderUid)
            val (client, otherClient, algorithm) = encryptionContext

            if (messageType == MessageType.KEY_EXCHANGE) {
                val xPublicKey = AsymmetricEncryption.RSA.decrypt(encryptedMessage.copyOfRange(0, RSA_ENCRYPTION_LENGTH), client!!.rsaPrivateKey!!)
                val encryptedMessageContent = encryptedMessage.copyOfRange(RSA_ENCRYPTION_LENGTH, encryptedMessage.size)

                val aesKey = algorithm.generateSymmetricKey(
                    X25519PublicKeyParameters(xPublicKey),
                    client.x25519PrivateKey!!
                )

                val decryptedMessage = algorithm.decrypt(encryptedMessageContent, aesKey)

                timestamp = decryptedMessage.copyOfRange(0, RSA_ENCRYPTION_LENGTH + TIMESTAMP_LENGTH).toLong()
                messageContent = decryptedMessage.copyOfRange(TIMESTAMP_LENGTH, decryptedMessage.size)
            }
            else if (otherClient != null) {
                val aesKey = algorithm.generateSymmetricKey(
                    otherClient.x25519PublicKey!!,
                    client!!.x25519PrivateKey!!
                )
                val decryptedMessage = algorithm.decrypt(encryptedMessage, aesKey)

                val signature = decryptedMessage.copyOfRange(0, SIGNATURE_LENGTH)

                timestamp = decryptedMessage.copyOfRange(
                    SIGNATURE_LENGTH,
                    SIGNATURE_LENGTH + TIMESTAMP_LENGTH
                ).toLong()
                messageContent = decryptedMessage.copyOfRange(
                    SIGNATURE_LENGTH + TIMESTAMP_LENGTH,
                    decryptedMessage.size
                )

                if (!algorithm.checkSign(
                        signature = signature,
                        message = messageContent,
                        publicKey = otherClient.ed25519PublicKey!!
                    )
                ) {
                    throw SecurityException("Message integrity compromised")
                }
            }

            val messagePartDtos = mutableListOf<MessagePartDto>()
            var offset = UID_LENGTH
            while (offset < messageContent.size) {
                val type = messageContent.copyOfRange(offset, offset + SHORT_BYTES).toShort()
                offset += SHORT_BYTES
                val size = messageContent.copyOfRange(offset, offset + INT_BYTES).toInt()
                offset += INT_BYTES
                val content = messageContent.copyOfRange(offset, offset + size)
                offset += size
                messagePartDtos.add(
                    MessagePartDto(
                        type = ContentMessageType.entries[type.toInt()],
                        length = size,
                        content = content
                    )
                )
            }

            return ReceivedMessageDto(messageType, senderUid, timestamp, messagePartDtos)
        }
    }
}