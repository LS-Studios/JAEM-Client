package de.stubbe.jaem_client.viewmodel

import androidx.lifecycle.ViewModel
import de.stubbe.jaem_client.model.enums.AsymmetricEncryption
import de.stubbe.jaem_client.model.enums.SymmetricEncryption
import de.stubbe.jaem_client.repositories.database.ChatRepository
import de.stubbe.jaem_client.repositories.database.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow

class CreateChatViewModel(
    val chatRepository: ChatRepository,
    val profileRepository: ProfileRepository,
): ViewModel() {

    val profilePicture: MutableStateFlow<ByteArray?> = MutableStateFlow(null)
    val profileName: MutableStateFlow<String> = MutableStateFlow("")
    val profileDescription: MutableStateFlow<String> = MutableStateFlow("")

    val advancedOptionsOpen: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val asymmetricEncryption: MutableStateFlow<AsymmetricEncryption> = MutableStateFlow(AsymmetricEncryption.ED25519)
    val symmetricEncryption: MutableStateFlow<SymmetricEncryption> = MutableStateFlow(SymmetricEncryption.AES)

    fun changeProfilePicture(picture: ByteArray) {
        profilePicture.value = picture
    }

    fun changeProfileName(name: String) {
        profileName.value = name
    }

    fun changeProfileDescription(description: String) {
        profileDescription.value = description
    }

    fun toggleAdvancedOptions() {
        advancedOptionsOpen.value = !advancedOptionsOpen.value
    }

    fun changeAsymmetricEncryption(encryption: AsymmetricEncryption) {
        asymmetricEncryption.value = encryption
    }

    fun changeSymmetricEncryption(encryption: SymmetricEncryption) {
        symmetricEncryption.value = encryption
    }

}