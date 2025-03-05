package de.stubbe.jaem_client.utils

import de.stubbe.jaem_client.model.enums.AsymmetricEncryption
import de.stubbe.jaem_client.network.Message
import de.stubbe.jaem_client.network.NetworkMessageModel
import de.stubbe.jaem_client.network.SendNetworkMessageModel
import de.stubbe.jaem_client.network.toByteArray
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.time.Instant

class MessageBuilder(var encryptionData: ChatEncryptionData){
    var messageType: UShort = 0u
    var messages: ByteArray = byteArrayOf()

    fun setupCommunication(encryptionData: ChatEncryptionData) = apply {
        this.encryptionData = encryptionData
    }

    fun setMessageType(type: UShort) = apply {
        messageType = type
    }

    fun addMessage(messageType: UShort, message: ByteArray) = apply {
        messages += (ByteBuffer.allocate(2).putShort(messageType.toShort()).flip() as ByteBuffer).toString().toByteArray() + message.size.toULong().toString().toByteArray() + message
    }

    fun build(): SendNetworkMessageModel {
        return constructMessage()
    }

    private fun constructMessage(): SendNetworkMessageModel{
        val client = encryptionData.client!!
        val otherClient = encryptionData.otherClient!!
        val algorithm = encryptionData.encryption

        val messageWithPublicKey = client.ed25519PublicKey!!.encoded + messages
        val signature = algorithm.sign(messageWithPublicKey, client.ed25519PrivateKey!!)
        val aesKey = algorithm.generateSymmetricKey(otherClient.x25519PublicKey!!, client.x25519PrivateKey!!)

        var request =  SendNetworkMessageModel(client.encryption.code, otherClient.ed25519PublicKey!!.encoded, byteArrayOf())

        var message = getCurrentUnixTimestamp().toString().toByteArray() +
                splitRequestIntoMessages(messages).map { it.toByteArray() }.reduce { acc, bytes -> acc + bytes }
        var aesEncryptedMessage = algorithm.encrypt(message, signature, aesKey)

        val rsaEncryptedData = AsymmetricEncryption.RSA.encrypt(client.ed25519PublicKey!!.encoded + aesEncryptedMessage, otherClient.rsaPublicKey!!)
        request.rsaEncryptedMessage = rsaEncryptedData

        return request
    }

    fun deconstructMessage(message: ByteArray){
        val client = encryptionData.client!!
        val otherClient = encryptionData.otherClient!!
        val algorithm = encryptionData.encryption

        val rsaDecryptedMessage = AsymmetricEncryption.RSA.decrypt(message, client.rsaPrivateKey!!)
        val senderSignatureKey = rsaDecryptedMessage.copyOfRange(0, 32)
        val aesEncryptedMessage = rsaDecryptedMessage.copyOfRange(32, rsaDecryptedMessage.size)

        val aesKey = algorithm.generateSymmetricKey(otherClient.x25519PublicKey!!, client.x25519PrivateKey!!)
        val aesDecryptedMessage = algorithm.decrypt(aesEncryptedMessage, aesKey)

        val signature = aesDecryptedMessage.copyOfRange(0,64)
        val messageType = aesDecryptedMessage.copyOfRange(64, 66)
        val message = aesEncryptedMessage.copyOfRange(66, aesDecryptedMessage.size)


    }

    private fun splitRequestIntoMessages(message: ByteArray): List<Message> {
        var pointer = 0
        val messages = mutableListOf<Message>()
        while (pointer < message.size) {
            val messageType = message.copyOfRange(pointer, pointer + 2)
            pointer += 2
            val messageSize = message.copyOfRange(pointer, pointer + 8)
            pointer += 8
            val sizeBuffer = ByteBuffer.wrap(messageSize).order(ByteOrder.BIG_ENDIAN).getInt().toUInt().toInt()
            val messagePart = message.copyOfRange(pointer, pointer + sizeBuffer)
            messages.add(Message(messageType.toUShort(), sizeBuffer.toUInt(), messagePart))
            pointer += sizeBuffer
        }
        return messages
    }

    private fun getCurrentUnixTimestamp(): ULong {
        val currentTime = Instant.now().epochSecond
        return currentTime.toULong()
    }

    fun ByteArray.toUShort(): UShort {
        return ByteBuffer.wrap(this).order(ByteOrder.BIG_ENDIAN).short.toUShort()
    }
}