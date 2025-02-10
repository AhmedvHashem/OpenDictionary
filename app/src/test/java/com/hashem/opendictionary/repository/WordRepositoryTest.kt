package com.hashem.opendictionary.repository

import com.google.common.truth.Truth.assertThat
import com.hashem.opendictionary.feature.data.WordRepository
import com.hashem.opendictionary.feature.data.cache.WordCacheDataSource
import com.hashem.opendictionary.feature.data.cache.models.DefinitionCache
import com.hashem.opendictionary.feature.data.cache.models.MeaningCache
import com.hashem.opendictionary.feature.data.cache.models.PhoneticCache
import com.hashem.opendictionary.feature.data.cache.models.WordCache
import com.hashem.opendictionary.feature.data.remote.WordRemoteDataSource
import com.hashem.opendictionary.feature.data.remote.models.DefinitionRemote
import com.hashem.opendictionary.feature.data.remote.models.MeaningRemote
import com.hashem.opendictionary.feature.data.remote.models.WordRemote
import com.hashem.opendictionary.feature.domain.models.Word
import com.hashem.opendictionary.feature.domain.repository.WordError
import com.hashem.opendictionary.feature.domain.repository.WordResult
import com.hashem.opendictionary.fixtures.WordCacheFixture
import com.hashem.opendictionary.fixtures.WordFixture
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import retrofit2.Response
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals


class WordRepositoryTest {

    private val remote: WordRemoteDataSource = mock()
    private val cache: WordCacheDataSource = mock()
    private lateinit var repository: WordRepository

    private val word = "test"
    private val phoneticCache = PhoneticCache("test", "testing Audio")
    private val definitionCache = DefinitionCache("test", "testing")
    private val meaning = MeaningCache(listOf(definitionCache), setOf("test"), setOf("test"))
    private val wordCache = WordCache(word, phoneticCache, mapOf("test" to meaning))


    @BeforeTest
    fun setUp() {
        repository = WordRepository(remote, cache)
    }

    @Test
    fun `invoke should return recent search words from cache only`(): Unit = runTest {
        val expectedWords = listOf(WordFixture.createWord(word = "cache-word1"))

        whenever(cache.getWords()).thenReturn(listOf(WordCacheFixture.createWordCache(word = "cache-word1")))

        // single verifies that the flow emits only once
        val result = repository.getRecentSearchWords().single()
        assertEquals(WordResult.Success(expectedWords), result)
    }

    @Test
    fun `invoke should return empty list if no recent search words in cache`(): Unit = runTest {
        val expectedWords = emptyList<Word>()

        whenever(cache.getWords()).thenReturn(emptyList())

        // single verifies that the flow emits only once
        val result = repository.getRecentSearchWords().single()
        assertEquals(WordResult.Success(expectedWords), result)
    }

    @Test(expected = Exception::class)
    fun `invoke should return error if exception has been thrown`(): Unit = runTest {
        val exceptionMessage = "error"

        whenever(cache.getWords()).thenThrow(Exception(exceptionMessage))

        // single verifies that the flow emits only once
        val result = repository.getRecentSearchWords().single()
        assertEquals(WordResult.Fail(WordError.UnknownError(exceptionMessage)), result)
    }

    @Test
    fun `getWord should return cached word if available`(): Unit = runTest {

        val wordList = listOf(wordCache)
        val expectedWord =
            Word(word, phoneticCache.toPhonetic(), mapOf("test" to meaning.toMeaning()))

        whenever(cache.getWord(word)).thenReturn(wordList)

        val resultFlow = repository.getWord(word)

        // Assert
        resultFlow.collect { result ->
            assertThat(result).isInstanceOf(WordResult.Success::class.java)
            val successResult = result as WordResult.Success<List<Word>>
            assertThat(successResult.data).hasSize(1)
            assertThat(successResult.data[0].word).isEqualTo(expectedWord.word)
            assertThat(successResult.data[0]).isEqualTo(expectedWord)
        }
    }

    @Test
    fun `getWord should fetch from remote if cache is empty`() = runTest {
        val expectedWord = wordCache.toWord()

        whenever(cache.getWord(word)).thenReturn(emptyList())
        whenever(remote.getWords(word)).thenReturn(
            mockWordRemoteResponse(word, definitionCache.definition)
        )
        repository.getWord(word).collect { result ->
            assertThat(result).isInstanceOf(WordResult.Success::class.java)
            val successResult = result as WordResult.Success
            assertThat(successResult.data).hasSize(1)
            assertThat(successResult.data[1]).isEqualTo(expectedWord)
        }
    }


    @Test
    fun `getWord should return NotFoundError if word is not found`() = runTest {
        whenever(cache.getWord(word)).thenReturn(emptyList())
        whenever(remote.getWords(word)).thenReturn(mockNotFoundResponse())

        repository.getWord(word).collect { result ->
            assertThat(result).isInstanceOf(WordResult.Fail::class.java)
            val errorResult = result as WordResult.Fail
            assertThat(errorResult.error).isInstanceOf(WordError.NotFoundError::class.java)
        }
    }

    @Test
    fun `getWord should return ApiError on server error`() = runTest {
        // Arrange
        whenever(cache.getWord(word)).thenReturn(emptyList())
        whenever(remote.getWords(word)).thenReturn(mockServerErrorResponse())

        // Act & Assert
        repository.getWord(word).collect { result ->
            assertThat(result).isInstanceOf(WordResult.Fail::class.java)
            val errorResult = result as WordResult.Fail
            assertThat(errorResult.error).isInstanceOf(WordError.ApiError::class.java)
        }
    }

    @Test
    fun `getWord should return UnknownError for custom error`() = runTest {
        // Arrange
        whenever(cache.getWord(word)).thenReturn(emptyList())
        whenever(remote.getWords(word)).thenReturn(mockErrorResponse(403, "Forbidden"))

        // Act & Assert
        repository.getWord(word).collect { result ->
            assertThat(result).isInstanceOf(WordResult.Fail::class.java)
            val errorResult = result as WordResult.Fail
            assertThat(errorResult.error).isInstanceOf(WordError.UnknownError::class.java)
        }
    }


    // Success response with a list of WordRemote objects
    private fun mockWordRemoteResponse(
        word: String,
        definition: String
    ): Response<List<WordRemote>> {
        return Response.success(
            listOf(
                WordRemote(
                    word = word,
                    meanings = listOf(
                        MeaningRemote(
                            partOfSpeech = "noun",
                            definitions = listOf(
                                DefinitionRemote(definition = definition)
                            )
                        )
                    )
                )
            )
        )
    }

    // Mock 404 Not Found response
    private fun mockNotFoundResponse(): Response<List<WordRemote>> {
        return Response.error(
            404,
            ResponseBody.create("application/json".toMediaTypeOrNull(), "{\"error\":\"Not Found\"}")
        )
    }

    // Mock 500 Internal Server Error response
    private fun mockServerErrorResponse(): Response<List<WordRemote>> {
        return Response.error(
            500,
            ResponseBody.create(
                "application/json".toMediaTypeOrNull(),
                "{\"error\":\"Internal Server Error\"}"
            )
        )
    }

    // Mock generic error response with a specific code and message
    private fun mockErrorResponse(code: Int, message: String): Response<List<WordRemote>> {
        return Response.error(
            code,
            ResponseBody.create("application/json".toMediaTypeOrNull(), "{\"error\":\"$message\"}")
        )
    }
}