package com.hashem.opendictionary.feature.ui.models

import com.hashem.opendictionary.feature.domain.models.Meaning

data class MeaningUI(
    val definitions: List<DefinitionUI>,
    val synonyms: Set<String>,
    val antonyms: Set<String>
)

data class DefinitionUI(
    val definition: String,
    val example: String,
)

fun Meaning.toMeaningUI() = MeaningUI(
    definitions = definitions.map { DefinitionUI(it.definition, it.example) },
    synonyms = synonyms,
    antonyms = antonyms
)