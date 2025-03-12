package de.stubbe.jaem_client.network

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface MessageDeliveryApiService {

    @POST
    suspend fun sendMessage(@Url url: String, @Body requestBody: RequestBody): Response<ResponseBody>

    @POST
    suspend fun getMessages(@Url url: String, @Body requestBody: RequestBody): Response<ResponseBody>

    @POST
    suspend fun deleteMessage(@Url url: String, @Body requestBody: RequestBody): Response<ResponseBody>

    @POST
    suspend fun share(@Url url: String, @Body requestBody: RequestBody): Response<ResponseBody>

    @GET
    suspend fun getSharedProfile(@Url url: String): Response<ResponseBody>

}