package de.stubbe.jaem_client.utils

import okhttp3.ResponseBody
import retrofit2.Response

fun <T> Response<T>.splitResponse(): Pair<T?, ResponseBody?> {
    return if (this.isSuccessful) {
        Pair(this.body(), null)
    } else {
        Pair(null, this.errorBody())
    }
}