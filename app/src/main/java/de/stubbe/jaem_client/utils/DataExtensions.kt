package de.stubbe.jaem_client.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import androidx.datastore.dataStore
import de.stubbe.jaem_client.data.USER_PREFERENCES_NAME
import de.stubbe.jaem_client.data.UserPreferencesSerializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.math.roundToInt


/**
 * Initialisierung des DataStore für die UserPreferences.
 */
val Context.userPreferencesDataStore by dataStore(
    fileName = USER_PREFERENCES_NAME,
    serializer = UserPreferencesSerializer
)

/**
 * Konvertiert einen Long-Wert (Sekunden seit der Unix-Epoche) in ein LocalDateTime.
 */
fun Long.epochSecondToLocalDateTime(): LocalDateTime {
    return LocalDateTime.ofInstant(Instant.ofEpochSecond(this), ZoneId.systemDefault())
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

suspend fun Uri.toByteArray(context: Context): ByteArray? {
    val uri = this

    return withContext(Dispatchers.IO) {
        context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
    }
}

/**
 * Komprimiert ein Bild, das durch eine Uri repräsentiert wird.
 */
suspend fun Uri.compressImage(
    context: Context,
    compressionThreshold: Long
): ByteArray? {
    val uri = this

    return withContext(Dispatchers.IO) {
        val mimeType = context.contentResolver.getType(uri)

        val inputBytes = uri.toByteArray(context) ?: return@withContext null

        ensureActive()

        withContext(Dispatchers.Default) {
            val bitmap =
                BitmapFactory.decodeByteArray(inputBytes, 0, inputBytes.size)

            ensureActive()

            val compressFormat = when (mimeType) {
                "image/jpeg" -> Bitmap.CompressFormat.JPEG
                "image/png" -> Bitmap.CompressFormat.PNG
                "image/webp" -> Bitmap.CompressFormat.WEBP_LOSSLESS
                else -> Bitmap.CompressFormat.JPEG
            }

            var outputBytes: ByteArray
            var quality = 100

            do {
                ByteArrayOutputStream().use { outputStream ->
                    bitmap.compress(compressFormat, quality, outputStream)
                    outputBytes = outputStream.toByteArray()
                    quality -= (quality * 0.1).roundToInt()
                }
            } while (isActive &&
                outputBytes.size > compressionThreshold &&
                quality > 5 &&
                compressFormat != Bitmap.CompressFormat.PNG
            )

            outputBytes
        }
    }
}

suspend fun Uri.getFileName(context: Context): String? {
    val uri = this
    return withContext(Dispatchers.IO) {
        val contentResolver = context.contentResolver

        val cursor = contentResolver.query(uri, null, null, null, null)
        var fileName: String? = null

        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }

        fileName
    }
}