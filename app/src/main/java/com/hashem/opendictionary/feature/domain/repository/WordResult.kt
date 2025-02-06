package com.hashem.opendictionary.feature.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

sealed interface WordResult<out T> {
    data class Success<T>(val data: T) : WordResult<T>
    data class Fail(val error: WordError) : WordResult<Nothing>
}

fun <T> Flow<T>.asWordResultFlow(): Flow<WordResult<T>> =
    map<T, WordResult<T>> { WordResult.Success(it) }
        .catch {
            if (it is WordError) {
                emit(WordResult.Fail(it))
            } else
                emit(WordResult.Fail(WordError.UnknownError(it.message ?: "Fail: Unknown Fail")))
        }

