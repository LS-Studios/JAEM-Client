package de.stubbe.jaem_client.network

import de.stubbe.jaem_client.model.network.UDSUserDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface UDSApiService {

    @GET
    suspend fun getUserProfile(@Url url: String): Response<UDSUserDto>

    @GET
    suspend fun getUsers(@Url url: String): List<UDSUserDto>

    @GET
    suspend fun findUsersByUsername(@Url url: String): List<UDSUserDto>

    @POST
    suspend fun joinService(@Url url: String, @Body udsModelDto: UDSUserDto): Response<ResponseBody>

    @DELETE
    suspend fun leaveService(@Url url: String): Response<ResponseBody>

}