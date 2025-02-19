package de.stubbe.jaem_client.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Formatierung eines LocalDateTime zu einem String im Format "dd.MM.yyyy".
 */
fun LocalDateTime.formatTime(): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return formatter.format(this)
}