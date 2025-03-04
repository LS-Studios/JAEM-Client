package de.stubbe.jaem_client.model.enums
import android.annotation.SuppressLint
import java.security.KeyPairGenerator
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Key
import java.security.KeyPair
import java.security.Security
import java.util.Base64
import java.util.Random
import javax.crypto.Cipher

enum class AsymmetricEncryption(
    val algorithm: String,
    val generate: () -> KeyPair,
    val encrypt: (String, Key) -> String,
    val decrypt: (String, Key) -> String
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
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC")
            cipher.init(Cipher.ENCRYPT_MODE, recipientPublicKey)
            val cipherText = cipher.doFinal(message.toByteArray())
            cipherText.toString()
        },
        { message, privateKey ->
            val cipher = Cipher.getInstance("RSA/ECB/RKCS1Padding", "BC")
            cipher.init(Cipher.DECRYPT_MODE, privateKey)
            val decryptedText = String(cipher.doFinal(message.toByteArray()))
            decryptedText
        }
    )
}



