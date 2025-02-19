package de.stubbe.jaem_client.viewmodel

import android.annotation.SuppressLint
import androidx.collection.intIntMapOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import de.stubbe.jaem_client.JAEMApplication
import de.stubbe.jaem_client.repositories.UserPreferencesRepository
import de.stubbe.jaem_client.utils.userPreferencesDataStore

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
                jaemApplication().container.messageRepository
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
            ChatViewModel(
                jaemApplication().container.messageRepository,
                jaemApplication().container.chatRepository,
                jaemApplication().container.profileRepository,
                jaemApplication().container.userPreferencesRepository
            )
        }

        initializer {
            val profileId = this[ProfileViewModel.PROFILE_ID_KEY] as Int

            ProfileViewModel(
                jaemApplication().container.profileRepository,
                profileId
            )
        }
    }
}

/**
 * Gibt die JAEM Application zurück
 */
fun CreationExtras.jaemApplication(): JAEMApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as JAEMApplication)