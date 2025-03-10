package de.stubbe.jaem_client.data.di

import android.content.Context
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
import de.stubbe.jaem_client.network.JAEMApiService
import de.stubbe.jaem_client.network.UDSApiService
import de.stubbe.jaem_client.repositories.MessageDeliveryRepository
import de.stubbe.jaem_client.repositories.NetworkRepository
import de.stubbe.jaem_client.repositories.UDSRepository
import de.stubbe.jaem_client.repositories.UserPreferencesRepository
import de.stubbe.jaem_client.repositories.database.ChatRepository
import de.stubbe.jaem_client.repositories.database.EncryptionKeyRepository
import de.stubbe.jaem_client.repositories.database.MessageRepository
import de.stubbe.jaem_client.repositories.database.ProfileRepository
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
    fun provideEncryptionKeyRepository(
        asymmetricKeyPairDao: EncryptionKeyDao,
        userPreferencesRepository: UserPreferencesRepository
    ): EncryptionKeyRepository {
        return EncryptionKeyRepository(userPreferencesRepository, asymmetricKeyPairDao)
    }

    @Provides
    @Singleton
    fun provideMessageDeliveryRepository(
        chatRepository: ChatRepository,
        encryptionKeyRepository: EncryptionKeyRepository,
        profileRepository: ProfileRepository,
        messageRepository: MessageRepository,
        jaemApiService: JAEMApiService
    ): MessageDeliveryRepository {
        return MessageDeliveryRepository(
            chatRepository,
            encryptionKeyRepository,
            profileRepository,
            messageRepository,
            jaemApiService
        )
    }


    @Provides
    @Singleton
    fun provideUDSRepository(
        jaemDatabase: JAEMDatabase,
        udsApiService: UDSApiService,
        userPreferencesRepository: UserPreferencesRepository
    ): UDSRepository {
        return UDSRepository(
            jaemDatabase,
            udsApiService,
            userPreferencesRepository
        )
    }

    @Provides
    @Singleton
    fun provideNetworkRepository(
        @ApplicationContext context: Context,
        messageDeliveryRepository: MessageDeliveryRepository,
        udsRepository: UDSRepository
    ): NetworkRepository {
        return NetworkRepository(context, messageDeliveryRepository, udsRepository)
    }

}
