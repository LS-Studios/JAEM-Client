package de.stubbe.jaem_client.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import de.stubbe.jaem_client.JAEMApplication
import de.stubbe.jaem_client.repositories.UserPreferencesRepository
import de.stubbe.jaem_client.utils.userPreferencesDataStore

object AppViewModelProvider {
    @SuppressLint("NewApi")
    val Factory = viewModelFactory {
        initializer {
            MainActivityViewModel(
                UserPreferencesRepository(jaemApplication().userPreferencesDataStore)
            )
        }
    }
}

fun CreationExtras.jaemApplication(): JAEMApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as JAEMApplication)