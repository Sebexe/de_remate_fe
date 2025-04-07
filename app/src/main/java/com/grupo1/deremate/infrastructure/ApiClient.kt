package com.grupo1.deremate.infrastructure

import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiClient @Inject constructor() {

    private var baseUrl: String = DEFAULT_BASE_URL
    private var token: String? = null

    private val gson = GsonBuilder().create()

    private val loggingInterceptor = HttpLoggingInterceptor { message ->
        Log.d("ApiClient", message)
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private fun buildClient(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            addInterceptor(loggingInterceptor)
            token?.let {
                addInterceptor { chain ->
                    val newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $it")
                        .build()
                    chain.proceed(newRequest)
                }
            }
        }.build()
    }

    private fun buildRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl.ensureEndsWithSlash())
            .client(buildClient())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    fun <T> createService(service: Class<T>): T {
        return buildRetrofit().create(service)
    }

    fun setBaseUrl(url: String): ApiClient {
        baseUrl = url
        return this
    }

    fun setToken(newToken: String?): ApiClient {
        token = newToken
        return this
    }

    companion object {
        private const val DEFAULT_BASE_URL = "http://10.0.2.2:8080"
    }
}

private fun String.ensureEndsWithSlash(): String {
    return if (endsWith("/")) this else "$this/"
}
