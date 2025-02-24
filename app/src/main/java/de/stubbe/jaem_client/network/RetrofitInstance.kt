package de.stubbe.jaem_client.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//https://square.github.io/retrofit/

object RetrofitInstance {
    private const val BASE_URL = "http://localhost:8080/"

    val api: JAEMApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JAEMApiService::class.java)
    }
}