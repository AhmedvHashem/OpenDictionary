package com.hashem.opendictionary.usecases

import com.hashem.opendictionary.feature.domain.GetWordUseCase
import com.hashem.opendictionary.feature.domain.models.Definition
import com.hashem.opendictionary.feature.domain.models.Meaning
import com.hashem.opendictionary.feature.domain.models.Phonetic
import com.hashem.opendictionary.feature.domain.models.Word
import com.hashem.opendictionary.feature.domain.repository.WordError
import com.hashem.opendictionary.feature.domain.repository.WordRepository
import com.hashem.opendictionary.feature.domain.repository.WordResult
import com.hashem.opendictionary.fixtures.WordFixture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

class GetWordUseCaseTest {

    private lateinit var repository: WordRepository
    private lateinit var getWordUseCase: GetWordUseCase

    @Before
    fun setUp() {
        repository = mock()
        getWordUseCase = GetWordUseCase(repository, Dispatchers.Unconfined)
    }

    @Test
    fun `invoke should return word from repository`() = runTest {
        val word = "example"
        val expectedWord = listOf(WordFixture.createWord())
        whenever(repository.getWord(word)).thenReturn(flowOf(WordResult.Success(expectedWord)))

        val resultFlow = getWordUseCase(word)

        resultFlow.collect { result ->
            assertEquals(WordResult.Success(expectedWord), result)
        }
    }

    @Test
    fun `invoke should return NotFoundError if word is not found`() = runTest {
        val word = "nonexistent"
        whenever(repository.getWord(word)).thenReturn(flowOf(WordResult.Fail(WordError.NotFoundError)))

        val resultFlow = getWordUseCase(word)

        resultFlow.collect { result ->
            assertEquals(WordResult.Fail(WordError.NotFoundError), result)
        }
    }

    @Test
    fun `invoke should return ApiError on server error`() = runTest {
        val word = "example"
        whenever(repository.getWord(word)).thenReturn(flowOf(WordResult.Fail(WordError.ApiError)))

        val resultFlow = getWordUseCase(word)

        resultFlow.collect { result ->
            assertEquals(WordResult.Fail(WordError.ApiError), result)
        }
    }

    @Test
    fun `invoke should return UnknownError for custom error`() = runTest {
        val word = "example"
        val exceptionMessage = "Unknown Exception"
        whenever(repository.getWord(word)).thenReturn(
            flowOf(
                WordResult.Fail(
                    WordError.UnknownError(
                        exceptionMessage
                    )
                )
            )
        )

        val resultFlow = getWordUseCase(word)

        resultFlow.collect { result ->
            assertEquals(WordResult.Fail(WordError.UnknownError(exceptionMessage)), result)
        }
    }
}