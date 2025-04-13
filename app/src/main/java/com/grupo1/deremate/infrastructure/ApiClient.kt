package com.grupo1.deremate.infrastructure

import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
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
            addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                token?.let {
                    requestBuilder.addHeader("Authorization", "Bearer $it")
                }
                chain.proceed(requestBuilder.build())
            }
        }
            .connectTimeout(60, TimeUnit.SECONDS) // Tiempo para establecer conexión (ej. 60 segundos)
            .readTimeout(60, TimeUnit.SECONDS)    // Tiempo para leer datos de la respuesta (ej. 60 segundos)
            .writeTimeout(60, TimeUnit.SECONDS)   // Tiempo para escribir datos de la solicitud (ej. 60 segundos)
            .build()
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
        private const val DEFAULT_BASE_URL = "http://10.0.2.2:4002"
    }
}

// Asegura que la base URL termine con "/"
private fun String.ensureEndsWithSlash(): String {
    return if (endsWith("/")) this else "$this/"
}

// Utilidad para parsear mensaje de error de respuestas HTTP
fun parseErrorMessage(errorBody: ResponseBody?): String {
    return try {
        val errorJson = JSONObject(errorBody?.string() ?: "")
        errorJson.optString("message", "Error inesperado del servidor")
    } catch (e: Exception) {
        "No se pudo interpretar el mensaje de error"
    }
}
