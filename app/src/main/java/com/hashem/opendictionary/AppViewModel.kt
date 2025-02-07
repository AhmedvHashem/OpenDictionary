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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
                    GetRecentSearchWordsUseCase(repo, Dispatchers.IO),
                    GetWordUseCase(repo, Dispatchers.IO),
                )
            }
        }
    }

    init {
        viewModelScope.launch {
            getRecentSearchWordsUseCase().collect { result ->
                Log.e("getRecentSearchWordsUseCase", result.toString())

                when (result) {
                    is WordResult.Success -> {
                    }

                    is WordResult.Fail -> {
                    }
                }
            }

            getWordUseCase("but").collect { result ->
                Log.e("getWordUseCase", result.toString())

                when (result) {
                    is WordResult.Success -> {
                    }

                    is WordResult.Fail -> {
                    }
                }
            }
        }
    }

    fun Hi() {

    }
}
