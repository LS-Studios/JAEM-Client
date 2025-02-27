package de.stubbe.jaem_client.model.enums
import org.bouncycastle.crypto.AsymmetricCipherKeyPair
import org.bouncycastle.crypto.KeyGenerationParameters
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator
import org.bouncycastle.crypto.params.AsymmetricKeyParameter
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

enum class SymmetricEncryption(
    val algorithm: String,
    val generate: ()-> AsymmetricCipherKeyPair,
    val generateSymmetricKey: (AsymmetricKeyParameter, AsymmetricKeyParameter)-> ByteArray,
    val encrypt: (String, ByteArray) -> String,
    val decrypt: (String, ByteArray) -> String
) {
    AES(
        "AES",
        {
            val random = SecureRandom()
            val keyGen = Ed25519KeyPairGenerator()
            keyGen.init(KeyGenerationParameters(random, 256))
            keyGen.generateKeyPair()
        },
        { recipientPublicKey, privateKey ->
            val privateKeyByte = (privateKey as Ed25519PrivateKeyParameters).encoded
            val recipientPublicKeyByte = (recipientPublicKey as Ed25519PublicKeyParameters).encoded

            val sharedSecret = privateKeyByte + recipientPublicKeyByte
            val mac = Mac.getInstance("HmacSHA256")
            val salt = ByteArray(32) // Optional: Can be random or fixed
            val info = "Ed25519 Key Exchange".toByteArray()

            mac.init(SecretKeySpec(salt, "HmacSHA256"))
            mac.update(sharedSecret)
            val prk = mac.doFinal()

            mac.init(SecretKeySpec(prk, "HmacSHA256"))
            mac.update(info)
            val aesKey = mac.doFinal().copyOf(32)
            aesKey
        },
        { message, aesKey ->
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val iv = ByteArray(12)
            SecureRandom().nextBytes(iv) // Generate random IV

            val keySpec = SecretKeySpec(aesKey, "AES")
            val gcmSpec = GCMParameterSpec(128, iv)

            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec)
            val ciphertext = cipher.doFinal(message.toByteArray())

            Base64.getEncoder().encodeToString(iv + ciphertext)
        },
        { message, aesKey ->
            val decodedData = Base64.getDecoder().decode(message)

            val iv = decodedData.copyOfRange(0, 12) // Extract IV
            val ciphertext = decodedData.copyOfRange(12, decodedData.size) // Extract actual ciphertext

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val keySpec = SecretKeySpec(aesKey, "AES")
            val gcmSpec = GCMParameterSpec(128, iv)

            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec)
            val decryptedBytes = cipher.doFinal(ciphertext)

            String(decryptedBytes)
        }
    )
}