package de.stubbe.jaem_client.viewmodel

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import de.stubbe.jaem_client.repositories.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.coroutines.coroutineContext
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import de.stubbe.jaem_client.datastore.UserPreferences
import de.stubbe.jaem_client.datastore.UserPreferences.Theme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MainActivityViewModel(
    userPreferencesRepository: UserPreferencesRepository
): ViewModel() {

    val userPreferences = userPreferencesRepository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences.getDefaultInstance()
        )
}