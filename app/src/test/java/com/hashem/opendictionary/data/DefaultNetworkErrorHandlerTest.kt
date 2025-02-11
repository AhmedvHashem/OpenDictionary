package com.hashem.opendictionary.data

import com.hashem.opendictionary.feature.data.remote.DefaultNetworkErrorHandler
import com.hashem.opendictionary.feature.domain.repository.WordError
import com.hashem.opendictionary.fixtures.WordRemoteFixture
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class DefaultNetworkErrorHandlerTest {

    private val networkErrorHandler = DefaultNetworkErrorHandler()

    @Test
    fun `handle should return response body if response is successful`() = runBlocking {
        val expectedResponse = WordRemoteFixture.createWordRemote()
        val response = WordRemoteFixture.mockSuccessResponse()

        val result = networkErrorHandler.handle { response }

        assertEquals(listOf(expectedResponse), result)
    }

    @Test
    fun `handle should throw ApiError if response is successful and body is null`() = runBlocking {
        val response = WordRemoteFixture.mockSuccessNullResponse()

        val error = assertFails {
            networkErrorHandler.handle { response }
            return@assertFails
        }

        assertEquals(WordError.ApiError::class.java, error::class.java)
    }

}