import de.stubbe.jaem_client.model.network.AddProfileResponse
import de.stubbe.jaem_client.model.network.PublicKey
import jdk.incubator.vector.Vector

data class UserData (
    @SerializedName("uid")
    val uid: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("public_keys")
    val createdAt: Vector<PublicKey>,
)

data class PublicKey(
    @SerializedName("algorithm")
    val algorithm: String,
    @SerializedName("key")
    val key: String
)
