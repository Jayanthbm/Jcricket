package com.jayanth.jcricket.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class MatchesData(
    @SerialName("matches") val matches: List<Match> = emptyList(),
    @SerialName("count") val count: Int = 0,
    @SerialName("lastUpdated") val lastUpdated: Long = 0L,
    @SerialName("lastUpdatedText") val lastUpdatedText: String? = null
)

@Serializable
data class Match(
    @SerialName("matchId") val matchId: Int = 0,
    @SerialName("seriesName") val seriesName: String = "",
    @SerialName("matchDesc") val matchDesc: String? = null,
    @SerialName("matchFormat") val matchFormat: String = "",
    @SerialName("statusType") val statusType: String = "",
    @SerialName("team1") val team1: Team = Team(),
    @SerialName("team2") val team2: Team = Team(),
    @SerialName("score") val score: Score = Score(),
    @SerialName("displayScore") val displayScore: String? = null,
    @SerialName("state") val state: String = "",
    @SerialName("stateTitle") val stateTitle: String? = null,
    @SerialName("status") val status: String = "",
    @SerialName("shortStatus") val shortStatus: String? = null,
    @SerialName("venueText") val venueText: String = "",
    @SerialName("startDate") val startDate: Long = 0L,
    @SerialName("startDateText") val startDateText: String? = null,
    @SerialName("matchType") val matchType: String = "",
    @SerialName("sortKey") val sortKey: Long = 0L
)

@Serializable
data class Team(
    @SerialName("id") val id: Int = 0,
    @SerialName("name") val name: String = "",
    @SerialName("shortName") val shortName: String = "",
    @SerialName("imageId") val imageId: Int? = null,
    @SerialName("icon") val icon: String? = null
)

@Serializable
data class Score(
    @SerialName("team1") val team1: String? = null,
    @SerialName("team2") val team2: String? = null
)
