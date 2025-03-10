package de.stubbe.jaem_client.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.stubbe.jaem_client.database.converters.AttachmentConverter
import de.stubbe.jaem_client.database.converters.PublicKeyListConverter
import de.stubbe.jaem_client.database.daos.ChatDao
import de.stubbe.jaem_client.database.daos.EncryptionKeyDao
import de.stubbe.jaem_client.database.daos.MessageDao
import de.stubbe.jaem_client.database.daos.ProfileDao
import de.stubbe.jaem_client.database.daos.UDSUserDao
import de.stubbe.jaem_client.database.entries.ChatEntity
import de.stubbe.jaem_client.database.entries.EncryptionKeyEntity
import de.stubbe.jaem_client.database.entries.MessageEntity
import de.stubbe.jaem_client.database.entries.ProfileEntity
import de.stubbe.jaem_client.database.entries.UDSUserEntity

/**
 * Datenbankklasse für die JAEM Datenbank
 */
@Database(
    entities = [
        ChatEntity::class,
        MessageEntity::class,
        ProfileEntity::class,
        EncryptionKeyEntity::class,
        UDSUserEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    AttachmentConverter::class,
    PublicKeyListConverter::class
)
abstract class JAEMDatabase : RoomDatabase() {

    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
    abstract fun profileDao(): ProfileDao
    abstract fun encryptionKeyDao(): EncryptionKeyDao
    abstract fun udsUserDao(): UDSUserDao

}
