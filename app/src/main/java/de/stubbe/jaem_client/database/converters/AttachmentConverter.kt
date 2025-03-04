package de.stubbe.jaem_client.database.converters

import androidx.room.TypeConverter
import de.stubbe.jaem_client.model.Attachments
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AttachmentConverter {

    @TypeConverter
    fun fromString(value: String): Attachments {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun toString(attachments: Attachments): String {
        return Json.encodeToString(attachments)
    }

}