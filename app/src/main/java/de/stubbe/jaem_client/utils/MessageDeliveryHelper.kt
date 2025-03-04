package de.stubbe.jaem_client.utils

import ED25519Client
import de.stubbe.jaem_client.model.enums.AsymmetricEncryption
import de.stubbe.jaem_client.model.enums.SymmetricEncryption
import okhttp3.RequestBody
import java.nio.ByteBuffer
import java.nio.ByteOrder

object MessageDeliveryHelper {

    fun constructMessage(
        client: ED25519Client,
        otherClient: ED25519Client,
        algorithm: SymmetricEncryption,
        message: ByteArray,
    ): ByteArray {
        val messageWithPublicKey = message + client.ed25519PublicKey!!.encoded
        val signature = algorithm.sign(messageWithPublicKey, client.ed25519PrivateKey!!)
        val aesKey = algorithm.generateSymmetricKey(otherClient.x25519PublicKey!!, client.x25519PrivateKey!!)
        val aesEncrypted = algorithm.encrypt(message, signature, aesKey)
        val aesEncryptedWithEDPubKey = client.ed25519PublicKey!!.encoded + aesEncrypted

        val rsaKeysRecipient = AsymmetricEncryption.RSA.generate()
        val rsaEncrypted = AsymmetricEncryption.RSA.encrypt(aesEncryptedWithEDPubKey, rsaKeysRecipient.public)

        val finalMessage = byteArrayOf(client.encryption.code) + otherClient.ed25519PublicKey!!.encoded + rsaKeysRecipient.public.encoded + rsaEncrypted

        return finalMessage
    }

    fun getMessagesBody(
        client: ED25519Client,
        algorithm: SymmetricEncryption,
    ): RequestBody {
        val encCode = algorithm.code
        val unixTime = System.currentTimeMillis().toString().toByteArray()
        val signature = algorithm.sign(unixTime, client.ed25519PrivateKey!!)
        val message = byteArrayOf(encCode) + signature + client.ed25519PublicKey!!.encoded + unixTime
        return RequestBody.create(null, message)
    }

    fun deconstructMessage(
        message: ByteArray,
    ): List<ByteArray> {
        var pointer = 0
        var messages = MutableList(0) { byteArrayOf() }
        while(pointer < message.size) {
            val messageSize = message.copyOfRange(pointer, pointer + 8)
            val sizeBuffer = ByteBuffer.wrap(messageSize).order(ByteOrder.BIG_ENDIAN).getInt() .toUInt().toInt()
            pointer += 8
            val messagePart = message.copyOfRange(pointer, pointer + sizeBuffer)
            messages.add(messagePart)
            pointer += sizeBuffer
        }
        return messages
    }
}

