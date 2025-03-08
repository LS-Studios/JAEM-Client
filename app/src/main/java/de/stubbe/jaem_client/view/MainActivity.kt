package de.stubbe.jaem_client.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import de.stubbe.jaem_client.datastore.UserPreferences
import de.stubbe.jaem_client.view.screens.Navigation
import de.stubbe.jaem_client.view.variables.JAEMTheme
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider
import de.stubbe.jaem_client.viewmodel.MainActivityViewModel
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val viewModel: MainActivityViewModel = hiltViewModel()
            val userPreferences by viewModel.userPreferences.collectAsState()
            val context = LocalContext.current

            LaunchedEffect(Unit) {
                viewModel.updateTheme(UserPreferences.Theme.DARK)

                val scheduler = Executors.newScheduledThreadPool(1)
                val task = Runnable {
                    viewModel.getNewMessages(context)
                }
                scheduler.scheduleWithFixedDelay(task, 0, 5, TimeUnit.SECONDS)

                //viewModel.deleteExampleData(this@MainActivity)
                 //viewModel.addExampleData()
            }

            JAEMTheme(
                theme = userPreferences.theme
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = JAEMThemeProvider.current.background,
                    contentWindowInsets = WindowInsets(0)
                ) { innerPadding ->
                    Navigation(
                        Modifier
                            .padding(innerPadding)
                            .consumeWindowInsets(innerPadding)
                            .systemBarsPadding()
                    )
                }
            }
        }
    }
}