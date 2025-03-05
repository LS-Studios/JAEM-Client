package de.stubbe.jaem_client.network

import kotlinx.serialization.Serializable
import java.nio.ByteBuffer

data class NetworkMessageModel(
    val signatureKey: ByteArray,
    val signature: ByteArray,
    val timeSend: ULong,
    val messages: List<Message>,
)

data class SendNetworkMessageModel(
    val algorithm: Byte,
    val recipientPublicKey: ByteArray,
    var rsaEncryptedMessage: ByteArray,
)

fun SendNetworkMessageModel.toByteArray(): ByteArray{
    return algorithm.toString().toByteArray()  + recipientPublicKey + rsaEncryptedMessage
}

data class Message(
    val messageType: UShort,
    val messageLength: UInt,
    val message: ByteArray,
)

fun Message.toByteArray(): ByteArray{
    return ByteBuffer.allocate(2 + 4 + message.size)
        .putShort(messageType.toShort())
        .putInt(message.size)
        .put(message)
        .array()
}
