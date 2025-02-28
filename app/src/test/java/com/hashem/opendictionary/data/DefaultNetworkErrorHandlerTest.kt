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


//    @Test
//    fun `handle should throw ApiError if response is not successful`() = runBlocking {
//        val response = mock(Response::class.java) as Response<String>
//        `when`(response.isSuccessful).thenReturn(false)
//        `when`(response.code()).thenReturn(500)
//
//        val exception = Assert.assertThrows(WordError.ApiError::class.java) {
//            runBlocking { networkErrorHandler.handle { response } }
//        }
//
//        assertEquals(WordError.ApiError::class.java, exception::class.java)
//    }

//    @Test
//    fun `handle should throw NotFoundError if response code is 404`() = runBlocking {
//        val response = mock(Response::class.java) as Response<String>
//        `when`(response.isSuccessful).thenReturn(false)
//        `when`(response.code()).thenReturn(404)
//
//        val exception = assertThrows(WordError.NotFoundError::class.java) {
//            runBlocking { networkErrorHandler.handle { response } }
//        }
//
//        assertEquals(WordError.NotFoundError::class.java, exception::class.java)
//    }
//
//    @Test
//    fun `handle should throw NetworkError if IOException is thrown`() = runBlocking {
//        val block: suspend () -> Response<String> = mock()
//        `when`(block.invoke()).thenThrow(IOException::class.java)
//
//        val exception = assertThrows(WordError.NetworkError::class.java) {
//            runBlocking { networkErrorHandler.handle(block) }
//        }
//
//        assertEquals(WordError.NetworkError::class.java, exception::class.java)
//    }
//
//    @Test
//    fun `handle should rethrow WordError if it is thrown`() = runBlocking {
//        val block: suspend () -> Response<String> = mock()
//        `when`(block.invoke()).thenThrow(WordError.ApiError::class.java)
//
//        val exception = assertThrows(WordError.ApiError::class.java) {
//            runBlocking { networkErrorHandler.handle(block) }
//        }
//
//        assertEquals(WordError.ApiError::class.java, exception::class.java)
//    }

}