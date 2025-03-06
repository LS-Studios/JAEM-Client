package de.stubbe.jaem_client.network

import de.stubbe.jaem_client.model.network.ShareProfileResponse
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface JAEMApiService {
    /**
     * Erstellt einen Link zum Teilen eines Profils
     *
     * @param profileModel Das zu teilende Profil
     * @return Die uid zum geteilten Profil
     */
    @POST("share")
    suspend fun createShareProfile(@Body profileModel: RequestBody): Call<ShareProfileResponse>

    /**
     * Gibt ein geteiltes Profil zurück
     *
     * @param profileId Die id des geteilten Profils
     * @return Das geteilte Profil
     */
    @GET("share/{id}")
    suspend fun getSharedProfile(profileId: String): Call<ShareProfileResponse>

    @GET("hello")
    suspend fun hello(): Call<String>

    @POST("send_message")
    suspend fun sendMessage(@Body requestBody: RequestBody): Call<Response<Void>>

    @POST("get_messages")
    fun getMessages(@Body requestBody: RequestBody): Call<ResponseBody>

    @POST("delete_messages")
    suspend fun deleteMessage(@Body requestBody: RequestBody): Call<ResponseBody>

}