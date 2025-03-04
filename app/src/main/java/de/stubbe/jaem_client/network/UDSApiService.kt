
import de.stubbe.jaem_client.model.network.AddProfileResponse
import de.stubbe.jaem_client.model.network.ResponseMessage
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST

interface UDSApiService {
    /**
     * Gibt ein user aus dem UDS zurück
     *
     * @param uid Die id des users
     * @return Der user
     */
    @GET("user/{uid}")
    suspend fun getUserByID(uid: String): Call<GetUserResponse>

    /**
     * Gibt mehrere user aus dem UDS zurück welche dem username matchen
     *
     * @param username Ein zu suchendes Pattern
     * @return Eine Liste von usern
     */

    @GET("users/{username}")
    suspend fun getUsers(username: String): Call<GetUsersResponse>

    @POST("create_user")
    suspend fun addNewUser(@Body user: UserData): Call<AddProfileResponse>

    @DELETE("user/{uid}")
    suspend fun deleteUser(uid: String): Call<ResponseMessage>

    @POST("add_pub_key")
    suspend fun addPublicKey(@Body user: UserData): Call<AddProfileResponse>

    @DELETE("user/{uid}/{public_key}")
    suspend fun deletePublicKey(uid:String,publicKey: ByteArray): Call<ResponseMessage>
}