package de.stubbe.jaem_client

import android.app.Application
import android.content.Context
import androidx.datastore.dataStore
import de.stubbe.jaem_client.data.AppContainer
import de.stubbe.jaem_client.data.AppDataContainer
import de.stubbe.jaem_client.data.USER_PREFERENCES_NAME
import de.stubbe.jaem_client.data.UserPreferencesSerializer

class JAEMApplication: Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()

        container = AppDataContainer(this)
    }

}