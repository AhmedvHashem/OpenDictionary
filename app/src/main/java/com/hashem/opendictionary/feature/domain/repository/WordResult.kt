package com.hashem.opendictionary.feature.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val error: String) : Result<Nothing>
}

fun <T> Flow<T>.asResultFlow(): Flow<Result<T>> = map<T, Result<T>> { Result.Success(it) }
    .catch { emit(Result.Error(it.message ?: "Result: Unknown Error")) }

