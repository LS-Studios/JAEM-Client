package de.stubbe.jaem_client.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.stubbe.jaem_client.database.converters.AttachmentConverter
import de.stubbe.jaem_client.database.daos.ChatDao
import de.stubbe.jaem_client.database.daos.ChatRequestDao
import de.stubbe.jaem_client.database.daos.EncryptionKeyDao
import de.stubbe.jaem_client.database.daos.MessageDao
import de.stubbe.jaem_client.database.daos.ProfileDao
import de.stubbe.jaem_client.database.entries.ChatModel
import de.stubbe.jaem_client.database.entries.ChatRequestModel
import de.stubbe.jaem_client.database.entries.EncryptionKeyModel
import de.stubbe.jaem_client.database.entries.MessageModel
import de.stubbe.jaem_client.database.entries.ProfileModel

/**
 * Datenbankklasse für die JAEM Datenbank
 */
@Database(
    entities = [
        ChatModel::class,
        MessageModel::class,
        ProfileModel::class,
        EncryptionKeyModel::class,
        ChatRequestModel::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    AttachmentConverter::class
)
abstract class JAEMDatabase : RoomDatabase() {

    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
    abstract fun profileDao(): ProfileDao
    abstract fun encryptionKeyDao(): EncryptionKeyDao
    abstract fun chatRequestDao(): ChatRequestDao

}
