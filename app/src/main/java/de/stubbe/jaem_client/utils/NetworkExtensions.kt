package de.stubbe.jaem_client.utils

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

fun <T> Response<T>.splitResponse(): Pair<T?, ResponseBody?> {
    return if (this.isSuccessful) {
        Pair(this.body(), null)
    } else {
        Pair(null, this.errorBody())
    }
}

fun <T> Call<T>.executeSafely(): Pair<T?, ResponseBody?> {
    return try {
        val response = execute()
        if (response.isSuccessful) {
            Pair(response.body(), null)
        } else {
            Pair(null, response.errorBody())
        }
    } catch (e: Throwable) {
        Pair(null, ResponseBody.create(null, e.message ?: "Unknown error"))
    }
}