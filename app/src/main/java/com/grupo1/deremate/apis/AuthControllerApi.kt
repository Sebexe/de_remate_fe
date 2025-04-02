package com.grupo1.deremate.apis

import com.grupo1.deremate.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Call
import okhttp3.RequestBody
import com.google.gson.annotations.SerializedName

import com.grupo1.deremate.models.GenericResponseDTO
import com.grupo1.deremate.models.LoginRequestDTO
import com.grupo1.deremate.models.LoginResponseDTO
import com.grupo1.deremate.models.SignupRequestDTO

interface AuthControllerApi {
    /**
     * POST api/v1/auth/login
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param loginRequestDTO 
     * @return [Call]<[LoginResponseDTO]>
     */
    @POST("api/v1/auth/login")
    fun login(@Body loginRequestDTO: LoginRequestDTO): Call<LoginResponseDTO>

    /**
     * POST api/v1/auth/signup
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param signupRequestDTO 
     * @return [Call]<[GenericResponseDTO]>
     */
    @POST("api/v1/auth/signup")
    fun signup(@Body signupRequestDTO: SignupRequestDTO): Call<GenericResponseDTO>

}
