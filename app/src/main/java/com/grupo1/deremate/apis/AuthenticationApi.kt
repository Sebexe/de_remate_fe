package com.grupo1.deremate.apis

import com.grupo1.deremate.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Call
import okhttp3.RequestBody
import com.google.gson.annotations.SerializedName

import com.grupo1.deremate.models.GenericResponseDTO
import com.grupo1.deremate.models.LoginRequestDTO
import com.grupo1.deremate.models.PasswordResetRequestDto
import com.grupo1.deremate.models.SignupRequestDTO

interface AuthenticationApi {
    /**
     * POST api/v1/auth/forgot-password
     * Request a password reset token
     * 
     * Responses:
     *  - 200: Password reset token sent
     *  - 404: User not found
     *  - 500: Error while sending verification email
     *
     * @param email User&#39;s email address
     * @return [Call]<[GenericResponseDTO]>
     */
    @POST("api/v1/auth/forgot-password")
    fun forgotPassword(@Query("email") email: kotlin.String): Call<GenericResponseDTO>

    /**
     * POST api/v1/auth/login
     * Authenticate user and return JWT token
     * 
     * Responses:
     *  - 200: Successfully authenticated
     *  - 400: Invalid request
     *  - 401: Invalid credentials, user disabled, email not verified
     *  - 404: User not found
     *
     * @param loginRequestDTO 
     * @return [Call]<[GenericResponseDTO]>
     */
    @POST("api/v1/auth/login")
    fun login(@Body loginRequestDTO: LoginRequestDTO): Call<GenericResponseDTO>

    /**
     * POST api/v1/auth/resend-verification
     * Resend the email verification token
     * 
     * Responses:
     *  - 200: Verification email sent
     *  - 400: Account already verified
     *  - 404: User not found
     *  - 500: Error while sending the email
     *
     * @param email User&#39;s email address
     * @return [Call]<[GenericResponseDTO]>
     */
    @POST("api/v1/auth/resend-verification")
    fun resendVerification(@Query("email") email: kotlin.String): Call<GenericResponseDTO>

    /**
     * POST api/v1/auth/reset-password
     * Reset the user&#39;s password
     * 
     * Responses:
     *  - 200: Password reset successfully
     *  - 400: Invalid password format
     *  - 401: Invalid or expired token
     *  - 404: User not found
     *
     * @param passwordResetRequestDto 
     * @return [Call]<[GenericResponseDTO]>
     */
    @POST("api/v1/auth/reset-password")
    fun resetPassword(@Body passwordResetRequestDto: PasswordResetRequestDto): Call<GenericResponseDTO>

    /**
     * POST api/v1/auth/signup
     * Register a new user
     * 
     * Responses:
     *  - 201: User registered successfully
     *  - 500: Email already exists or error sending verification email
     *
     * @param signupRequestDTO 
     * @return [Call]<[GenericResponseDTO]>
     */
    @POST("api/v1/auth/signup")
    fun signup(@Body signupRequestDTO: SignupRequestDTO): Call<GenericResponseDTO>

    /**
     * POST api/v1/auth/verify
     * Verify user&#39;s email
     * 
     * Responses:
     *  - 200: Email verified successfully
     *  - 400: Invalid token or account already verified
     *  - 404: User not found
     *
     * @param token Verification token
     * @param email User&#39;s email address
     * @return [Call]<[GenericResponseDTO]>
     */
    @POST("api/v1/auth/verify")
    fun verifyEmail(@Query("token") token: kotlin.String, @Query("email") email: kotlin.String): Call<GenericResponseDTO>

}
