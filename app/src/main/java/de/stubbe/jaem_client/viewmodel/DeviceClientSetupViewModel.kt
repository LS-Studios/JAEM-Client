package de.stubbe.jaem_client.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.stubbe.jaem_client.repositories.database.EncryptionKeyRepository
import javax.inject.Inject

@HiltViewModel
class DeviceClientSetupViewModel @Inject constructor(
    private val encryptionKeyRepository: EncryptionKeyRepository
): ViewModel() {

}