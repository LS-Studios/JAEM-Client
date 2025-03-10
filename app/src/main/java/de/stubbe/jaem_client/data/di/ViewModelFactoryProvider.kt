package de.stubbe.jaem_client.data.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import de.stubbe.jaem_client.viewmodel.EditServerListViewModel

@EntryPoint
@InstallIn(ActivityComponent::class)
interface ViewModelFactoryProvider {
    fun editServerListViewModelFactory(): EditServerListViewModel.Factory
}