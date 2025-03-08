package de.stubbe.jaem_client.network

import de.stubbe.jaem_client.model.network.ShareProfileResponse
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST

interface UDSApiService {
    @GET("user/{id}")
    suspend fun getUserProfile(uid: String): Response<ResponseBody>

    @GET("users/{username}")
    suspend fun findUsersByUsername(username: String): Response<ResponseBody>

    @POST("create_user")
    suspend fun joinService(@Body joinUDSRequestBody: String): Response<ResponseBody>

    @POST("add_pub_key")
    suspend fun addPubKey(@Body addPubKeyBody: String): Response<ResponseBody>

    @DELETE("user/{id}")
    suspend fun leaveService(uid: String): Response<ResponseBody>
}