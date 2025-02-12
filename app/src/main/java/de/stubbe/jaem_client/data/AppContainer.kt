package de.stubbe.jaem_client.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import de.stubbe.jaem_client.database.JAEMDatabase
import de.stubbe.jaem_client.datastore.UserPreferences
import de.stubbe.jaem_client.repositories.ChatRepository
import de.stubbe.jaem_client.repositories.MessageRepository
import de.stubbe.jaem_client.repositories.ProfileRepository
import de.stubbe.jaem_client.repositories.UserPreferencesRepository

interface AppContainer {

    val chatRepository: ChatRepository
    val messageRepository: MessageRepository
    val profileRepository: ProfileRepository

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

}

