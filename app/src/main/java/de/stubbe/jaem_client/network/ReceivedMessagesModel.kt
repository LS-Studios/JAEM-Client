package de.stubbe.jaem_client.network

import de.stubbe.jaem_client.data.SEPARATOR_BYTE
import de.stubbe.jaem_client.model.Attachments
import de.stubbe.jaem_client.model.ED25519Client
import de.stubbe.jaem_client.model.enums.AsymmetricEncryption
import de.stubbe.jaem_client.model.enums.AttachmentType
import de.stubbe.jaem_client.model.enums.MessageType
import de.stubbe.jaem_client.model.enums.SymmetricEncryption
import de.stubbe.jaem_client.utils.toByteArray
import de.stubbe.jaem_client.utils.toInt
import de.stubbe.jaem_client.utils.toLong
import de.stubbe.jaem_client.utils.toShort
import java.io.File
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Instant

/**
 * Klasse zum halten der Verschlüsselungsdaten
 */
data class ChatEncryptionData(
    val client: ED25519Client?,
    val otherClient: ED25519Client?,
    val encryption: SymmetricEncryption,
)

/**
 * Klasse zur darstellung einer Empfangenen Nachricht
 */
data class ReceivedMessagesModel(
    val senderUid: String,
    val timeSend: Long,
    val messages: MutableList<NetworkMessagePartModel>,
) {
    companion object {
        fun messageFromByteArray(byteArray: ByteArray): List<ByteArray> {
            val receivedMessages = mutableListOf<ByteArray>()

            var pointer = 0

            while (pointer < byteArray.size) {
                val responseMessageSize: ULong = byteArray.copyOfRange(pointer, pointer + 8).reversed().foldIndexed(0uL) { index, acc, byte -> acc + (byte.toULong() shl index * 8) }

                pointer += 8

                val responseMessage =
                    byteArray.copyOfRange(pointer, pointer + responseMessageSize.toInt())

                pointer += responseMessageSize.toInt()

                receivedMessages.add(responseMessage)
            }

            return receivedMessages
        }

        suspend fun fromByteArray(
            byteArray: ByteArray, deviceClient: ED25519Client?,
            getEncryptionDataFromUid: suspend (String) -> ChatEncryptionData
        ): ReceivedMessagesModel {
            val rsaDecryptedMessage =
                AsymmetricEncryption.RSA.decrypt(byteArray, deviceClient?.rsaPrivateKey!!)
            val uid = String(rsaDecryptedMessage.copyOfRange(0, 36))
            val aesEncryptedMessage = rsaDecryptedMessage.copyOfRange(36, rsaDecryptedMessage.size)

            val encryptionData = getEncryptionDataFromUid(uid)

            val client = encryptionData.client!!
            val otherClient = encryptionData.otherClient!!
            val algorithm = encryptionData.encryption

            val aesKey =
                algorithm.generateSymmetricKey(
                    otherClient.x25519PublicKey!!,
                    client.x25519PrivateKey!!
                )
            val aesDecryptedMessage = algorithm.decrypt(aesEncryptedMessage, aesKey)

            val signature = aesDecryptedMessage.copyOfRange(0, 64)
            val timeSend = aesDecryptedMessage.copyOfRange(64, 72).toLong()
            val messageData = aesDecryptedMessage.copyOfRange(72, aesDecryptedMessage.size)

            val messageUnchanged =
                algorithm.checkSign(messageData, signature, otherClient.ed25519PublicKey!!)

            if (!messageUnchanged) {
                throw Exception("Message has been changed")
            }

            val messageModel = ReceivedMessagesModel(uid, timeSend, mutableListOf())
            var pointer = 36
            while (pointer < messageData.size) {
                val messageType = messageData.copyOfRange(pointer, pointer + 2).toShort()
                pointer += 2
                val messageSize = messageData.copyOfRange(pointer, pointer + 4).toInt()
                pointer += 4
                val message = messageData.copyOfRange(pointer, pointer + messageSize)
                pointer += messageSize
                messageModel.messages.add(
                    NetworkMessagePartModel(
                        MessageType.entries[messageType.toInt()],
                        messageSize,
                        message
                    )
                )
            }

            return messageModel
        }
    }
}

/**
 * Klasse zur darstellung einer Empfangenen Nachricht
 */
data class NetworkMessagePartModel(
    val messageType: MessageType,
    val messageLength: Int,
    val message: ByteArray,
) {
    fun toByteArray(): ByteArray {
        return messageType.ordinal.toShort().toByteArray() + message.size.toByteArray() + message
    }

    companion object {

        fun buildMessageParts(
            message: String,
            attachments: Attachments?
        ): List<NetworkMessagePartModel> {
            val messagePart = NetworkMessagePartModel(
                MessageType.MESSAGE,
                message.length,
                message.toByteArray()
            )

            if (attachments == null) {
                return listOf(messagePart)
            }

            val messageType = when (attachments.type) {
                AttachmentType.FILE -> MessageType.FILE
                AttachmentType.IMAGE_AND_VIDEO -> MessageType.IMAGE_AND_VIDEO
            }

            val attachmentParts = attachments.attachmentPaths.map { filePath ->
                val file = File(filePath)
                val fileAsByteArray = Files.readAllBytes(Paths.get(filePath))
                NetworkMessagePartModel(
                    messageType,
                    fileAsByteArray.size,
                    file.name.toByteArray() + SEPARATOR_BYTE + fileAsByteArray,
                )
            }

            return listOf(messagePart) + attachmentParts
        }

    }
}

/**
 * Klasse zur darstellung einer zu sendenden Nachricht
 */
data class SendMessageModel(
    val algorithm: Byte,
    val recipientPublicKey: ByteArray,
    var rsaEncryptedMessage: ByteArray,
) {
    fun toByteArray(): ByteArray {
        return byteArrayOf(algorithm) + recipientPublicKey + rsaEncryptedMessage
    }

    companion object {

        fun buildSendMessageModel(
            chatEncryptionData: ChatEncryptionData,
            messageParts: List<NetworkMessagePartModel>
        ): SendMessageModel {
            val client = chatEncryptionData.client!!
            val otherClient = chatEncryptionData.otherClient!!
            val algorithm = chatEncryptionData.encryption

            val messageWithUID =
                client.profileUid!!.toByteArray() + messageParts.map { it.toByteArray() }.reduce { acc, byteArray -> acc + byteArray }

            val signature = algorithm.sign(messageWithUID, client.ed25519PrivateKey!!)
            val aesKey = algorithm.generateSymmetricKey(
                otherClient.x25519PublicKey!!,
                client.x25519PrivateKey!!
            )

            val request = SendMessageModel(
                client.encryption.code,
                otherClient.ed25519PublicKey!!.encoded,
                byteArrayOf()
            )

            val unixTimestamp = Instant.now().epochSecond.toByteArray()

            val message = unixTimestamp + messageWithUID
            val aesEncryptedMessage = algorithm.encrypt(message, signature, aesKey)

            val rsaEncryptedData = AsymmetricEncryption.RSA.encrypt(
                client.profileUid!!.toByteArray() + aesEncryptedMessage,
                otherClient.rsaPublicKey!!
            )
            request.rsaEncryptedMessage = rsaEncryptedData

            return request
        }

    }
}

data class ReceiveBody(
    val algorithm: Byte,
    val signature: ByteArray,
    val publicKey: ByteArray,
    val unixTime: Long
) {
    fun toByteArray(): ByteArray {
        val buffer = ByteBuffer.allocate(1 + signature.size + publicKey.size + 8)
        buffer.put(0u.toByte())
        buffer.put(signature)
        buffer.put(publicKey)
        buffer.putLong(unixTime)
        return buffer.array()
    }

    companion object {

        fun buildReceiveBody(
            deviceClient: ED25519Client
        ): ReceiveBody {
            val unixTimeStamp = Instant.now().epochSecond
            val timestamp = unixTimeStamp.toByteArray()

            val signature = deviceClient.encryption.sign(
                deviceClient.ed25519PublicKey!!.encoded + timestamp,
                deviceClient.ed25519PrivateKey!!
            )

            return ReceiveBody(
                deviceClient.encryption.code,
                signature,
                deviceClient.ed25519PublicKey!!.encoded,
                unixTimeStamp
            )
        }
    }
}