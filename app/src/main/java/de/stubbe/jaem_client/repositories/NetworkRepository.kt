package de.stubbe.jaem_client.repositories

import de.stubbe.jaem_client.model.ShareProfileModel
import de.stubbe.jaem_client.model.network.ShareProfileResponse
import de.stubbe.jaem_client.network.JAEMApiService
import retrofit2.Call
import javax.inject.Inject

class NetworkRepository @Inject constructor(
    val retrofitInstance: JAEMApiService
) {

    suspend fun createShareProfile(shareProfile: ShareProfileModel): Call<ShareProfileResponse> {
        //return retrofitInstance.createShareProfileLink(shareProfile)
        return object : Call<ShareProfileResponse> {
            override fun execute(): retrofit2.Response<ShareProfileResponse> {
                return retrofit2.Response.success(ShareProfileResponse(shareProfile.uid, null, System.currentTimeMillis()))
            }

            override fun enqueue(callback: retrofit2.Callback<ShareProfileResponse>) {
                callback.onResponse(this, execute())
            }

            override fun isExecuted(): Boolean {
                return false
            }

            override fun clone(): Call<ShareProfileResponse> {
                return this
            }

            override fun isCanceled(): Boolean {
                return false
            }

            override fun cancel() {
            }

            override fun request() = null

            override fun timeout() = null

        }
    }

    suspend fun getSharedProfile(profileId: String): Call<ShareProfileResponse> {
        //return retrofitInstance.getSharedProfile(id)
        return object : Call<ShareProfileResponse> {
            override fun execute(): retrofit2.Response<ShareProfileResponse> {
                return retrofit2.Response.error(404, null)
            }

            override fun enqueue(callback: retrofit2.Callback<ShareProfileResponse>) {
                callback.onResponse(this, execute())
            }

            override fun isExecuted(): Boolean {
                return false
            }

            override fun clone(): Call<ShareProfileResponse> {
                return this
            }

            override fun isCanceled(): Boolean {
                return false
            }

            override fun cancel() {
            }

            override fun request() = null

            override fun timeout() = null

        }
    }
}