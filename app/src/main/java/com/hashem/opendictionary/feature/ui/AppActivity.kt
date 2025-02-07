package com.hashem.opendictionary.feature.ui


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hashem.opendictionary.feature.ui.models.WordUI
import com.hashem.opendictionary.theme.OpenDictionaryTheme


class AppActivity : ComponentActivity() {
    private val appViewModel by viewModels<AppViewModel>(factoryProducer = {
        AppViewModel.Factory
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OpenDictionaryTheme {
                val snackBarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()

                val uiState by appViewModel.uiState.collectAsStateWithLifecycle()
                val searchResults by appViewModel.searchResults.collectAsStateWithLifecycle()
                val searchAction: (searchQuery: String) -> Unit = { appViewModel.search(it) }

                Scaffold(
                    snackbarHost = { SnackbarHost(snackBarHostState) },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
//                    scope.launch { snackBarHostState.showSnackbar("Hi SnackBar") }
                    App(
                        modifier = Modifier.padding(innerPadding),
                        uiState = uiState,
                        searchResults = searchResults,
                        searchAction = searchAction
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
        App()
    }
}

@Composable
fun App(
    modifier: Modifier = Modifier,
    uiState: AppUIState = AppUIState.Loading,
    searchResults: List<WordUI> = emptyList(),
    searchAction: (searchQuery: String) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var showRecentSearch by remember { mutableStateOf(true) }

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

            AppSearchBar(
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
                    Text(
                        modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
                        text = "Loading...",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    )
                }

                AppUIState.Success -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(searchResults) { word ->
                            AppCard(word)
                        }
                    }
                }

                is AppUIState.Error -> {
                    Text(
                        modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
                        text = uiState.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

//            val entries = List(5) {
//                DictionaryEntry(
//                    "Dog",
//                    "The dog (Canis familiaris when considered a distinct species " +
//                            "or Canis lupus familiaris when considered a subspecies of the " +
//                            "wolf)[5] is a domesticated carnivore of the family Canidae."
//                )
//            }
//            LazyColumn(
//                verticalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                items(entries) { entry ->
//                    AppCard(entry)
//                }
//            }
        }
    }
}

@Composable
fun AppSearchBar(
    modifier: Modifier = Modifier,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onSearchClicked: (String) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        TextField(
            value = searchQuery,
            onValueChange = {
                onSearchQueryChanged(it)
            },
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = {
                Text(
                    "Search",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            },
            trailingIcon = {
                AnimatedVisibility(searchQuery.isNotEmpty()) {
                    Row {
                        IconButton(onClick = {
                            keyboardController?.hide()
                            onSearchQueryChanged("")
                            onSearchClicked("")
                        }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        IconButton(onClick = {
                            keyboardController?.hide()
                            onSearchClicked(searchQuery)
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                unfocusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                focusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    keyboardController?.hide()
                    onSearchClicked(searchQuery)
                }
            ),
        )
    }
}

@Composable
fun AppCard(wordUI: WordUI) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {

            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = wordUI.word,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = wordUI.phoneticText,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            IconButton(
                onClick = { /* Handle play button click */ }
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play pronunciation",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )
    }
}


