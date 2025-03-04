package de.stubbe.jaem_client.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.stubbe.jaem_client.database.daos.AsymmetricKeyPairDao
import de.stubbe.jaem_client.database.daos.ChatDao
import de.stubbe.jaem_client.database.daos.ChatRequestDao
import de.stubbe.jaem_client.database.daos.MessageDao
import de.stubbe.jaem_client.database.daos.ProfileDao
import de.stubbe.jaem_client.database.daos.SymmetricKeyDao
import de.stubbe.jaem_client.repositories.UserPreferencesRepository
import de.stubbe.jaem_client.repositories.database.AsymmetricKeyPairRepository
import de.stubbe.jaem_client.repositories.database.ChatRepository
import de.stubbe.jaem_client.repositories.database.ChatRequestRepository
import de.stubbe.jaem_client.repositories.database.MessageRepository
import de.stubbe.jaem_client.repositories.database.ProfileRepository
import de.stubbe.jaem_client.repositories.database.SymmetricKeyRepository
import de.stubbe.jaem_client.utils.userPreferencesDataStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideChatRepository(chatDao: ChatDao): ChatRepository {
        return ChatRepository(chatDao)
    }

    @Provides
    @Singleton
    fun provideMessageRepository(messageDao: MessageDao): MessageRepository {
        return MessageRepository(messageDao)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(profileDao: ProfileDao): ProfileRepository {
        return ProfileRepository(profileDao)
    }

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(@ApplicationContext context: Context): UserPreferencesRepository {
        return UserPreferencesRepository(context.userPreferencesDataStore)
    }

    @Provides
    @Singleton
    fun provideAsymmetricKeyPairRepository(asymmetricKeyPairDao: AsymmetricKeyPairDao): AsymmetricKeyPairRepository {
        return AsymmetricKeyPairRepository(asymmetricKeyPairDao)
    }

    @Provides
    @Singleton
    fun provideSymmetricKeyPairRepository(symmetricKeyDao: SymmetricKeyDao): SymmetricKeyRepository {
        return SymmetricKeyRepository(symmetricKeyDao)
    }

    @Provides
    @Singleton
    fun provideChatRequestRepository(chatRequestDao: ChatRequestDao): ChatRequestRepository {
        return ChatRequestRepository(chatRequestDao)
    }

}
