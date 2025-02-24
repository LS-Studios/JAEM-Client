package de.stubbe.jaem_client.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import de.stubbe.jaem_client.JAEMApplication

/**
 * Factory für die ViewModels
 */
object AppViewModelProvider {
    @SuppressLint("NewApi")
    val Factory = viewModelFactory {
        initializer {
            MainActivityViewModel(
                jaemApplication().container.userPreferencesRepository,
                jaemApplication().container.chatRepository,
                jaemApplication().container.profileRepository,
                jaemApplication().container.messageRepository,
                jaemApplication().container.asymmetricKeyPairRepository,
                jaemApplication().container.symmetricKeyRepository
            )
        }

        initializer {
            NavigationViewModel()
        }

        initializer {
            ChatOverviewViewModel(
                jaemApplication().container.chatRepository,
                jaemApplication().container.messageRepository,
                jaemApplication().container.profileRepository,
                jaemApplication().container.userPreferencesRepository
            )
        }

        initializer {
            val chatId = this[ChatViewModel.CHAT_ID_KEY] as Int

            ChatViewModel(
                jaemApplication().container.messageRepository,
                jaemApplication().container.chatRepository,
                jaemApplication().container.profileRepository,
                jaemApplication().container.userPreferencesRepository,
                chatId
            )
        }

        initializer {
            val profileId = this[ProfileViewModel.PROFILE_ID_KEY] as Int

            ProfileViewModel(
                jaemApplication().container.profileRepository,
                jaemApplication().container.asymmetricKeyPairRepository,
                jaemApplication().container.symmetricKeyRepository,
                jaemApplication().container.networkRepository,
                profileId
            )
        }

        initializer {
            CreateChatViewModel(
                jaemApplication().container.chatRepository,
                jaemApplication().container.profileRepository
            )
        }
    }
}

/**
 * Gibt die JAEM Application zurück
 */
fun CreationExtras.jaemApplication(): JAEMApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as JAEMApplication)