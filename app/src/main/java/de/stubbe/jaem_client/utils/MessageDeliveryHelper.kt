package de.stubbe.jaem_client.utils

import ED25519Client
import de.stubbe.jaem_client.model.enums.AsymmetricEncryption
import de.stubbe.jaem_client.model.enums.SymmetricEncryption
import okhttp3.RequestBody
import java.nio.ByteBuffer
import java.nio.ByteOrder

 class MessageDeliveryHelper(
     var client: ED25519Client,
     var otherClient: ED25519Client)
  {
    fun constructMessage(
        algorithm: SymmetricEncryption,
        message: ByteArray,
        messageType: UShort,
    ): ByteArray {
        val messageWithPublicKey =  client.ed25519PublicKey!!.encoded + message
        val signature = algorithm.sign(messageWithPublicKey, client.ed25519PrivateKey!!)
        val aesKey = algorithm.generateSymmetricKey(otherClient.x25519PublicKey!!, client.x25519PrivateKey!!)
        val aesEncrypted = algorithm.encrypt(
            (ByteBuffer.allocate(2).putShort(messageType.toShort()).flip() as ByteBuffer).toString().toByteArray() + message, signature, aesKey)
        val aesEncryptedWithEDPubKey = client.ed25519PublicKey!!.encoded + aesEncrypted

        val rsaKeysRecipient = AsymmetricEncryption.RSA.generate()
        val rsaEncrypted = AsymmetricEncryption.RSA.encrypt(aesEncryptedWithEDPubKey, rsaKeysRecipient.public)

        val finalMessage = byteArrayOf(client.encryption.code) + otherClient.ed25519PublicKey!!.encoded + rsaKeysRecipient.public.encoded + rsaEncrypted

        return finalMessage
    }

    fun getMessagesBody(
        algorithm: SymmetricEncryption,
    ): RequestBody {
        val encCode = algorithm.code
        val unixTime = System.currentTimeMillis().toString().toByteArray()
        val signature = algorithm.sign(unixTime, client.ed25519PrivateKey!!)
        val message = byteArrayOf(encCode) + signature + client.ed25519PublicKey!!.encoded + unixTime
        return RequestBody.create(null, message)
    }




    fun deconstructMessage(encryption: SymmetricEncryption, message: ByteArray, client: ED25519Client, otherClient: ED25519Client){


        val rsaDecryptedMessage = AsymmetricEncryption.RSA.decrypt(message, client.rsaPrivateKey!!)
        val recipientSignatureKey = rsaDecryptedMessage.copyOfRange(0, 32)
        val recipientRSAKey = rsaDecryptedMessage.copyOfRange(32, 96)
        val aesEncryptedMessage = rsaDecryptedMessage.copyOfRange(96, rsaDecryptedMessage.size)

        val aesKey = encryption.generateSymmetricKey(otherClient.x25519PublicKey!!, client.x25519PrivateKey!!)
        val aesDecryptedMessage = encryption.decrypt(aesEncryptedMessage, aesKey)
        val signature = aesDecryptedMessage.copyOfRange(0, 64)
        val
    }
}

