package de.stubbe.jaem_client.model.encryption

import kotlinx.serialization.Serializable
import org.bouncycastle.crypto.agreement.X25519Agreement
import org.bouncycastle.crypto.digests.SHA256Digest
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

@Serializable
sealed class SymmetricEncryption(
    val algorithm: String,
    val code: Byte
) {

    companion object {
        fun fromAlgorithm(algorithm: String): SymmetricEncryption {
            return when (algorithm) {
                ED25519.algorithm -> ED25519
                else -> throw IllegalArgumentException("Unknown algorithm: $algorithm")
            }
        }
    }

    @Serializable
    /** ED25519 + X25519 hybrid encryption */
    data object ED25519 : SymmetricEncryption("ED25519", 0) {

        fun generateX25519Keys(edPrivateKey: Ed25519PrivateKeyParameters): Pair<X25519PublicKeyParameters, X25519PrivateKeyParameters> {
            val xPrivateKey = X25519PrivateKeyParameters(edPrivateKey.encoded)
            val xPublicKey = xPrivateKey.generatePublicKey()
            return Pair(xPublicKey, xPrivateKey)
        }
        fun generateSignatureKeys(): Pair<Ed25519PublicKeyParameters, Ed25519PrivateKeyParameters> {
            val privateKey = Ed25519PrivateKeyParameters(SecureRandom())
            val publicKey = privateKey.generatePublicKey()
            return Pair(publicKey, privateKey)
        }
        fun generateSymmetricKey(publicKey: X25519PublicKeyParameters, privateKey: X25519PrivateKeyParameters): ByteArray {
            val agreement = X25519Agreement()
            val sharedSecret = ByteArray(32)
            agreement.init(privateKey)
            agreement.calculateAgreement(publicKey, sharedSecret, 0)

            val hkdf = HKDFBytesGenerator(SHA256Digest())
            val salt = ByteArray(32)
            val info = "Ed25519 Key Exchange".toByteArray()
            hkdf.init(HKDFParameters(sharedSecret, salt, info))
            val aesKey = ByteArray(32)
            hkdf.generateBytes(aesKey, 0, aesKey.size)
            return aesKey
        }
        fun sign(message: ByteArray, privateKey: Ed25519PrivateKeyParameters): ByteArray {
            val signer = Ed25519Signer()
            signer.init(true, privateKey)
            signer.update(message, 0, message.size)
            return signer.generateSignature()
        }
        fun checkSign(signature: ByteArray, message: ByteArray, publicKey: Ed25519PublicKeyParameters): Boolean {
            val verifier = Ed25519Signer()
            verifier.init(false, publicKey)
            verifier.update(message, 0, message.size)
            return verifier.verifySignature(signature)
        }
        fun encrypt(message: ByteArray, aesKey: ByteArray): ByteArray {
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val iv = ByteArray(12)
            SecureRandom().nextBytes(iv) // Generate random IV

            val keySpec = SecretKeySpec(aesKey, "AES")
            val gcmSpec = GCMParameterSpec(128, iv)

            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec)

            val messageWithSignature = message
            val ciphertext = cipher.doFinal(messageWithSignature)

            return iv + ciphertext
        }
        fun encrypt(signature: ByteArray, message: ByteArray, aesKey: ByteArray): ByteArray {
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val iv = ByteArray(12)
            SecureRandom().nextBytes(iv) // Generate random IV

            val keySpec = SecretKeySpec(aesKey, "AES")
            val gcmSpec = GCMParameterSpec(128, iv)

            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec)

            val messageWithSignature = signature + message
            val ciphertext = cipher.doFinal(messageWithSignature)

            return iv + ciphertext
        }
        fun decrypt(message: ByteArray, aesKey: ByteArray): ByteArray {
            val iv = message.copyOfRange(0, 12) // Extract IV
            val ciphertext = message.copyOfRange(12, message.size) // Extract actual ciphertext

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val keySpec = SecretKeySpec(aesKey, "AES")
            val gcmSpec = GCMParameterSpec(128, iv)

            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec)
            return cipher.doFinal(ciphertext)
        }

    }

}