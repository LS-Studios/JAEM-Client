package de.stubbe.jaem_client.model.enums
import android.annotation.SuppressLint
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Security
import javax.crypto.Cipher

enum class AsymmetricEncryption(
    val algorithm: String,
    val generate: () -> KeyPair,
    val encrypt: (ByteArray, PublicKey) -> ByteArray,
    val decrypt: (ByteArray, PrivateKey) -> ByteArray
) {
    @SuppressLint("DeprecatedProvider")
    RSA(
        "RSA",
        {
            Security.addProvider(BouncyCastleProvider())
            val keyPairGen = KeyPairGenerator.getInstance("RSA", "BC")
            keyPairGen.initialize(2048)
            keyPairGen.generateKeyPair()
        },
        { message, recipientPublicKey ->
            Security.addProvider(BouncyCastleProvider())
            val cipher = Cipher.getInstance("RSA", "BC")
            cipher.init(Cipher.ENCRYPT_MODE, recipientPublicKey)
            val cipherText = cipher.doFinal(message)
            cipherText
        },
        { message, privateKey ->
            Security.addProvider(BouncyCastleProvider())
            val cipher = Cipher.getInstance("RSA", "BC")
            cipher.init(Cipher.DECRYPT_MODE, privateKey)
            val decryptedText = cipher.doFinal(message)
            decryptedText
        }
    )
}



