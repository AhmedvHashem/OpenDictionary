package com.hashem.opendictionary.feature.domain.models

data class Phonetic(
    val text: String,
    val audio: String,
){
    fun isEmpty(): Boolean {
        return text.isEmpty()
    }
}