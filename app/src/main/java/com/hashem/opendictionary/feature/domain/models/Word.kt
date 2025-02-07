package com.hashem.opendictionary.feature.domain.models

data class Word(
    val word: String,
    val phonetic: Phonetic,
    val meanings: Map<String, Meaning>,
)
