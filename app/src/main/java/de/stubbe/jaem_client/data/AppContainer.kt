package de.stubbe.jaem_client.data

import android.content.Context
import de.stubbe.jaem_client.database.JAEMDatabase
import de.stubbe.jaem_client.repositories.NetworkRepository
import de.stubbe.jaem_client.repositories.UserPreferencesRepository
import de.stubbe.jaem_client.repositories.database.AsymmetricKeyPairRepository
import de.stubbe.jaem_client.repositories.database.ChatRepository
import de.stubbe.jaem_client.repositories.database.MessageRepository
import de.stubbe.jaem_client.repositories.database.ProfileRepository
import de.stubbe.jaem_client.repositories.database.SymmetricKeyRepository
import de.stubbe.jaem_client.utils.userPreferencesDataStore

interface AppContainer {

    val chatRepository: ChatRepository
    val messageRepository: MessageRepository
    val profileRepository: ProfileRepository
    val asymmetricKeyPairRepository: AsymmetricKeyPairRepository
    val symmetricKeyRepository: SymmetricKeyRepository
    val userPreferencesRepository: UserPreferencesRepository
    val networkRepository: NetworkRepository

}

class AppDataContainer(private val context: Context): AppContainer {

    override val chatRepository: ChatRepository by lazy {
        ChatRepository(JAEMDatabase.getDatabase(context).chatDao())
    }

    override val messageRepository: MessageRepository by lazy {
        MessageRepository(JAEMDatabase.getDatabase(context).messageDao())
    }

    override val profileRepository: ProfileRepository by lazy {
        ProfileRepository(JAEMDatabase.getDatabase(context).profileDao())
    }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context.userPreferencesDataStore)
    }

    override val asymmetricKeyPairRepository: AsymmetricKeyPairRepository by lazy {
        AsymmetricKeyPairRepository(JAEMDatabase.getDatabase(context).asymmetricKeyPairDao())
    }

    override val symmetricKeyRepository: SymmetricKeyRepository by lazy {
        SymmetricKeyRepository(JAEMDatabase.getDatabase(context).symmetricKeyDao())
    }

    override val networkRepository: NetworkRepository by lazy {
        NetworkRepository()
    }

}

