package com.grupo1.deremate.util
import com.grupo1.deremate.infrastructure.Serializer
import okhttp3.ResponseBody
import java.lang.Exception

inline fun <reified T> parseErrorBody(errorBody: ResponseBody?): T? {
    return try {
        errorBody?.charStream()?.use {
            Serializer.gson.fromJson(it, T::class.java)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}