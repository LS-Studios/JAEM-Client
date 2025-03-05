package de.stubbe.jaem_client.model.network;

data class ReceiveBody (
    val algorithm: Byte,
    val signature: ByteArray,
    val publicKey: ByteArray,
    val unixTimeStamp: ULong,
)
