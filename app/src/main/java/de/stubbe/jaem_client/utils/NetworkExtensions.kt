package de.stubbe.jaem_client.utils

import retrofit2.Call
import retrofit2.HttpException

fun <T> Call<T>.executeSafely(): Pair<T?, Throwable?> {
    return try {
        val response = execute()
        if (response.isSuccessful) {
            Pair(response.body(), null)
        } else {
            Pair(null, HttpException(response))
        }
    } catch (e: Throwable) {
        Pair(null, e)
    }
}