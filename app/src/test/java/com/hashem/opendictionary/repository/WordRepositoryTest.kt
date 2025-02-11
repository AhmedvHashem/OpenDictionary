package com.hashem.opendictionary.repository

import com.hashem.opendictionary.feature.data.WordRepository
import com.hashem.opendictionary.feature.data.cache.WordCacheDataSource
import com.hashem.opendictionary.feature.data.remote.WordRemoteDataSource
import com.hashem.opendictionary.feature.domain.models.Word
import com.hashem.opendictionary.feature.domain.repository.WordError
import com.hashem.opendictionary.feature.domain.repository.WordResult
import com.hashem.opendictionary.fixtures.WordCacheFixture
import com.hashem.opendictionary.fixtures.WordFixture
import com.hashem.opendictionary.fixtures.WordRemoteFixture
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class WordRepositoryTest {

    private val remote: WordRemoteDataSource = mock()
    private val cache: WordCacheDataSource = mock()
    private lateinit var repository: WordRepository

    @BeforeTest
    fun setUp() {
        repository = WordRepository(remote, cache)
    }

    @Test
    fun `getRecentSearchWords should return recent search words from cache only`() = runTest {
        val expectedWords = listOf(WordFixture.createWord(word = "cache-word1"))

        whenever(cache.getWords()).thenReturn(listOf(WordCacheFixture.createWordCache(word = "cache-word1")))

        // single verifies that the flow emits only once
        val result = repository.getRecentSearchWords().single()

        verify(cache).getWords()
        assertEquals(WordResult.Success(expectedWords), result)
    }

    @Test
    fun `getRecentSearchWords should return empty list if no recent search words in cache`() =
        runTest {
            val expectedWords = emptyList<Word>()

            whenever(cache.getWords()).thenReturn(emptyList())

            // single verifies that the flow emits only once
            val result = repository.getRecentSearchWords().single()

            verify(cache).getWords()
            assertEquals(WordResult.Success(expectedWords), result)
        }

    @Test
    fun `getRecentSearchWords should return error if exception has been thrown`() = runTest {
        whenever(cache.getWords()).thenThrow(RuntimeException())

        // single verifies that the flow emits only once
        val result = repository.getRecentSearchWords().single()

        verify(cache).getWords()
        assertTrue(result is WordResult.Fail)
    }

    @Test
    fun `getWord should return from cache if available then from remote`() = runTest {
        val word = "word"
        val expectedResult = listOf<WordResult<List<Word>>>(
            WordResult.Success(listOf(WordFixture.createWord("cache-word"))),
            WordResult.Success(listOf(WordFixture.createWord("remote-word")))
        )

        whenever(cache.getWords(word)).thenReturn(listOf(WordCacheFixture.createWordCache("cache-word")))
        whenever(remote.getWords(word)).thenReturn(WordRemoteFixture.mockSuccessResponse("remote-word"))

        val result = repository.getWord(word).toList()

        verify(cache).getWords(word)
        verify(cache).insertWord(WordCacheFixture.createWordCache("remote-word"))
        verify(remote).getWords(word)
        assertEquals(expectedResult, result)
    }

    @Test
    fun `getWord should fetch from remote if cache is empty`() = runTest {
        val word = "remote-word1"
        val expectedWords = listOf(WordFixture.createWord(word))

        whenever(cache.getWords(word)).thenReturn(emptyList())
        whenever(remote.getWords(word)).thenReturn(WordRemoteFixture.mockSuccessResponse(word))

        val result = repository.getWord(word).single()

        verify(cache).getWords(word)
        verify(remote).getWords(word)
        assertEquals(WordResult.Success(expectedWords), result)
    }

    @Test
    fun `getWord should return error if exception has been thrown`() = runTest {
        val word = "word"
        whenever(cache.getWords(word)).thenReturn(emptyList())
        whenever(remote.getWords(word)).thenThrow(RuntimeException())

        val result = repository.getWord(word).single()

        verify(cache, never()).insertWord(WordCacheFixture.createWordCache("remote-word"))
        assertTrue(result is WordResult.Fail)

        val resultCount = repository.getWord(word).count()
        assertEquals(1, resultCount)
    }

    //extra tests
    @Test
    fun `getWord should return NotFoundError if word is not found`() = runTest {
        val word = "word"
        whenever(cache.getWords(word)).thenReturn(emptyList())
        whenever(remote.getWords(word)).thenReturn(WordRemoteFixture.mockNotFoundResponse())

        val result = repository.getWord(word).single()
        assertEquals(WordResult.Fail(WordError.NotFoundError), result)
    }

    @Test
    fun `getWord should return ApiError on server error`() = runTest {
        val word = "word"
        whenever(cache.getWords(word)).thenReturn(emptyList())
        whenever(remote.getWords(word)).thenReturn(WordRemoteFixture.mockServerErrorResponse())

        val result = repository.getWord(word).single()
        assertEquals(WordResult.Fail(WordError.ApiError), result)
    }
}