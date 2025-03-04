import com.google.gson.annotations.SerializedName

data class GetUsersResponse(
    @SerializedName("users")
    val users: List<UserData>
)

data class GetUserResponse(
    @SerializedName("user")
    val users: UserData
)
