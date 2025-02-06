package com.hashem.opendictionary.feature.domain.repository

sealed class WordError : Exception() {
    data object NotFoundError : WordError()
    data object ApiError : WordError()
    data object NetworkError : WordError()
    data class UnknownError(override val message: String) : WordError()
}