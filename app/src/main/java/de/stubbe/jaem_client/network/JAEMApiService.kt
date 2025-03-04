package de.stubbe.jaem_client.network

import de.stubbe.jaem_client.model.ShareProfileModel
import de.stubbe.jaem_client.model.enums.SymmetricEncryption
import de.stubbe.jaem_client.model.network.ResponseMessage
import de.stubbe.jaem_client.model.network.ShareProfileResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import java.time.Instant

interface JAEMApiService {
    /**
     * Erstellt einen Link zum Teilen eines Profils
     *
     * @param profileModel Das zu teilende Profil
     * @return Die uid zum geteilten Profil
     */
    @POST("share")
    suspend fun createShareProfile(@Body profileModel: ShareProfileModel): Call<ShareProfileResponse>

    /**
     * Gibt ein geteiltes Profil zurück
     *
     * @param profileId Die id des geteilten Profils
     * @return Das geteilte Profil
     */
    @GET("share/{id}")
    suspend fun getSharedProfile(profileId: String): Call<ShareProfileResponse>

    @POST("send_message")
    suspend fun sendMessage(encryption: SymmetricEncryption, recipientPublicKey: ByteArray, message: ByteArray): Call<ResponseMessage>

    @POST("get_messages")
    suspend fun getMessages(encryption: SymmetricEncryption, signature: ByteArray, publicKey: ByteArray, timeStamp: Instant): Call<ResponseMessage>

    @POST("delete_messages")
    suspend fun deleteMessage(encryption: SymmetricEncryption, signature: ByteArray, publicKey: ByteArray, timeStamp: Instant): Call<ResponseMessage>

}