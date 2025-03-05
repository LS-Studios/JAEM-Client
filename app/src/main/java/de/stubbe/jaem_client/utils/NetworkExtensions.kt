package de.stubbe.jaem_client.utils

import de.stubbe.jaem_client.model.enums.AsymmetricEncryption
import de.stubbe.jaem_client.model.network.ReceiveBody
import de.stubbe.jaem_client.network.NetworkMessageModel
import retrofit2.Call
import retrofit2.HttpException
import java.nio.ByteBuffer
import java.nio.ByteOrder

fun <T> Call<T>.executeSafely(): Pair<T?, Throwable?> {
    return try {
        val response = execute()
        if (response.isSuccessful) {
            Pair(response.body(), null)
        } else {
            Pair(null, HttpException(response))
        }
    } catch (e: Throwable) {
        Pair(null, e)
    }
}

fun ReceiveBody.toByteArray(): ByteArray {
    val buffer = ByteBuffer.allocate(1 + signature.size + publicKey.size + 8)
    buffer.put(algorithm)
    buffer.put(signature)
    buffer.put(publicKey)
    buffer.putLong(unixTimeStamp.toLong())
    return buffer.array()
}

fun ByteArray.toNetworkMessageModel(encryptionData: ChatEncryptionData): NetworkMessageModel {
    val client = encryptionData.client!!
    val otherClient = encryptionData.otherClient!!
    val algorithm = encryptionData.encryption

    val rsaDecryptedMessage = AsymmetricEncryption.RSA.decrypt(this, client.rsaPrivateKey!!)
    val signatureKey = rsaDecryptedMessage.copyOfRange(0, 32)
    val aesEncryptedMessage = rsaDecryptedMessage.copyOfRange(32, rsaDecryptedMessage.size)

    val aesKey = algorithm.generateSymmetricKey(otherClient.x25519PublicKey!!, client.x25519PrivateKey!!)
    val aesDecryptedMessage = algorithm.decrypt(aesEncryptedMessage, aesKey)

    val signature = aesDecryptedMessage.copyOfRange(0,64)
    val messageType = aesDecryptedMessage.copyOfRange(64, 66)
    val message = aesEncryptedMessage.copyOfRange(66, aesDecryptedMessage.size)

}

fun splitResponseIntoMessages(
    message: ByteArray,
): List<ByteArray> {
    var pointer = 0
    val messages = MutableList(0) { byteArrayOf() }
    while (pointer < message.size) {
        val messageSize = message.copyOfRange(pointer, pointer + 8)
        val sizeBuffer = ByteBuffer.wrap(messageSize).order(ByteOrder.BIG_ENDIAN).getInt().toUInt().toInt()
        pointer += 8
        val messagePart = message.copyOfRange(pointer, pointer + sizeBuffer)
        messages.add(messagePart)
        pointer += sizeBuffer
    }
    return messages
}