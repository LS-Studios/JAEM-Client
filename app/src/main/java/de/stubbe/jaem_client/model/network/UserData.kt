import com.google.gson.annotations.SerializedName

data class UserData (
    @SerializedName("uid")
    val uid: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("public_keys")
    val createdAt: List<PublicKey>,
)

data class PublicKey(
    @SerializedName("algorithm")
    val algorithm: String,
    @SerializedName("key")
    val key: String
)
