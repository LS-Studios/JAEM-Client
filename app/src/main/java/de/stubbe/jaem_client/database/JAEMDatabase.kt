package de.stubbe.jaem_client.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import de.stubbe.jaem_client.database.converters.DBIntegerListConverter
import de.stubbe.jaem_client.database.daos.ChatDao
import de.stubbe.jaem_client.database.daos.MessageDao
import de.stubbe.jaem_client.database.daos.ProfileDao
import de.stubbe.jaem_client.database.entries.ChatModel
import de.stubbe.jaem_client.database.entries.MessageModel
import de.stubbe.jaem_client.database.entries.ProfileModel

@Database(
    entities = [
        ChatModel::class,
        MessageModel::class,
        ProfileModel::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    DBIntegerListConverter::class,
)
abstract class JAEMDatabase : RoomDatabase() {

    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
    abstract fun profileDao(): ProfileDao

    companion object {
        @Volatile
        private var Instance: JAEMDatabase? = null

        fun getDatabase(context: Context): JAEMDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, JAEMDatabase::class.java, "jaem-database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
