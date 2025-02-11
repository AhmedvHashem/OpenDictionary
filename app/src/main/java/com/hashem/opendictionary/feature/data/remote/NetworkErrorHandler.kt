package com.hashem.opendictionary.feature.data.remote

import com.hashem.opendictionary.feature.domain.repository.WordError
import retrofit2.Response
import java.io.IOException

interface NetworkErrorHandler {
    @Throws(WordError::class)
    suspend fun <T> handle(block: suspend () -> Response<T>): T
}

class DefaultNetworkErrorHandler : NetworkErrorHandler {

    @Throws(WordError::class)
    override suspend fun <T> handle(block: suspend () -> Response<T>): T {
        return try {
            val response = block()
            if (response.isSuccessful) {
                response.body() ?: throw WordError.ApiError
            } else {
                if (response.code() == 404)
                    throw WordError.NotFoundError
                else
                    throw WordError.ApiError
            }
        } catch (e: IOException) {
            throw WordError.NetworkError
        } catch (e: WordError) {
            throw e
        }
    }
}