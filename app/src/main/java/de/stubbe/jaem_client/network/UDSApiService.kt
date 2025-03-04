import de.stubbe.jaem_client.model.network.AddProfileResponse
import de.stubbe.jaem_client.network.ResponseMessage
import jdk.vm.ci.code.site.Call

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

    @Post("add_pub_key")
    suspend fun addPublicKey(@Body user: UserData): Call<AddProfileResponse>

    @DELETE("user/{uid}/{public_key}")
    suspend fun deletePublicKey(uid:String,publicKey: ByteArray): Call<ResponseMessage>
}

data class ResponseMessage (
    @SerializedName("message")
    val message: String,
)
