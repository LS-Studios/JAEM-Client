package de.stubbe.jaem_client.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.stubbe.jaem_client.database.converters.AsymmetricEncryptionConverter
import de.stubbe.jaem_client.database.converters.SymmetricEncryptionConverter
import de.stubbe.jaem_client.database.daos.AsymmetricKeyPairDao
import de.stubbe.jaem_client.database.daos.ChatDao
import de.stubbe.jaem_client.database.daos.MessageDao
import de.stubbe.jaem_client.database.daos.ProfileDao
import de.stubbe.jaem_client.database.daos.SymmetricKeyDao
import de.stubbe.jaem_client.database.entries.AsymmetricKeyPairModel
import de.stubbe.jaem_client.database.entries.ChatModel
import de.stubbe.jaem_client.database.entries.MessageModel
import de.stubbe.jaem_client.database.entries.ProfileModel
import de.stubbe.jaem_client.database.entries.SymmetricKeyModel

/**
 * Datenbankklasse für die JAEM Datenbank
 */
@Database(
    entities = [
        ChatModel::class,
        MessageModel::class,
        ProfileModel::class,
        AsymmetricKeyPairModel::class,
        SymmetricKeyModel::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    SymmetricEncryptionConverter::class,
    AsymmetricEncryptionConverter::class
)
abstract class JAEMDatabase : RoomDatabase() {

    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
    abstract fun profileDao(): ProfileDao
    abstract fun symmetricKeyDao(): SymmetricKeyDao
    abstract fun asymmetricKeyPairDao(): AsymmetricKeyPairDao

    companion object {
        @Volatile
        private var Instance: JAEMDatabase? = null

        fun getDatabase(context: Context): JAEMDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, JAEMDatabase::class.java, "jaem_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
