package de.stubbe.jaem_client.model.enums
import android.annotation.SuppressLint
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Key
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.Security
import javax.crypto.Cipher

enum class AsymmetricEncryption(
    val algorithm: String,
    val generate: () -> KeyPair,
    val encrypt: (ByteArray, Key) -> ByteArray,
    val decrypt: (ByteArray, Key) -> ByteArray
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
            val cipherText = cipher.doFinal(message)
            cipherText
        },
        { message, privateKey ->
            val cipher = Cipher.getInstance("RSA/ECB/RKCS1Padding", "BC")
            cipher.init(Cipher.DECRYPT_MODE, privateKey)
            val decryptedText = cipher.doFinal(message)
            decryptedText
        }
    )
}



