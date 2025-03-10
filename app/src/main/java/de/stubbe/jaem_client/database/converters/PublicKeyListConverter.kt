package de.stubbe.jaem_client.database.converters

import androidx.room.TypeConverter
import de.stubbe.jaem_client.database.entries.PublicKeyEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class PublicKeyListConverter {

    @TypeConverter
    fun fromString(value: String): List<PublicKeyEntity> {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun toString(publicKeys: List<PublicKeyEntity>): String {
        return Json.encodeToString(publicKeys)
    }

}