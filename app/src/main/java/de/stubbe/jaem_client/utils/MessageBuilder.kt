package de.stubbe.jaem_client.utils

import de.stubbe.jaem_client.model.enums.AsymmetricEncryption
import de.stubbe.jaem_client.network.SendNetworkMessageModel
import java.nio.ByteBuffer
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

        val request =  SendNetworkMessageModel(client.encryption.code, otherClient.ed25519PublicKey!!.encoded, byteArrayOf())

        val message = getCurrentUnixTimestamp().toString().toByteArray() + messages
        val aesEncryptedMessage = algorithm.encrypt(message, signature, aesKey)

        val rsaEncryptedData = AsymmetricEncryption.RSA.encrypt(client.ed25519PublicKey!!.encoded + aesEncryptedMessage, otherClient.rsaPublicKey!!)
        request.rsaEncryptedMessage = rsaEncryptedData

        return request
    }

    private fun getCurrentUnixTimestamp(): ULong {
        val currentTime = Instant.now().epochSecond
        return currentTime.toULong()
    }
}