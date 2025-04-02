package com.grupo1.deremate.rest.auth

import com.grupo1.deremate.rest.auth.dto.LoginRequest
import com.grupo1.deremate.rest.auth.dto.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApiService {
    @POST("/login")
    fun postLogin(@Body loginRequest: LoginRequest): Call<LoginResponse>
}