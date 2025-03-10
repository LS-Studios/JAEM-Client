package de.stubbe.jaem_client.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.stubbe.jaem_client.database.JAEMDatabase
import de.stubbe.jaem_client.database.daos.ChatDao
import de.stubbe.jaem_client.database.daos.EncryptionKeyDao
import de.stubbe.jaem_client.database.daos.MessageDao
import de.stubbe.jaem_client.database.daos.ProfileDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): JAEMDatabase {
        return Room.databaseBuilder(
            context,
            JAEMDatabase::class.java,
            "jaem_database"
        ).build()
    }

    @Provides
    fun provideChatDao(database: JAEMDatabase): ChatDao {
        return database.chatDao()
    }

    @Provides
    fun provideMessageDao(database: JAEMDatabase): MessageDao {
        return database.messageDao()
    }

    @Provides
    fun provideProfileDao(database: JAEMDatabase): ProfileDao {
        return database.profileDao()
    }

    @Provides
    fun provideEncryptionKeyDao(database: JAEMDatabase): EncryptionKeyDao {
        return database.encryptionKeyDao()
    }

}
