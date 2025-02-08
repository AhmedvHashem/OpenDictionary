package com.hashem.opendictionary.feature.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hashem.opendictionary.feature.ui.models.WordUI
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDetailsScreen(
    word: WordUI,
    playAction: (String) -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    ModalBottomSheet(
        sheetState = rememberModalBottomSheetState(true),
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Word with pronunciation section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = word.word.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(
                                Locale.ROOT
                            ) else it.toString()
                        },
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = word.phoneticText.takeIf { it.isNotEmpty() } ?: "//",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                    )
                }
                IconButton(
                    onClick = {
                        if (word.phoneticAudio.isNotEmpty())
                            playAction(word.phoneticAudio)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play pronunciation",
                        tint = if (word.phoneticAudio.isNotEmpty()) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.inversePrimary
                    )
                }
            }

            // Definition section
            Text(
                text = "DEFINITIONS",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(word.meanings.toList()) { (partOfSpeech, meaning) ->
                    Text(
                        text = partOfSpeech.uppercase(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Column {
                        meaning.definitions.forEachIndexed { index, definition ->
                            Text(
                                text = "${index + 1}. ${definition.definition}",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                            )
                        }
                        if (meaning.synonyms.isNotEmpty())
                            Text(
                                text = "SYNONYMS: ${meaning.synonyms.joinToString()}",
                                fontSize = 12.sp,
                                lineHeight = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier.padding(top = 6.dp),
                            )

                        if (meaning.antonyms.isNotEmpty())
                            Text(
                                text = "ANTONYMS: ${meaning.antonyms.joinToString()}",
                                fontSize = 12.sp,
                                lineHeight = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier.padding(top = 6.dp),
                            )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
