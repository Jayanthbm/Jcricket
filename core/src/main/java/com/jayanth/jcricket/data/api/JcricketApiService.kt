package com.jayanth.jcricket.data.api

import com.jayanth.jcricket.data.model.ApiResponse
import com.jayanth.jcricket.data.model.MatchesData
import com.jayanth.jcricket.data.model.MatchDetailsData
import retrofit2.http.GET
import retrofit2.http.Path

interface JcricketApiService {
    @GET("matches")
    suspend fun getMatches(): ApiResponse<MatchesData>

    @GET("matches/{matchId}")
    suspend fun getMatchDetails(@Path("matchId") matchId: Int): ApiResponse<MatchDetailsData>
}
