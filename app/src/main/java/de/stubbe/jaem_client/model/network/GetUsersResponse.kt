import jdk.incubator.vector.Vector

data class GetUsersResponse(
    @SerializedName("users")
    val users: Vector<UserData>
)

data class GetUserResponse(
    @SerializedName("user")
    val users: UserData
)
