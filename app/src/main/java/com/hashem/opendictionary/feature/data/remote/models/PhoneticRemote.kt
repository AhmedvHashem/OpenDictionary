package com.hashem.opendictionary.feature.data.remote.models

import com.hashem.opendictionary.feature.domain.models.Phonetic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhoneticRemote(
    @SerialName("text")
    var text: String? = null,
    @SerialName("audio")
    var audio: String? = null,
) {
    fun toPhonetic(): Phonetic {
        return Phonetic(
            text = text ?: "",
            audio = audio ?: ""
        )
    }
}