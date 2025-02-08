package com.hashem.opendictionary.feature.ui.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hashem.opendictionary.feature.ui.models.DefinitionUI
import com.hashem.opendictionary.feature.ui.models.MeaningUI
import com.hashem.opendictionary.feature.ui.models.WordUI
import com.hashem.opendictionary.theme.OpenDictionaryTheme
import java.util.Locale


@Preview(showBackground = true)
@Composable
fun OpenDictionaryAppPreview() {
    OpenDictionaryTheme {
        AppListView(
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
                                            definition = "Used as a greeting or to begin with a greeting and conversation or attract attention.",
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
fun AppListView(
    searchResults: List<WordUI>,
    detailsAction: (WordUI) -> Unit = {},
    playAction: (String) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(searchResults) { word ->
            AppCard(word, detailsAction, playAction)
        }
    }
}

@Composable
fun AppCard(
    wordUI: WordUI,
    detailsAction: (WordUI) -> Unit = {},
    playAction: (String) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                detailsAction(wordUI)
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
                    text = wordUI.word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = wordUI.phoneticText.takeIf { it.isNotEmpty() } ?: "//",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                )
                Spacer(modifier = Modifier.height(8.dp))

                wordUI.meanings.firstNotNullOf { (_, meaning) ->
                    Column {
                        meaning.definitions.firstNotNullOf { definition ->
                            Text(
                                text = definition.definition,
                                fontSize = 16.sp,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                                softWrap = true,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

            }

            IconButton(
                onClick = {
                    if (wordUI.phoneticAudio.isNotEmpty())
                        playAction(wordUI.phoneticAudio)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play pronunciation",
                    tint = if (wordUI.phoneticAudio.isNotEmpty()) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.inversePrimary
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )
    }
}


