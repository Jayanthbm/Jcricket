package com.jayanth.jcricket.data.repository

import com.jayanth.jcricket.data.api.RetrofitClient
import com.jayanth.jcricket.data.model.MatchesData
import com.jayanth.jcricket.data.model.MatchDetailsData

class MatchRepository {
    private val apiService = RetrofitClient.apiService

    suspend fun getMatches(): MatchesData {
        val response = apiService.getMatches()
        if (response.success) {
            return response.data
        } else {
            throw Exception("Failed to fetch matches: Success flag is false")
        }
    }

    suspend fun getMatchDetails(matchId: Int): MatchDetailsData {
        val response = apiService.getMatchDetails(matchId)
        if (response.success) {
            return response.data
        } else {
            throw Exception("Failed to fetch match details: Success flag is false")
        }
    }
}
