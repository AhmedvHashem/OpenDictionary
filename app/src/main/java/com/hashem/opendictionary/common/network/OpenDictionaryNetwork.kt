package com.hashem.opendictionary.common.network

import com.hashem.opendictionary.feature.data.remote.WordRemoteDataSource
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

abstract class OpenDictionaryNetwork {
    abstract fun dataSource(): WordRemoteDataSource

    companion object {
        private const val BASE_URL = "https://api.dictionaryapi.dev/api/v2/"

        private var INSTANCE: OpenDictionaryNetwork? = null
        fun getInstance(): OpenDictionaryNetwork {
            return INSTANCE ?: provideNetwork().also { INSTANCE = it }
        }

        private fun provideNetwork(): OpenDictionaryNetwork {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC)
            val client: OkHttpClient = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            val retrofit = Retrofit.Builder()
                .client(client)
                .baseUrl(BASE_URL)
                .addConverterFactory(Converter.json())
                .build()

            return object : OpenDictionaryNetwork() {
                override fun dataSource(): WordRemoteDataSource {
                    return retrofit.create(WordRemoteDataSource::class.java)
                }
            }
        }
    }
}