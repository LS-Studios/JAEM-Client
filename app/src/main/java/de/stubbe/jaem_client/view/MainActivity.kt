package de.stubbe.jaem_client.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import de.stubbe.jaem_client.utils.userPreferencesDataStore
import de.stubbe.jaem_client.view.theme.JAEMClientTheme
import de.stubbe.jaem_client.viewmodel.AppViewModelProvider
import de.stubbe.jaem_client.viewmodel.MainActivityViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import de.stubbe.jaem_client.datastore.UserPreferences

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainActivityViewModel = viewModel(factory = AppViewModelProvider.Factory)
            val userPreferences by viewModel.userPreferences.collectAsState()

            JAEMClientTheme(
                darkTheme = when(userPreferences.theme) {
                    UserPreferences.Theme.LIGHT -> false
                    UserPreferences.Theme.DARK -> true
                    UserPreferences.Theme.SYSTEM -> isSystemInDarkTheme()
                    UserPreferences.Theme.UNRECOGNIZED -> false
                    null -> false
                },
            ) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                }
            }
        }
    }
}