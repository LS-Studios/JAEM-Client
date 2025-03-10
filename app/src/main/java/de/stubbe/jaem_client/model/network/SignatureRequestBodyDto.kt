package de.stubbe.jaem_client.model.network

import de.stubbe.jaem_client.model.ED25519Client
import de.stubbe.jaem_client.utils.getUnixTime
import de.stubbe.jaem_client.utils.toByteArray
import java.nio.ByteBuffer

/**
 * Repräsentiert eine Anfrage zum verifizieren der Identität an den Server
 */
class SignatureRequestBodyDto(client: ED25519Client) {
    private var algorithm: Byte = 0
    private var signature: ByteArray = byteArrayOf()
    private var publicKey: ByteArray = byteArrayOf()
    private var timestamp: Long = 0

    fun toByteArray(): ByteArray = ByteBuffer.allocate(1 + signature.size + publicKey.size + 8)
        .put(algorithm)
        .put(signature)
        .put(publicKey)
        .putLong(timestamp)
        .array()

    init {
        val timestamp = getUnixTime()
        val signature = client.encryption.sign(client.ed25519PublicKey!!.encoded + timestamp.toByteArray(), client.ed25519PrivateKey!!)
        this.algorithm = client.encryption.code
        this.signature = signature
        this.publicKey = client.ed25519PublicKey!!.encoded
        this.timestamp = timestamp
    }
}
