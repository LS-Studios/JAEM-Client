package de.stubbe.jaem_client.data.di

import UDSApiService
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

    private const val JAEM_BASE_URL = "http://localhost:8081/"
    private const val UDS_BASE_URL = "http://localhost:3000/"

    @Provides
    @Singleton
    fun provideJAEMRetrofitInstance(): JAEMApiService {
        return Retrofit.Builder()
            .baseUrl(JAEM_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JAEMApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUDSRetrofitInstance(): UDSApiService {
        return Retrofit.Builder()
            .baseUrl(UDS_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UDSApiService::class.java)
    }

}