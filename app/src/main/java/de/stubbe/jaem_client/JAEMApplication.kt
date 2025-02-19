package de.stubbe.jaem_client

import android.app.Application
import de.stubbe.jaem_client.data.AppContainer
import de.stubbe.jaem_client.data.AppDataContainer

class JAEMApplication: Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()

        container = AppDataContainer(this)
    }

}