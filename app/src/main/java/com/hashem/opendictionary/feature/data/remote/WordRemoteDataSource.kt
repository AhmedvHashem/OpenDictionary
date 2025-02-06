package com.hashem.opendictionary.feature.data.remote

import com.hashem.opendictionary.feature.data.remote.models.WordRemote
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface WordRemoteDataSource {

    @GET("/entries/en/{word}")
    suspend fun getWords(
        @Path("word") word: String
    ): Response<List<WordRemote>>
}