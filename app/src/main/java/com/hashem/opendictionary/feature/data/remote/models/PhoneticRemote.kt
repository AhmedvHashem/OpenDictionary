package com.hashem.opendictionary.feature.data.remote.models

import com.hashem.opendictionary.feature.domain.models.Phonetic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhoneticRemote(
    @SerialName("text")
    val text: String?,
    @SerialName("audio")
    val audio: String?,
) {
    fun toPhonetic(): Phonetic {
        return Phonetic(
            text = text ?: "",
            audio = audio ?: ""
        )
    }
}