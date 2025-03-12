package de.stubbe.jaem_client.utils

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import de.stubbe.jaem_client.data.SEPARATOR_BYTE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.tika.Tika
import java.io.File
import java.io.FileOutputStream

object AppStorageHelper {

    suspend fun createFileFromBytesInSharedStorage(nameAndContentBytes: ByteArray, context: Context): File? {
        val tika = Tika()

        val separatorIndex = nameAndContentBytes.indexOf(SEPARATOR_BYTE)
        val fileName = String(nameAndContentBytes.copyOfRange(0, separatorIndex)).trim()

        val mimeType = tika.detect(nameAndContentBytes)

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        }

        val collection = when {
            mimeType.startsWith("image/") -> {
                contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Jaem Images")
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

            mimeType.startsWith("video/") -> {
                contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, "Jaem Videos")
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }

            mimeType.startsWith("audio/") -> {
                contentValues.put(MediaStore.Audio.Media.RELATIVE_PATH, "Jaem Audio")
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }

            else -> {
                contentValues.put(MediaStore.Downloads.RELATIVE_PATH, "Jaem Downloads")
                MediaStore.Downloads.EXTERNAL_CONTENT_URI
            }
        }

        val contentBytes = nameAndContentBytes.copyOfRange(separatorIndex, nameAndContentBytes.size)

        return try {
            val uri = context.contentResolver.insert(collection, contentValues) ?: return null
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(contentBytes)
            }
            File(uri.path!!)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun copyUriToAppStorage(nameAndContentBytes: ByteArray, context: Context): File? {
        val tika = Tika()

        val separatorIndex = nameAndContentBytes.indexOf(SEPARATOR_BYTE)
        val fileName = String(nameAndContentBytes.copyOfRange(0, separatorIndex)).trim()

        val mimeType = tika.detect(nameAndContentBytes)

        val fileDir = when {
            mimeType.startsWith("image/") -> "${context.getExternalFilesDir(null)}${File.separator}Images"
            mimeType.startsWith("video/") -> "${context.getExternalFilesDir(null)}${File.separator}Videos"
            mimeType.startsWith("audio/") -> "${context.getExternalFilesDir(null)}${File.separator}Audio"
            else -> "${context.getExternalFilesDir(null)}${File.separator}Downloads"
        }

        val dir = File(fileDir)
        if (!dir.exists()) {
            dir.mkdirs()
        }

        val contentBytes = nameAndContentBytes.copyOfRange(separatorIndex, nameAndContentBytes.size)

        return withContext(Dispatchers.IO) {
            try {
                val outputFile = File(fileDir, fileName)
                FileOutputStream(outputFile).use { output ->
                    output.write(contentBytes)
                }
                outputFile
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    fun uriToFile(uri: Uri, context: Context): File? {
        val contentResolver = context.contentResolver
        var filePath: String? = null

        val projection = arrayOf(MediaStore.MediaColumns.DATA)
        val cursor: Cursor? = contentResolver.query(uri, projection, null, null, null)

        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                filePath = it.getString(columnIndex)
            }
        }

        return filePath?.let { File(it) }
    }

}