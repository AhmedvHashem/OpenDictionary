package com.hashem.opendictionary.domain

import com.hashem.opendictionary.feature.domain.GetRecentSearchWordsUseCase
import com.hashem.opendictionary.feature.domain.repository.WordError
import com.hashem.opendictionary.feature.domain.repository.WordRepository
import com.hashem.opendictionary.feature.domain.repository.WordResult
import com.hashem.opendictionary.fixtures.WordFixture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetRecentSearchWordsUseCaseTest {

    private lateinit var repository: WordRepository
    private lateinit var getRecentSearchWordsUseCase: GetRecentSearchWordsUseCase

    @BeforeTest
    fun setUp() {
        repository = mock()
        getRecentSearchWordsUseCase = GetRecentSearchWordsUseCase(repository, Dispatchers.Unconfined)
    }

    @Test
    fun `invoke should return recent search words from repository`() = runTest {
        val expectedWords = listOf(WordFixture.createWord())
        whenever(repository.getRecentSearchWords()).thenReturn(flowOf(WordResult.Success(expectedWords)))

        val resultFlow = getRecentSearchWordsUseCase()

        resultFlow.collect { result ->
            assertEquals(WordResult.Success(expectedWords), result)
        }
    }

    @Test
    fun `invoke should return empty list if no recent search words`() = runTest {
        whenever(repository.getRecentSearchWords()).thenReturn(flowOf(WordResult.Success(emptyList())))

        val resultFlow = getRecentSearchWordsUseCase()

        resultFlow.collect { result ->
            assertEquals(WordResult.Success(emptyList()), result)
        }
    }

    @Test
    fun `invoke should return UnknownError for custom error`() = runTest {
        val exceptionMessage = "Custom error"
        whenever(repository.getRecentSearchWords()).thenReturn(
            flowOf(
                WordResult.Fail(
                    WordError.UnknownError(
                        exceptionMessage
                    )
                )
            )
        )

        val resultFlow = getRecentSearchWordsUseCase()

        resultFlow.collect { result ->
            assertEquals(WordResult.Fail(WordError.UnknownError(exceptionMessage)), result)
        }
    }
}