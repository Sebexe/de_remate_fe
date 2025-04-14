package com.grupo1.deremate.infrastructure

import android.content.Intent
import android.util.Log
import com.google.gson.GsonBuilder
import com.grupo1.deremate.LoginActivity
import com.grupo1.deremate.UadeAppplication
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

                val response = chain.proceed(requestBuilder.build())

                if (response.code == 401 || response.code == 403) {
                    handleExpiredToken()
                }

                response
            }
        }
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    private fun handleExpiredToken() {
        token = null

        val appContext = UadeAppplication.getAppContext()

        android.os.Handler(android.os.Looper.getMainLooper()).post {
            android.widget.Toast.makeText(
                appContext,
                "Tu sesión ha expirado. Por favor, iniciá sesión nuevamente.",
                android.widget.Toast.LENGTH_LONG
            ).show()
        }

        val intent = Intent(appContext, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        appContext.startActivity(intent)
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
