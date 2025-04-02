package com.grupo1.deremate.apis

import com.grupo1.deremate.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Call
import okhttp3.RequestBody
import com.google.gson.annotations.SerializedName

import com.grupo1.deremate.models.UserDTO

interface UserControllerApi {
    /**
     * GET api/v1/user/info
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @return [Call]<[UserDTO]>
     */
    @GET("api/v1/user/info")
    fun getUserInfo(): Call<UserDTO>

}
