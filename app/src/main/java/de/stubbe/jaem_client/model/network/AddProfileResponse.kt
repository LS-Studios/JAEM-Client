package de.stubbe.jaem_client.model.network

import UserData
import com.google.gson.annotations.SerializedName

data class AddProfileResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("user")
    val user: UserData
)



