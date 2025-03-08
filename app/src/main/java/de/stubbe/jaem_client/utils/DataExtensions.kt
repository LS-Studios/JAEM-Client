package de.stubbe.jaem_client.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.datastore.dataStore
import de.stubbe.jaem_client.data.USER_PREFERENCES_NAME
import de.stubbe.jaem_client.data.UserPreferencesSerializer
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset


/**
 * Initialisierung des DataStore für die UserPreferences.
 */
val Context.userPreferencesDataStore by dataStore(
    fileName = USER_PREFERENCES_NAME,
    serializer = UserPreferencesSerializer
)

/**
 * Konvertiert einen Long-Wert (Millisekunden seit der Unix-Epoche) in ein LocalDateTime.
 */
fun Long.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())
}

/**
 * Konvertiert ein LocalDateTime in einen Long-Wert (Millisekunden seit der Unix-Epoche).
 */
fun LocalDateTime.toEpochSeconds(): Long {
    return this.toInstant(ZoneOffset.UTC).toEpochMilli() / 1000
}

/**
 * Umwandlung eines ByteArrays in ein Bitmap.
 */
fun ByteArray.toBitmap(): Bitmap {
    return BitmapFactory.decodeByteArray(this, 0, this.size)
}

/**
 * Umwandlung eines Bitmaps in ein ByteArray.
 */
fun Bitmap.toByteArray(): ByteArray {
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}

/**
 * Umwandlung einer Datei in ein Bitmap.
 */
fun File.toBitmap(): Bitmap? {
    val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    return BitmapFactory.decodeFile(this.absolutePath, options).takeIf { options.outWidth != -1 && options.outHeight != -1 }
}

/**
 * Darstellung der Dateigröße als String.
 */
fun Long.toSizeString(): String {
    val kb = this / 1024
    val mb = kb / 1024
    val gb = mb / 1024
    return when {
        gb > 0 -> "${gb}GB"
        mb > 0 -> "${mb}MB"
        else -> "${kb}KB"
    }
}

fun getUnixTime(): Long {
    return Instant.now().epochSecond
}