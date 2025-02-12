package de.stubbe.jaem_client.repositories

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import de.stubbe.jaem_client.datastore.UserPreferences
import de.stubbe.jaem_client.datastore.UserPreferences.Language
import de.stubbe.jaem_client.datastore.UserPreferences.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class UserPreferencesRepository(
    private val userPreferencesStore: DataStore<UserPreferences>
) {
    private val TAG: String = "UserPreferencesRepo"

    val userPreferencesFlow: Flow<UserPreferences> = userPreferencesStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading sort order preferences.", exception)
                emit(UserPreferences.getDefaultInstance())
            } else {
                throw exception
            }
        }

    suspend fun updateLanguage(newLanguage: Language) {
        userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder().setLanguage(newLanguage).build()
        }
    }

    suspend fun updateTheme(newTheme: Theme) {
        userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder().setTheme(newTheme).build()
        }
    }
}