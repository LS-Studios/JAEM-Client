package de.stubbe.jaem_client.network

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface JAEMApiService {

    @POST("send_message")
    suspend fun sendMessage(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST("get_messages")
    suspend fun getMessages(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST("delete_messages")
    suspend fun deleteMessage(@Body requestBody: RequestBody): Response<ResponseBody>

}