package de.stubbe.jaem_client.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.stubbe.jaem_client.network.JAEMApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val JAEM_BASE_URL = "https://6f60-2a00-20-b043-2950-fcb9-1a46-589-8fc4.ngrok-free.app/"
    private const val UDS_BASE_URL = "http://192.168.55.159:3000/"

    @Provides
    @Singleton
    fun provideJAEMRetrofitInstance(): JAEMApiService {
        return Retrofit.Builder()
            .baseUrl(JAEM_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JAEMApiService::class.java)
    }

}