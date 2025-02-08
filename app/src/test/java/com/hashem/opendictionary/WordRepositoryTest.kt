//package com.hashem.opendictionary
//
//import com.hashem.opendictionary.feature.data.WordRepository
//import com.hashem.opendictionary.feature.data.cache.WordCacheDataSource
//import com.hashem.opendictionary.feature.data.cache.models.DefinitionCache
//import com.hashem.opendictionary.feature.data.cache.models.MeaningCache
//import com.hashem.opendictionary.feature.data.cache.models.PhoneticCache
//import com.hashem.opendictionary.feature.data.cache.models.WordCache
//import com.hashem.opendictionary.feature.data.remote.WordRemoteDataSource
//import com.hashem.opendictionary.feature.data.remote.models.DefinitionRemote
//import com.hashem.opendictionary.feature.data.remote.models.MeaningRemote
//import com.hashem.opendictionary.feature.data.remote.models.WordRemote
//import com.hashem.opendictionary.feature.domain.models.Word
//import com.hashem.opendictionary.feature.domain.repository.WordError
//import com.hashem.opendictionary.feature.domain.repository.WordResult
//import kotlinx.coroutines.test.runTest
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.ResponseBody
//import org.junit.Before
//import org.junit.Test
//import org.mockito.Mockito.mock
//import org.mockito.kotlin.whenever
//import retrofit2.Response
//
//
//class WordRepositoryTest {
//
//    private lateinit var repository: WordRepository
//    private val remote: WordRemoteDataSource = mock()
//    private val cache: WordCacheDataSource = mock()
//    private val word = "test"
//    private val phoneticCache = PhoneticCache("test", "testing Audio")
//    private val definitionCache = DefinitionCache("test", "testing")
//    private val meaning = MeaningCache(listOf(definitionCache), setOf("test"), setOf("test"))
//    private val wordCache = WordCache(word, phoneticCache, mapOf("test" to meaning))
//    val wordList = listOf(wordCache)
//
//
//    @Before
//    fun setUp() {
//        repository = WordRepository(remote, cache)
//    }
//
//    @Test
//    fun `getWord should return cached word if available`(): Unit = runTest {
//
//        val wordList = listOf(wordCache)
//        val expectedWord =
//            Word(word, phoneticCache.toPhonetic(), mapOf("test" to meaning.toMeaning()))
//
//        whenever(cache.getWord(word)).thenReturn(wordList)
//
//        val resultFlow = repository.getWord(word)
//
//        // Assert
//        resultFlow.collect { result ->
//            assertThat(result).isInstanceOf(WordResult.Success::class.java)
//            val successResult = result as WordResult.Success<List<Word>>
//            assertThat(successResult.data).hasSize(1)
//            assertThat(successResult.data[0].word).isEqualTo(expectedWord.word)
//            assertThat(successResult.data[0]).isEqualTo(expectedWord)
//        }
//    }
//
//    @Test
//    fun `getWord should fetch from remote if cache is empty`() = runTest {
//        val expectedWord = wordCache.toWord()
//
//        whenever(cache.getWord(word)).thenReturn(emptyList())
//        whenever(remote.getWords(word)).thenReturn(
//            mockWordRemoteResponse(word, definitionCache.definition)
//        )
//        repository.getWord(word).collect { result ->
//            assertThat(result).isInstanceOf(WordResult.Success::class.java)
//            val successResult = result as WordResult.Success
//            assertThat(successResult.data).hasSize(1)
//            assertThat(successResult.data[1]).isEqualTo(expectedWord)
//        }
//    }
//
//
//    @Test
//    fun `getWord should return NotFoundError if word is not found`() = runTest {
//        whenever(cache.getWord(word)).thenReturn(emptyList())
//        whenever(remote.getWords(word)).thenReturn(mockNotFoundResponse())
//
//        repository.getWord(word).collect { result ->
//            assertThat(result).isInstanceOf(WordResult.Fail::class.java)
//            val errorResult = result as WordResult.Fail
//            assertThat(errorResult.error).isInstanceOf(WordError.NotFoundError::class.java)
//        }
//    }
//
//    @Test
//    fun `getWord should return ApiError on server error`() = runTest {
//        // Arrange
//        whenever(cache.getWord(word)).thenReturn(emptyList())
//        whenever(remote.getWords(word)).thenReturn(mockServerErrorResponse())
//
//        // Act & Assert
//        repository.getWord(word).collect { result ->
//            assertThat(result).isInstanceOf(WordResult.Fail::class.java)
//            val errorResult = result as WordResult.Fail
//            assertThat(errorResult.error).isInstanceOf(WordError.ApiError::class.java)
//        }
//    }
//
//    @Test
//    fun `getWord should return UnknownError for custom error`() = runTest {
//        // Arrange
//        whenever(cache.getWord(word)).thenReturn(emptyList())
//        whenever(remote.getWords(word)).thenReturn(mockErrorResponse(403, "Forbidden"))
//
//        // Act & Assert
//        repository.getWord(word).collect { result ->
//            assertThat(result).isInstanceOf(WordResult.Fail::class.java)
//            val errorResult = result as WordResult.Fail
//            assertThat(errorResult.error).isInstanceOf(WordError.UnknownError::class.java)
//        }
//    }
//
//
//    @Test
//    fun `getRecentSearchWords - success`(): Unit = runTest {
//        whenever(cache.getWords()).thenReturn(wordList)
//
//        val resultFlow = repository.getRecentSearchWords()
//        resultFlow.collect { result ->
//            assertThat(result).isInstanceOf(WordResult.Success::class.java)
//            val successResult = result as WordResult.Success
//            assertThat(successResult.data).hasSize(2)
//            assertThat(successResult.data[0].word).isEqualTo("word1")
//        }
//    }
//
//    @Test
//    fun `getWord - from remote - unknown error`() = runTest {
//        val word = "test"
//        val exceptionMessage = "Unknown Exception"
//
//        whenever(cache.getWord(word)).thenReturn(emptyList())
//        whenever(remote.getWords(word)).thenThrow(Exception(exceptionMessage))
//
//        val resultFlow = repository.getWord(word)
//        resultFlow.collect { result ->
//            assertThat(result).isInstanceOf(WordResult.Fail::class.java)
//            val errorResult = result as WordResult.Fail
//            assertThat(errorResult.error).isInstanceOf(WordError.UnknownError::class.java)
//            val unknownError = errorResult.error as WordError.UnknownError
//            assertThat(unknownError.message).contains(exceptionMessage)
//        }
//    }
//
//
//    // Success response with a list of WordRemote objects
//    private fun mockWordRemoteResponse(
//        word: String,
//        definition: String
//    ): Response<List<WordRemote>> {
//        return Response.success(
//            listOf(
//                WordRemote(
//                    word = word,
//                    meanings = listOf(
//                        MeaningRemote(
//                            partOfSpeech = "noun",
//                            definitions = listOf(
//                                DefinitionRemote(definition = definition)
//                            )
//                        )
//                    )
//                )
//            )
//        )
//    }
//
//    // Mock 404 Not Found response
//    private fun mockNotFoundResponse(): Response<List<WordRemote>> {
//        return Response.error(
//            404,
//            ResponseBody.create("application/json".toMediaTypeOrNull(), "{\"error\":\"Not Found\"}")
//        )
//    }
//
//    // Mock 500 Internal Server Error response
//    private fun mockServerErrorResponse(): Response<List<WordRemote>> {
//        return Response.error(
//            500,
//            ResponseBody.create(
//                "application/json".toMediaTypeOrNull(),
//                "{\"error\":\"Internal Server Error\"}"
//            )
//        )
//    }
//
//    // Mock generic error response with a specific code and message
//    private fun mockErrorResponse(code: Int, message: String): Response<List<WordRemote>> {
//        return Response.error(
//            code,
//            ResponseBody.create("application/json".toMediaTypeOrNull(), "{\"error\":\"$message\"}")
//        )
//    }
//}