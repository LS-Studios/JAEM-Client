package de.stubbe.jaem_client.database.converters

import androidx.room.TypeConverter
import de.stubbe.jaem_client.model.enums.SymmetricEncryption

class SymmetricEncryptionConverter {

    @TypeConverter
    fun fromString(value: String): SymmetricEncryption {
        return SymmetricEncryption.valueOf(value)
    }

    @TypeConverter
    fun toString(value: SymmetricEncryption): String {
        return value.name
    }

}