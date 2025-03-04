package de.stubbe.jaem_client.utils

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object AppStorageHelper {

    suspend fun saveUriToSharedStorage(uri: Uri, context: Context): File? {
        with(Dispatchers.IO) {
            val contentResolver = context.contentResolver

            val cursor = contentResolver.query(uri, null, null, null, null)
            val fileName = cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) it.getString(nameIndex) else null
                } else null
            } ?: return null

            val mimeType = contentResolver.getType(uri) ?: return null

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

            return try {
                val newUri = contentResolver.insert(collection, contentValues) ?: return null
                contentResolver.openOutputStream(newUri)?.use { outputStream ->
                    contentResolver.openInputStream(uri)?.use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                newUri.path?.let { File(it) }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun copyUriToAppStorage(uri: Uri, context: Context): File? {
        with(Dispatchers.IO) {
            val contentResolver = context.contentResolver

            // Datei-Details abrufen (Name und Erweiterung)
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

            val mimeType = contentResolver.getType(uri) ?: return null

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

            if (fileName == null) return null

            return try {
                val inputStream: InputStream? = contentResolver.openInputStream(uri)
                inputStream?.use { stream ->
                    val outputFile = File(fileDir, fileName!!)
                    FileOutputStream(outputFile).use { output ->
                        stream.copyTo(output)
                    }
                    outputFile
                }
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