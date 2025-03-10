package de.stubbe.jaem_client.repositories

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import de.stubbe.jaem_client.datastore.UserPreferences
import de.stubbe.jaem_client.datastore.UserPreferences.Language
import de.stubbe.jaem_client.datastore.UserPreferences.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map


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

    val getMessageDeliveryUrlsFlow: Flow<List<String>> = userPreferencesFlow
        .map { it.messageDeliveryUrlsList }

    val getUdsUrlsFlow: Flow<List<String>> = userPreferencesFlow
        .map { it.udsUrlsList }

    suspend fun updateLanguage(newLanguage: Language) {
        userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .setLanguage(newLanguage)
                .build()
        }
    }

    suspend fun updateTheme(newTheme: Theme) {
        userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .setTheme(newTheme)
                .build()
        }
    }

    suspend fun updateUserProfileUid(newProfileUid: String) {
        userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .setUserProfileUid(newProfileUid)
                .build()
        }
    }

    suspend fun updateMessageDeliveryUrls(newMessageDeliveryUrls: List<String>) {
        userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .clearMessageDeliveryUrls()
                .addAllMessageDeliveryUrls(newMessageDeliveryUrls)
                .build()
        }
    }

    suspend fun addMessageDeliverUrl(newMessageDeliverUrl: String) {
        userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .addMessageDeliveryUrls(newMessageDeliverUrl)
                .build()
        }
    }

    suspend fun removeMessageDeliverUrl(messageDeliverUrl: String) {
        userPreferencesStore.updateData { currentPreferences ->
            val newUrls = currentPreferences.messageDeliveryUrlsList.filter { it != messageDeliverUrl }
            currentPreferences.toBuilder()
                .clearMessageDeliveryUrls()
                .addAllMessageDeliveryUrls(newUrls)
                .build()
        }
    }

    suspend fun updateUdsUrls(newUdsUrls: List<String>) {
        userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .clearUdsUrls()
                .addAllUdsUrls(newUdsUrls)
                .build()
        }
    }

    suspend fun addUdsUrl(newUdsUrl: String) {
        userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .addUdsUrls(newUdsUrl)
                .build()
        }
    }

    suspend fun removeUdsUrl(udsUrl: String) {
        userPreferencesStore.updateData { currentPreferences ->
            val newUrls = currentPreferences.udsUrlsList.filter { it != udsUrl }
            currentPreferences.toBuilder()
                .clearUdsUrls()
                .addAllUdsUrls(newUrls)
                .build()
        }
    }

}