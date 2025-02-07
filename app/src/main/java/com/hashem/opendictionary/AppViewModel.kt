package com.hashem.opendictionary

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hashem.opendictionary.common.database.OpenDictionaryDatabase
import com.hashem.opendictionary.common.network.OpenDictionaryNetwork
import com.hashem.opendictionary.feature.data.WordRepository
import com.hashem.opendictionary.feature.domain.GetRecentSearchWordsUseCase
import com.hashem.opendictionary.feature.domain.GetWordUseCase
import com.hashem.opendictionary.feature.domain.repository.WordResult
import com.hashem.opendictionary.feature.ui.AppUIState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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

    private val searchQuery: MutableStateFlow<String> = MutableStateFlow("")
    val uiState: StateFlow<AppUIState> = searchQuery.flatMapLatest { searchQuery ->
        if (searchQuery.isEmpty()) {
            getRecentSearchWordsUseCase()
        } else {
            getWordUseCase(searchQuery)
        }.map { result ->
            Log.e("getWords", result.toString())

            when (result) {
                is WordResult.Success -> {
                    AppUIState.Success(result.data.toString())
                }

                is WordResult.Fail -> {
                    AppUIState.Error(result.error.toString())
                }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AppUIState.Loading
    )

    fun search(query: String) {
        searchQuery.value = query
    }

//    init {
//
//        viewModelScope.launch {
//            getRecentSearchWordsUseCase().collect { result ->
//                Log.e("getRecentSearchWordsUseCase", result.toString())
//
//                when (result) {
//                    is WordResult.Success -> {
//                    }
//
//                    is WordResult.Fail -> {
//                    }
//                }
//            }
//
//            getWordUseCase("wwe").collect { result ->
//                Log.e("getWordUseCase", result.toString())
//
//                when (result) {
//                    is WordResult.Success -> {
//                    }
//
//                    is WordResult.Fail -> {
//                    }
//                }
//            }
//        }
//    }
}
