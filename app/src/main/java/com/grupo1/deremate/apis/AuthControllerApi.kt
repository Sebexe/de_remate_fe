package com.grupo1.deremate.apis

import com.grupo1.deremate.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Call
import okhttp3.RequestBody
import com.google.gson.annotations.SerializedName

import com.grupo1.deremate.models.GenericResponseDTOObject
import com.grupo1.deremate.models.GenericResponseDTOString
import com.grupo1.deremate.models.LoginRequestDTO
import com.grupo1.deremate.models.PasswordResetRequestDto
import com.grupo1.deremate.models.SignupRequestDTO

interface AuthControllerApi {
    /**
     * POST api/v1/auth/forgot-password
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param email 
     * @return [Call]<[GenericResponseDTOString]>
     */
    @POST("api/v1/auth/forgot-password")
    fun forgotPassword(@Query("email") email: kotlin.String): Call<GenericResponseDTOString>

    /**
     * POST api/v1/auth/login
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param loginRequestDTO 
     * @return [Call]<[GenericResponseDTOObject]>
     */
    @POST("api/v1/auth/login")
    fun login(@Body loginRequestDTO: LoginRequestDTO): Call<GenericResponseDTOObject>

    /**
     * POST api/v1/auth/resend-verification
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param email 
     * @return [Call]<[GenericResponseDTOString]>
     */
    @POST("api/v1/auth/resend-verification")
    fun resendVerification(@Query("email") email: kotlin.String): Call<GenericResponseDTOString>

    /**
     * POST api/v1/auth/reset-password
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param passwordResetRequestDto 
     * @return [Call]<[GenericResponseDTOString]>
     */
    @POST("api/v1/auth/reset-password")
    fun resetPassword(@Body passwordResetRequestDto: PasswordResetRequestDto): Call<GenericResponseDTOString>

    /**
     * POST api/v1/auth/signup
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param signupRequestDTO 
     * @return [Call]<[GenericResponseDTOString]>
     */
    @POST("api/v1/auth/signup")
    fun signup(@Body signupRequestDTO: SignupRequestDTO): Call<GenericResponseDTOString>

    /**
     * POST api/v1/auth/verify
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param token 
     * @param email 
     * @return [Call]<[GenericResponseDTOString]>
     */
    @POST("api/v1/auth/verify")
    fun verifyEmail(@Query("token") token: kotlin.String, @Query("email") email: kotlin.String): Call<GenericResponseDTOString>

}
