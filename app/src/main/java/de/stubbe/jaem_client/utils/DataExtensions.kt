package de.stubbe.jaem_client.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.datastore.dataStore
import de.stubbe.jaem_client.data.USER_PREFERENCES_NAME
import de.stubbe.jaem_client.data.UserPreferencesSerializer
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneOffset


/**
 * Initialisierung des DataStore für die UserPreferences.
 */
val Context.userPreferencesDataStore by dataStore(
    fileName = USER_PREFERENCES_NAME,
    serializer = UserPreferencesSerializer
)

/**
 * Umwandlung eines Long Wertes in ein LocalDateTime.
 */
fun Long.toLocalTime(): LocalDateTime {
    return LocalDateTime.ofEpochSecond(this, 0, ZoneOffset.UTC)
}

/**
 * Umwandlung eines LocalDateTime in einen Long Wert.
 */
fun LocalDateTime.toLong(): Long {
    return this.toEpochSecond(ZoneOffset.UTC)
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

