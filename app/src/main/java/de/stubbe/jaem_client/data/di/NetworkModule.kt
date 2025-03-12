package de.stubbe.jaem_client.data.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.stubbe.jaem_client.model.encryption.SymmetricEncryption
import de.stubbe.jaem_client.model.encryption.SymmetricEncryptionDeserializer
import de.stubbe.jaem_client.model.encryption.SymmetricEncryptionSerializer
import de.stubbe.jaem_client.network.MessageDeliveryApiService
import de.stubbe.jaem_client.network.UDSApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val JAEM_BASE_URL = "https://jaem.web.app/message-delivery/"
    private const val UDS_BASE_URL = "https://jaem.web.app/user-discovery/"

    var gson: Gson = GsonBuilder()
        .registerTypeAdapter(SymmetricEncryption::class.java, SymmetricEncryptionSerializer())
        .registerTypeAdapter(SymmetricEncryption::class.java, SymmetricEncryptionDeserializer())
        .create()

    @Provides
    @Singleton
    fun provideJAEMRetrofitInstance(): MessageDeliveryApiService {
        return Retrofit.Builder()
            .baseUrl(JAEM_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(MessageDeliveryApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUDSRetrofitInstance(): UDSApiService {
        return Retrofit.Builder()
            .baseUrl(UDS_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(UDSApiService::class.java)
    }

}