package com.grupo1.deremate.infrastructure

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.google.gson.stream.JsonToken.NULL
import java.io.IOException
import kotlinx.datetime.LocalTime

class LocalTimeAdapter : TypeAdapter<LocalTime>() {
    @Throws(IOException::class)
    override fun write(out: JsonWriter?, value: LocalTime?) {
        if (value == null) {
            out?.nullValue()
        } else {
            out?.value(value.toString())
        }
    }

    @Throws(IOException::class)
    override fun read(out: JsonReader?): LocalTime? {
        out ?: return null

        when (out.peek()) {
            NULL -> {
                out.nextNull()
                return null
            }
            else -> {
                return LocalTime.parse(out.nextString())
            }
        }
    }
}
