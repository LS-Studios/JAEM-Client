package de.stubbe.jaem_client.utils

import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters
import org.bouncycastle.crypto.params.X25519PublicKeyParameters
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec


fun ByteArray.toRSAPublicKey(): PublicKey {
    val spec = X509EncodedKeySpec(this)
    val keyFactory = KeyFactory.getInstance("RSA")
    return keyFactory.generatePublic(spec)
}

fun ByteArray.toRSAPrivateKey(): PrivateKey {
    val spec = PKCS8EncodedKeySpec(this)
    val keyFactory = KeyFactory.getInstance("RSA")
    return keyFactory.generatePrivate(spec)
}

fun ByteArray.toEd25519PublicKey(): Ed25519PublicKeyParameters {
    return Ed25519PublicKeyParameters(this, 0)
}

fun ByteArray.toEd25519PrivateKey(): Ed25519PrivateKeyParameters {
    return Ed25519PrivateKeyParameters(this, 0)
}

fun ByteArray.toX25519PublicKey(): X25519PublicKeyParameters {
    return X25519PublicKeyParameters(this, 0)
}

fun ByteArray.toX25519PrivateKey(): X25519PrivateKeyParameters {
    return X25519PrivateKeyParameters(this, 0)
}

fun Short.toByteArray(): ByteArray {
    return ByteBuffer.allocate(Short.SIZE_BYTES)
        .order(ByteOrder.BIG_ENDIAN)
        .putShort(this)
        .array()
}

fun Long.toByteArray(): ByteArray {
    return ByteBuffer.allocate(ULong.SIZE_BYTES)
        .order(ByteOrder.BIG_ENDIAN)
        .putLong(this)
        .array()
}

fun Int.toByteArray(): ByteArray {
    return ByteBuffer.allocate(Int.SIZE_BYTES)
        .order(ByteOrder.BIG_ENDIAN)
        .putInt(this)
        .array()
}

fun ByteArray.toShort(): Short {
    return ByteBuffer.wrap(this).order(ByteOrder.BIG_ENDIAN).getShort()
}

fun ByteArray.toInt(): Int {
    return ByteBuffer.wrap(this).order(ByteOrder.BIG_ENDIAN).getInt()
}

fun ByteArray.toLong(): Long {
    return ByteBuffer.wrap(this).order(ByteOrder.BIG_ENDIAN).getLong()
}