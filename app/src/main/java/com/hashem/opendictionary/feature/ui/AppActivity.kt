package com.hashem.opendictionary.feature.ui


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hashem.opendictionary.feature.ui.components.AppEmptyView
import com.hashem.opendictionary.feature.ui.components.AppErrorView
import com.hashem.opendictionary.feature.ui.components.AppListView
import com.hashem.opendictionary.feature.ui.components.AppLoadingView
import com.hashem.opendictionary.feature.ui.components.AppSearchBarView
import com.hashem.opendictionary.feature.ui.models.DefinitionUI
import com.hashem.opendictionary.feature.ui.models.MeaningUI
import com.hashem.opendictionary.feature.ui.models.WordUI
import com.hashem.opendictionary.feature.ui.screens.AppDetailsScreen
import com.hashem.opendictionary.theme.OpenDictionaryTheme
import com.hashem.opendictionary.utils.rememberAudioPlayerManager
import kotlinx.coroutines.launch


class AppActivity : ComponentActivity() {
    private val appViewModel by viewModels<AppViewModel>(factoryProducer = {
        AppViewModel.Factory
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OpenDictionaryTheme {
                val audioPlayerManager = rememberAudioPlayerManager()
                val snackBarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()

                val uiState by appViewModel.uiState.collectAsStateWithLifecycle()
                val searchResults by appViewModel.searchResults.collectAsStateWithLifecycle()
                val searchAction: (searchQuery: String) -> Unit = { appViewModel.search(it) }
                val showErrorAction: (error: String) -> Unit =
                    { scope.launch { snackBarHostState.showSnackbar(it) } }

                val playAction: (String) -> Unit = { audioPlayerManager.play(it) }

                Scaffold(
                    snackbarHost = { SnackbarHost(snackBarHostState) },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    App(
                        modifier = Modifier.padding(innerPadding),
                        uiState = uiState,
                        searchResults = searchResults,
                        searchAction = searchAction,
                        showErrorAction = showErrorAction,
                        playAction = playAction
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OpenDictionaryAppPreview() {
    OpenDictionaryTheme {
        App(
            uiState = AppUIState.Success,
            searchResults = List(5) {
                WordUI(
                    word = "Hello",
                    phoneticText = "/həˈləʊ/",
                    phoneticAudio = "audio",
                    meanings = mapOf(
                        "exclamation" to
                                MeaningUI(
                                    definitions = listOf(
                                        DefinitionUI(
                                            definition = "used as a greeting or to begin a conversation",
                                            example = "hello there, Katie!"
                                        )
                                    ),
                                    synonyms = setOf("greeting", "welcome", "salutation"),
                                    antonyms = setOf("goodbye")
                                )
                    )
                )
            }
        )
    }
}

@Composable
fun App(
    modifier: Modifier = Modifier,
    uiState: AppUIState = AppUIState.Loading,
    searchResults: List<WordUI> = emptyList(),
    searchAction: (searchQuery: String) -> Unit = {},
    showErrorAction: (error: String) -> Unit = {},
    playAction: (String) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var showRecentSearch by remember { mutableStateOf(true) }
    var selectedWord: WordUI? by remember { mutableStateOf(null) }

    val detailsAction: (WordUI) -> Unit = {
        selectedWord = it
    }

    Surface(
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                modifier = Modifier.padding(bottom = 16.dp),
                text = "Open Dictionary.",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
            )

            AppSearchBarView(
                modifier = Modifier.padding(bottom = 16.dp),
                searchQuery,
                onSearchQueryChanged = { searchQuery = it },
                onSearchClicked = {
                    showRecentSearch = it.isEmpty()
                    searchAction(it)
                },
            )

            if (showRecentSearch) {
                Text(
                    modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
                    text = "Recent searches",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
            }

            when (uiState) {
                AppUIState.Loading -> {
                    AppLoadingView()
                }

                AppUIState.Success -> {
                    if (searchResults.isEmpty()) {
                        AppEmptyView()
                    } else {
                        AppListView(
                            searchResults,
                            detailsAction,
                            playAction
                        )
                    }
                }

                is AppUIState.Error -> {
                    if (uiState.message == "Network Error" && searchResults.isNotEmpty()) {
                        showErrorAction("Network Error")

                        AppListView(
                            searchResults,
                            detailsAction,
                            playAction
                        )
                    } else if (uiState.message == "Not Found") {
                        AppEmptyView()
                    } else {
                        AppErrorView(uiState.message)
                    }
                }
            }
        }

        selectedWord?.let {
            AppDetailsScreen(
                word = it,
                playAction = playAction,
                onDismiss = { selectedWord = null },
            )
        }
    }
}

