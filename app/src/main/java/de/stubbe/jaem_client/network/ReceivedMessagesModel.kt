package de.stubbe.jaem_client.network

import ED25519Client
import de.stubbe.jaem_client.model.enums.AsymmetricEncryption
import de.stubbe.jaem_client.model.enums.SymmetricEncryption
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.security.SignatureException
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
    val uid: String,
    val timeSend: ULong,
    val messages: MutableList<NetworkMessageModel>,
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
            val timeSend =
                ByteBuffer.wrap(aesDecryptedMessage.copyOfRange(64, 72)).order(ByteOrder.BIG_ENDIAN)
                    .getLong()
            val messageData = aesDecryptedMessage.copyOfRange(72, aesDecryptedMessage.size)

            val messageUnchanged =
                algorithm.checkSign(messageData, signature, otherClient.ed25519PublicKey!!)

            if (!messageUnchanged) {
                throw SignatureException("Signature is not valid")
            }

            val messageModel = ReceivedMessagesModel(uid, timeSend.toULong(), mutableListOf())
            var pointer = 0
            while (pointer < messageData.size) {
                val messageType = ByteBuffer.wrap(messageData.copyOfRange(pointer, pointer + 2))
                    .order(ByteOrder.BIG_ENDIAN).getShort().toUShort()
                pointer += 2
                val messageSize = ByteBuffer.wrap(messageData.copyOfRange(pointer, pointer + 4))
                    .order(ByteOrder.BIG_ENDIAN).getInt().toUInt().toInt()
                pointer += 4
                val message = messageData.copyOfRange(pointer, pointer + messageSize)
                pointer += messageSize
                messageModel.messages.add(
                    NetworkMessageModel(
                        messageType,
                        message.size.toUInt(),
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
data class NetworkMessageModel(
    val messageType: UShort,
    val messageLength: UInt,
    val message: ByteArray,
) {
    fun toByteArray(): ByteArray {
        return ByteBuffer.allocate(2 + 4 + message.size)
            .putShort(messageType.toShort())
            .putInt(message.size)
            .put(message)
            .array()
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
            messages: List<ByteArray>
        ): SendMessageModel {
            val client = chatEncryptionData.client!!
            val otherClient = chatEncryptionData.otherClient!!
            val algorithm = chatEncryptionData.encryption

            val messageWithUID =
                client.profileUid!!.toByteArray() + messages.reduce(ByteArray::plus)
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

            val unixTimestamp = Instant.now().epochSecond.toULong().toString().toByteArray()

            val message = unixTimestamp + messages.reduce(ByteArray::plus)
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
    val unixTime: ULong
) {
    fun toByteArray(): ByteArray {
        val buffer = ByteBuffer.allocate(1 + signature.size + publicKey.size + 8)
        buffer.put(0u.toByte())
        buffer.put(signature)
        buffer.put(publicKey)
        buffer.putLong(unixTime.toLong())
        return buffer.array()
    }

    companion object {

        fun buildReceiveBody(
            deviceClient: ED25519Client
        ): ReceiveBody {
            val unixTimeStamp = Instant.now().epochSecond.toULong()
            val timestamp = ByteBuffer.allocate(Long.SIZE_BYTES)
                .order(ByteOrder.BIG_ENDIAN)
                .putLong(unixTimeStamp.toLong())
                .array()

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