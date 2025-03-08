package de.stubbe.jaem_client.network

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface JAEMApiService {

    @POST("send_message")
    suspend fun sendMessage(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST("get_messages")
    suspend fun getMessages(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST("delete_messages")
    suspend fun deleteMessage(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST("share")
    suspend fun share(@Body requestBody: RequestBody): Response<ResponseBody>

    @GET("share/{share_link}")
    suspend fun getSharedProfile(@Path("share_link") shareLink: String): Response<ResponseBody>

}