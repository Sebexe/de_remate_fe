package com.grupo1.deremate.infrastructure

import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.grupo1.deremate.LoginActivity
import com.grupo1.deremate.R
import com.grupo1.deremate.UadeAppplication
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
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
        Log.d(TAG, "Building OkHttpClient...")
        return OkHttpClient.Builder().apply {
            addInterceptor(loggingInterceptor)

            addInterceptor { chain ->
                val originalRequest = chain.request() // Guardar petición original
                val requestBuilder = originalRequest.newBuilder()

                // Sigue usando la variable interna 'token'
                this@ApiClient.token?.let {
                    requestBuilder.addHeader("Authorization", "Bearer $it")
                    Log.d(TAG, "Interceptor: Added Authorization header.")
                }

                val request = requestBuilder.build()
                val response: Response
                try {
                    response = chain.proceed(request)
                } catch (e: Exception) {
                    Log.e(TAG, "Interceptor: Network Exception during chain.proceed()", e)
                    throw IOException("Network error during request", e) // Re-lanzar
                }


                // --- CAMBIO MÍNIMO AQUÍ ---
                val requestPath = originalRequest.url.encodedPath // Obtener la ruta
                Log.d(TAG, "Interceptor: Path=$requestPath, Code=${response.code}")

                // Verificar si es 401/403 Y *NO* es la ruta de resetPassword
                if ((response.code == 401 || response.code == 403) && requestPath != RESET_PASSWORD_PATH) {
                    Log.w(TAG, "Interceptor: Detected 401/403 on protected path ($requestPath). Calling handleExpiredToken.")
                    handleExpiredToken() // Solo llama si NO es la ruta de reset
                }

                response // Devolver la respuesta
            }
        }
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    private fun handleExpiredToken() {
        Log.w(TAG, "handleExpiredToken called - Session likely expired or invalid.")
        // Limpia el token interno
        this.token = null
        Log.d(TAG, "Internal token variable set to null.")

        val appContext = UadeAppplication.getAppContext()

        android.os.Handler(android.os.Looper.getMainLooper()).post {
            Toast.makeText(
                appContext,

                appContext.getString(R.string.session_expired_message),
                Toast.LENGTH_LONG
            ).show()
        }

        val intent = Intent(appContext, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        try {
            appContext.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error starting LoginActivity from handleExpiredToken", e)
        }
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
        private const val DEFAULT_BASE_URL = "http://10.0.2.2:8080" // Asegúrate que es correcta
        private const val RESET_PASSWORD_PATH = "/api/v1/auth/reset-password"
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