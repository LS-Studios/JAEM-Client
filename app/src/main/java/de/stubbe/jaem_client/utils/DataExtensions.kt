package de.stubbe.jaem_client.utils

import android.content.Context
import androidx.datastore.dataStore
import de.stubbe.jaem_client.data.USER_PREFERENCES_NAME
import de.stubbe.jaem_client.data.UserPreferencesSerializer

val Context.userPreferencesDataStore by dataStore(
    fileName = USER_PREFERENCES_NAME,
    serializer = UserPreferencesSerializer
)