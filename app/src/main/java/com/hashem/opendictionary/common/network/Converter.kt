package com.hashem.opendictionary.common.network

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Converter
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object Converter {
    fun json(): Converter.Factory {
        val networkJson = Json { ignoreUnknownKeys = true }
        return networkJson.asConverterFactory("application/json; charset=UTF8".toMediaType())
    }
}