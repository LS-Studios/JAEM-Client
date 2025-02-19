package de.stubbe.jaem_client.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import de.stubbe.jaem_client.repositories.ProfileRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class ProfileViewModel(
    profileRepository: ProfileRepository,
    profileId: Int
): ViewModel() {

    companion object {
        val PROFILE_ID_KEY = object : CreationExtras.Key<Int> {}
    }

    val profile = profileRepository
        .getProfileByIdWithChange(profileId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )

}