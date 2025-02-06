package com.hashem.opendictionary.feature.domain

import com.hashem.opendictionary.feature.domain.repository.WordRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

class GetRecentSearchWordsUseCase(
    private val repository: WordRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    operator fun invoke() = repository.getRecentSearchWords()
        .flowOn(dispatcher)
}