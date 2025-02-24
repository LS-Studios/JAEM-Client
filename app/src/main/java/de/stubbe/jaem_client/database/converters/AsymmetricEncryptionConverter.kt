package de.stubbe.jaem_client.database.converters

import androidx.room.TypeConverter
import de.stubbe.jaem_client.model.enums.AsymmetricEncryption

class AsymmetricEncryptionConverter {

    @TypeConverter
    fun fromString(value: String): AsymmetricEncryption {
        return AsymmetricEncryption.valueOf(value)
    }
    
    @TypeConverter
    fun toString(value: AsymmetricEncryption): String {
        return value.name
    }

}