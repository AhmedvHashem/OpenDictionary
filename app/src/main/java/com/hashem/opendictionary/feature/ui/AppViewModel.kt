package com.hashem.opendictionary.feature.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hashem.opendictionary.feature.data.WordRepository
import com.hashem.opendictionary.feature.domain.GetRecentSearchWordsUseCase
import com.hashem.opendictionary.feature.domain.GetWordUseCase
import com.hashem.opendictionary.feature.domain.repository.WordError
import com.hashem.opendictionary.feature.domain.repository.WordResult
import com.hashem.opendictionary.feature.ui.models.WordUI
import com.hashem.opendictionary.feature.ui.models.toWordUI
import com.hashem.opendictionary.framework.database.OpenDictionaryDatabase
import com.hashem.opendictionary.framework.network.OpenDictionaryNetwork
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModel(
    private val getRecentSearchWordsUseCase: GetRecentSearchWordsUseCase,
    private val getWordUseCase: GetWordUseCase
) : ViewModel() {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val context =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application

                val network = OpenDictionaryNetwork.getInstance()
                val db = OpenDictionaryDatabase.getInstance(context)

                val remote = network.dataSource()
                val cache = db.dataSource()
                val repo = WordRepository(remote, cache)

                AppViewModel(
                    GetRecentSearchWordsUseCase(repo),
                    GetWordUseCase(repo),
                )
            }
        }
    }

    val uiState: MutableStateFlow<AppUIState> = MutableStateFlow(AppUIState.Loading)

    private val searchQuery: MutableStateFlow<String> = MutableStateFlow("")
    val searchResults: StateFlow<List<WordUI>> = searchQuery.flatMapLatest { query ->
        if (query.isEmpty()) {
            getRecentSearchWordsUseCase()
        } else {
            getWordUseCase(query)
        }.map { result ->
            when (result) {
                is WordResult.Success -> {
                    uiState.value = AppUIState.Success

                    result.data.map { it.toWordUI() }
                }

                is WordResult.Fail -> {
                    uiState.value = when (result.error) {
                        is WordError.NetworkError -> AppUIState.Error("Network Error")
                        is WordError.NotFoundError -> AppUIState.Error("Not Found")
                        is WordError.ApiError -> AppUIState.Error("Api Error")
                        is WordError.UnknownError -> AppUIState.Error("Unknown Error")
                    }

                    emptyList()
                }
            }
        }.filterNot { it.isEmpty() }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun search(query: String) {
        if (query == searchQuery.value)
            return

        uiState.value = AppUIState.Loading
        searchQuery.value = query
    }
}
