package de.stubbe.jaem_client.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Base64

/**
 * Formatierung eines LocalDateTime zu einem String im Format "dd.MM.yyyy".
 */
fun LocalDateTime.formatTime(): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return formatter.format(this)
}

fun ByteArray.toBase64String(): String {
    return Base64.getEncoder().encodeToString(this)
}

fun String.base64StringToByteArray(): ByteArray {
    return Base64.getDecoder().decode(this)
}