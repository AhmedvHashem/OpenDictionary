package com.hashem.opendictionary

import com.hashem.opendictionary.feature.domain.repository.WordError
import com.hashem.opendictionary.feature.domain.repository.WordResult
import com.hashem.opendictionary.feature.domain.repository.asWordResultFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test


class WordResultFlowTest {

    @Test
    fun `asWordResultFlow should emit Success when flow emits value`() = runTest {
        val flow = flowOf("test").asWordResultFlow()

        flow.collect { result ->
            assertEquals(WordResult.Success("test"), result)
        }
    }

    @Test
    fun `asWordResultFlow should emit Fail with WordError when flow throws WordError`() = runTest {
        val flow = flow<String> { throw WordError.NotFoundError }.asWordResultFlow()

        flow.collect { result ->
            assertEquals(WordResult.Fail(WordError.NotFoundError), result)
        }
    }

    @Test
    fun `asWordResultFlow should emit Fail with UnknownError when flow throws unknown exception`() =
        runTest {
            val flow = flow<String> { throw Exception("Unknown exception") }.asWordResultFlow()

            flow.collect { result ->
                assertEquals(WordResult.Fail(WordError.UnknownError("Unknown exception")), result)
            }
        }

}