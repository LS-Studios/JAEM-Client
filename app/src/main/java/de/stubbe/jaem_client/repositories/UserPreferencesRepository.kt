package de.stubbe.jaem_client.repositories

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import de.stubbe.jaem_client.datastore.CachedShareLinkModel
import de.stubbe.jaem_client.datastore.ServerUrlModel
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

    val isInitializedFlow: Flow<Boolean> = userPreferencesFlow
        .map { it.getIsInitialized() }

    val messageDeliveryUrlsFlow: Flow<List<ServerUrlModel>> = userPreferencesFlow
        .map { it.messageDeliveryUrlsList }

    val udsUrlsFlow: Flow<List<ServerUrlModel>> = userPreferencesFlow
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

    suspend fun updateMessageDeliveryUrls(newMessageDeliveryUrls: List<ServerUrlModel>) {
        userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .clearMessageDeliveryUrls()
                .addAllMessageDeliveryUrls(newMessageDeliveryUrls)
                .build()
        }
    }

    suspend fun updateUdsUrls(newUdsUrls: List<ServerUrlModel>) {
        userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .clearUdsUrls()
                .addAllUdsUrls(newUdsUrls)
                .build()
        }
    }

    suspend fun updateIsInitialized(isInitialized: Boolean) {
        userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .setIsInitialized(isInitialized)
                .build()
        }
    }

    val cachedShareLinks: Flow<List<CachedShareLinkModel>> = userPreferencesFlow
        .map { it.cachedShareLinksList }

    suspend fun updateCachedShareLinks(newCachedShareLinks: List<CachedShareLinkModel>) {
        userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .clearCachedShareLinks()
                .addAllCachedShareLinks(newCachedShareLinks)
                .build()
        }
    }

}