package de.stubbe.jaem_client.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import dagger.hilt.android.lifecycle.HiltViewModel
import de.stubbe.jaem_client.data.SHARING_STARTED_DEFAULT
import de.stubbe.jaem_client.database.JAEMDatabase
import de.stubbe.jaem_client.datastore.UserPreferences
import de.stubbe.jaem_client.model.NavRoute
import de.stubbe.jaem_client.repositories.UserPreferencesRepository
import de.stubbe.jaem_client.repositories.database.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val profileRepository: ProfileRepository,
    private val jaemDatabase: JAEMDatabase
): ViewModel() {

    val userPreferencesFlow = userPreferencesRepository.userPreferencesFlow

    val selectedTheme = userPreferencesFlow
        .map { userPreferences ->
            userPreferences.theme
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SHARING_STARTED_DEFAULT),
            initialValue = UserPreferences.Theme.SYSTEM
        )

    val selectedLanguage = userPreferencesFlow
        .map { userPreferences ->
            userPreferences.language
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SHARING_STARTED_DEFAULT),
            initialValue = UserPreferences.Language.GERMAN
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val allowProfileSharing = userPreferencesFlow
        .flatMapLatest { userPreferences ->
            profileRepository.getProfileByUidFlow(userPreferences.userProfileUid)
                .map { profile ->
                    profile?.allowProfileSharing
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SHARING_STARTED_DEFAULT),
            initialValue = false
        )

    val deleteDataDialogIsOpen: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isMessageDeliveryDialogOpen: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isUdsDialogOpen: MutableStateFlow<Boolean> = MutableStateFlow(false)

    fun openDeleteDataDialog() {
        deleteDataDialogIsOpen.value = true
    }

    fun closeDeleteDataDialog() {
        deleteDataDialogIsOpen.value = false
    }

    fun openMessageDeliveryDialog() {
        isMessageDeliveryDialogOpen.value = true
    }

    fun closeMessageDeliveryDialog() {
        isMessageDeliveryDialogOpen.value = false
    }

    fun openUdsDialog() {
        isUdsDialogOpen.value = true
    }

    fun closeUdsDialog() {
        isUdsDialogOpen.value = false
    }

    fun updateTheme(theme: UserPreferences.Theme) {
        viewModelScope.launch(Dispatchers.IO) {
            userPreferencesRepository.updateTheme(theme)
        }
    }

    fun updateLanguage(language: UserPreferences.Language) {
        viewModelScope.launch(Dispatchers.IO) {
            userPreferencesRepository.updateLanguage(language)
        }
    }

    fun updateProfileSharing(allowProfileSharing: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val profile = profileRepository.getProfileByUid(userPreferencesFlow.first().userProfileUid)
            profile?.let {
                profileRepository.updateProfile(it.copy(allowProfileSharing = allowProfileSharing))
            }
        }
    }

    fun deleteDeviceData(navigationViewModel: NavigationViewModel) {
        viewModelScope.launch(Dispatchers.IO) {
            jaemDatabase.withTransaction {
                jaemDatabase.chatDao().clearAll()
                jaemDatabase.encryptionKeyDao().clearAll()
                jaemDatabase.messageDao().clearAll()
                jaemDatabase.profileDao().clearAll()
                jaemDatabase.udsUserDao().clearAll()
            }

            userPreferencesRepository.updateIsInitialized(false)

            withContext(Dispatchers.Main) {
                navigationViewModel.navigateTo(NavRoute.InitDevice)
            }
        }
    }

}