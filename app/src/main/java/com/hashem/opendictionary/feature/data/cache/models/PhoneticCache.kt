package com.hashem.opendictionary.feature.data.cache.models

import com.hashem.opendictionary.feature.domain.models.Phonetic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhoneticCache(
    @SerialName("text")
    val text: String,
    @SerialName("audio")
    val audio: String,
) {
    fun toPhonetic(): Phonetic {
        return Phonetic(
            text = text,
            audio = audio
        )
    }
}

fun Phonetic.toPhoneticCache(): PhoneticCache {
    return PhoneticCache(
        text = text,
        audio = audio
    )
}