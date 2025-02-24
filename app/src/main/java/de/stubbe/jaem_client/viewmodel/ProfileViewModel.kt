package de.stubbe.jaem_client.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import de.stubbe.jaem_client.data.SHARING_STARTED_DEFAULT
import de.stubbe.jaem_client.model.ShareProfileModel
import de.stubbe.jaem_client.model.entries.ProfilePresentationModel
import de.stubbe.jaem_client.model.network.ShareProfileResponse
import de.stubbe.jaem_client.repositories.NetworkRepository
import de.stubbe.jaem_client.repositories.database.AsymmetricKeyPairRepository
import de.stubbe.jaem_client.repositories.database.ProfileRepository
import de.stubbe.jaem_client.repositories.database.SymmetricKeyRepository
import de.stubbe.jaem_client.utils.executeSafely
import de.stubbe.jaem_client.utils.toBitmap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class ProfileViewModel(
    profileRepository: ProfileRepository,
    asymmetricKeyPairRepository: AsymmetricKeyPairRepository,
    symmetricKeyRepository: SymmetricKeyRepository,
    private val networkRepository: NetworkRepository,
    profileId: Int
): ViewModel() {

    companion object {
        val PROFILE_ID_KEY = object : CreationExtras.Key<Int> {}
    }

    private val profileFlow = profileRepository.getProfileByIdWithChange(profileId)
    private val asymmetricKeyPairsFlow = asymmetricKeyPairRepository.getAsymmetricKeyPairsByProfileId(profileId)
    private val symmetricKeyPairsFlow = symmetricKeyRepository.getSymmetricKeyPairsByProfileId(profileId)

    val profile = combine(
        profileFlow, asymmetricKeyPairsFlow, symmetricKeyPairsFlow
    ) { profile, asymmetricKeyPairs, symmetricKeyPairs ->
        ProfilePresentationModel(
            name = profile.name,
            profilePicture = profile.profilePicture?.toBitmap(),
            description = profile.description,
            asymmetricKeyPairs = asymmetricKeyPairs,
            symmetricKeys = symmetricKeyPairs,
            profile = profile
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(SHARING_STARTED_DEFAULT),
        initialValue = null
    )

    val shareProfileResponse: MutableStateFlow<ShareProfileResponse?> = MutableStateFlow(null)

    val isShareProfileBottomSheetVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)

    fun openShareProfileBottomSheet() {
        isShareProfileBottomSheetVisible.value = true
    }

    fun closeShareProfileBottomSheet() {
        isShareProfileBottomSheetVisible.value = false
    }

    fun fetchOrCreateShareProfileLink(profile: ProfilePresentationModel) {
        val shareProfileModel = ShareProfileModel.fromProfilePresentationModel(profile, profile.asymmetricKeyPairs, profile.symmetricKeys)

        viewModelScope.launch {
            val (shareProfile, shareProfileError) = networkRepository.getSharedProfile(profile.profile.uid).executeSafely()

            if (shareProfile != null && shareProfileError == null) {
                shareProfileResponse.value = shareProfile
            } else {
                val (newShareProfile, newShareProfileError) = networkRepository.createShareProfile(shareProfileModel).executeSafely()

                if (newShareProfile != null && newShareProfileError == null) {
                    shareProfileResponse.value = newShareProfile
                }
            }
        }
    }
}