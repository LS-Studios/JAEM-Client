package de.stubbe.jaem_client.data

import android.content.Context
import de.stubbe.jaem_client.database.JAEMDatabase
import de.stubbe.jaem_client.repositories.ChatRepository
import de.stubbe.jaem_client.repositories.MessageRepository
import de.stubbe.jaem_client.repositories.ProfileRepository
import de.stubbe.jaem_client.repositories.UserPreferencesRepository
import de.stubbe.jaem_client.utils.userPreferencesDataStore

interface AppContainer {

    val chatRepository: ChatRepository
    val messageRepository: MessageRepository
    val profileRepository: ProfileRepository
    val userPreferencesRepository: UserPreferencesRepository

}

class AppDataContainer(val context: Context): AppContainer {

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

}

