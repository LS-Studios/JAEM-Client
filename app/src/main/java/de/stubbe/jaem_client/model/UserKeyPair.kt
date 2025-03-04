package de.stubbe.jaem_client.model

import PublicKey

data class DeviceKeyPair(
    val edPublicKey: PublicKey,
    val x25519PublicKey: PublicKey,
    val edPrivateKey: ByteArray,
    val x25519PrivateKey: ByteArray,
    val rsaPublicKey: PublicKey,
    val rsaPrivateKey: ByteArray
)
