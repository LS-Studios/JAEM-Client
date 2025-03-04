package de.stubbe.jaem_client.model.network

import com.google.gson.annotations.SerializedName

data class ResponseMessage (
    @SerializedName("message")
    val message: String,
)
