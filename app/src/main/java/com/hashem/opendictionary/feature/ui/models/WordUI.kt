package com.hashem.opendictionary.feature.ui.models

import com.hashem.opendictionary.feature.domain.models.Word

data class WordUI(
    val word: String,
    val phoneticText: String,
    val phoneticAudio: String,
    val meanings: Map<String, MeaningUI>,
)

fun Word.toWordUI() = WordUI(
    word = word,
    phoneticText = phonetic.text,
    phoneticAudio = phonetic.audio,
    meanings = meanings.mapValues { it.value.toMeaningUI() }
)