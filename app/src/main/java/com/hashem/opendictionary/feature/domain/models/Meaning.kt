package com.hashem.opendictionary.feature.domain.models

data class Meaning(
    val definitions: List<Definition>,
    val synonyms: Set<String>,
    val antonyms: Set<String>
)

data class Definition(
    val definition: String,
    val example: String,
)