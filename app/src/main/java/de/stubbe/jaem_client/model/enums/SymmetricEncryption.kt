package de.stubbe.jaem_client.model.enums
import org.bouncycastle.crypto.agreement.X25519Agreement
import org.bouncycastle.crypto.generators.HKDFBytesGenerator
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters
import org.bouncycastle.crypto.params.HKDFParameters
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters
import org.bouncycastle.crypto.params.X25519PublicKeyParameters
import org.bouncycastle.crypto.signers.Ed25519Signer
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

enum class SymmetricEncryption(
    val algorithm: String,
    val code: Byte,
    val generateX25519Keys: (Ed25519PrivateKeyParameters) -> Pair<X25519PublicKeyParameters, X25519PrivateKeyParameters>,
    val generateSignatureKeys: () -> Pair<Ed25519PublicKeyParameters,Ed25519PrivateKeyParameters>,
    val generateSymmetricKey: (X25519PublicKeyParameters, X25519PrivateKeyParameters)-> ByteArray,
    val sign: (ByteArray,Ed25519PrivateKeyParameters) -> ByteArray,
    val checkSign: (ByteArray, ByteArray, Ed25519PublicKeyParameters) -> Boolean,
    val encrypt: (ByteArray, ByteArray, ByteArray) -> ByteArray,
    val decrypt: (ByteArray, ByteArray) -> ByteArray
) {
    ED25519(
        algorithm = "ED25519",
        code = 0,
        generateX25519Keys = {edPrivateKey ->
            val xPrivateKey = X25519PrivateKeyParameters(edPrivateKey.encoded)
            val xPublicKey = xPrivateKey.generatePublicKey()
            Pair(xPublicKey,xPrivateKey)
        },
        generateSignatureKeys = {
            val privateKey = Ed25519PrivateKeyParameters(SecureRandom())
            val publicKey = privateKey.generatePublicKey()

            Pair(publicKey, privateKey)
        },
        generateSymmetricKey = { publicKey, privateKey ->
            val agreement = X25519Agreement()
            val sharedSecret = ByteArray(32)
            agreement.init(privateKey)
            agreement.calculateAgreement(publicKey, sharedSecret,0)

            val hkdf = HKDFBytesGenerator(org.bouncycastle.crypto.digests.SHA256Digest())
            val salt = ByteArray(32)
            val info = "Ed25519 Key Exchange".toByteArray()
            hkdf.init(HKDFParameters(sharedSecret, salt, info))
            val aesKey = ByteArray(32)
            hkdf.generateBytes(aesKey, 0, aesKey.size)
            aesKey
        },
        sign = { message, privateKey ->
            val signer = Ed25519Signer()
            signer.init(true, privateKey)
            signer.update(message, 0 , message.size)
            val signature = signer.generateSignature()
            signature
        },
        checkSign = { message, signature,  publicKey ->
            val verifier = Ed25519Signer()
            verifier.init(false, publicKey)
            verifier.update(message, 0, message.size)
            verifier.verifySignature(signature)
        },
        encrypt = { message, signature, aesKey ->
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val iv = ByteArray(12)
            SecureRandom().nextBytes(iv) // Generate random IV

            val keySpec = SecretKeySpec(aesKey, "AES")
            val gcmSpec = GCMParameterSpec(128, iv)

            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec)

            var messageWithSignature = signature + message
            val ciphertext = cipher.doFinal(messageWithSignature)

            iv + ciphertext
        },
        decrypt = { message, aesKey ->
            val iv = message.copyOfRange(0, 12) // Extract IV
            val ciphertext = message.copyOfRange(12 , message.size) // Extract actual ciphertext

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val keySpec = SecretKeySpec(aesKey, "AES")
            val gcmSpec = GCMParameterSpec(128, iv)

            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec)
            val decryptedBytes = cipher.doFinal(ciphertext)

            decryptedBytes
        }
    )
}