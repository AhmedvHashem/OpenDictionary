package com.hashem.opendictionary.fixtures

import com.hashem.opendictionary.feature.data.remote.models.DefinitionRemote
import com.hashem.opendictionary.feature.data.remote.models.MeaningRemote
import com.hashem.opendictionary.feature.data.remote.models.PhoneticRemote
import com.hashem.opendictionary.feature.data.remote.models.WordRemote
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

object WordRemoteFixture {

    fun createWordRemote(
        word: String = "example",
        phonetic: List<PhoneticRemote> = listOf(createPhoneticRemote()),
        meanings: List<MeaningRemote> = listOf(createMeaningRemote())
    ) = WordRemote(word, phonetic, meanings)

    private fun createPhoneticRemote(
        text: String = "phonetic", audio: String = "audio"
    ) = PhoneticRemote(text, audio)

    private fun createMeaningRemote(
        partOfSpeech: String = "noun",
        definitions: List<DefinitionRemote> = listOf(createDefinitionRemote()),
        synonyms: Set<String> = setOf("synonym"),
        antonyms: Set<String> = setOf("antonym")
    ) = MeaningRemote(partOfSpeech, definitions, synonyms, antonyms)

    private fun createDefinitionRemote(
        definition: String = "definition", example: String = "example"
    ) = DefinitionRemote(definition, example)

    fun mockSuccessResponse(word: String): Response<List<WordRemote>> =
        Response.success(listOf(createWordRemote(word = word)))

    fun mockNotFoundResponse(): Response<List<WordRemote>> {
        return Response.error(
            404,
            "{\"error\":\"Not Found\"}".toResponseBody("application/json".toMediaTypeOrNull())
        )
    }

    fun mockServerErrorResponse(): Response<List<WordRemote>> {
        return Response.error(
            500,
            "{\"error\":\"Internal Server Error\"}"
                .toResponseBody("application/json".toMediaTypeOrNull())
        )
    }
}
