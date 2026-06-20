package com.jayanth.jcricket.data.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive

object OversSerializer : KSerializer<String> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Overs", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): String {
        return if (decoder is JsonDecoder) {
            val element = decoder.decodeJsonElement()
            if (element is JsonPrimitive) {
                element.content
            } else {
                ""
            }
        } else {
            decoder.decodeString()
        }
    }

    override fun serialize(encoder: Encoder, value: String) {
        encoder.encodeString(value)
    }
}

@Serializable
data class MatchDetailsData(
    @SerialName("matchFormat") val matchFormat: String = "",
    @SerialName("state") val state: String = "",
    @SerialName("status") val status: String = "",
    @SerialName("matchInfo") val matchInfo: MatchInfo = MatchInfo(),
    @SerialName("live") val live: LiveInfo? = null,
    @SerialName("commentary") val commentary: List<CommentaryItem> = emptyList(),
    @SerialName("lastUpdated") val lastUpdated: Long? = null
)

@Serializable
data class MatchInfo(
    @SerialName("matchId") val matchId: Int = 0,
    @SerialName("seriesName") val seriesName: String = "",
    @SerialName("matchDesc") val matchDesc: String? = null,
    @SerialName("matchFormat") val matchFormat: String = "",
    @SerialName("venueText") val venueText: String = "",
    @SerialName("startDate") val startDate: Long = 0L,
    @SerialName("startDateText") val startDateText: String? = null,
    @SerialName("matchType") val matchType: String = "",
    @SerialName("team1") val team1: Team = Team(),
    @SerialName("team2") val team2: Team = Team(),
    @SerialName("toss") val toss: TossInfo? = null
)

@Serializable
data class TossInfo(
    @SerialName("winner") val winner: String = "",
    @SerialName("decision") val decision: String = ""
)

@Serializable
data class LiveInfo(
    @SerialName("inningsId") val inningsId: Int = 0,
    @SerialName("battingTeam") val battingTeam: String = "",
    @SerialName("currentScore") val currentScore: String = "",
    @Serializable(with = OversSerializer::class)
    @SerialName("overs") val overs: String = "",
    @SerialName("runRate") val runRate: Double = 0.0,
    @SerialName("requiredRunRate") val requiredRunRate: Double = 0.0,
    @SerialName("remRunsToWin") val remRunsToWin: Int = 0,
    @SerialName("oversRem") val oversRem: Double = 0.0,
    @SerialName("recentOvers") val recentOvers: String? = null,
    @SerialName("recentBalls") val recentBalls: List<String> = emptyList(),
    @SerialName("partnership") val partnership: PartnershipInfo? = null,
    @SerialName("partnershipText") val partnershipText: String? = null,
    @SerialName("lastWicket") val lastWicket: String? = null,
    @SerialName("striker") val striker: BatterInfo? = null,
    @SerialName("nonStriker") val nonStriker: BatterInfo? = null,
    @SerialName("bowlerStriker") val bowlerStriker: BowlerInfo? = null,
    @SerialName("bowlerNonStriker") val bowlerNonStriker: BowlerInfo? = null,
    @SerialName("matchUdrs") val matchUdrs: UdrsInfo? = null
)

@Serializable
data class PartnershipInfo(
    @SerialName("runs") val runs: Int = 0,
    @SerialName("balls") val balls: Int = 0
)

@Serializable
data class BatterInfo(
    @SerialName("name") val name: String = "",
    @SerialName("runs") val runs: Int = 0,
    @SerialName("balls") val balls: Int = 0,
    @SerialName("fours") val fours: Int = 0,
    @SerialName("sixes") val sixes: Int = 0,
    @SerialName("strikeRate") val strikeRate: Double = 0.0
)

@Serializable
data class BowlerInfo(
    @SerialName("id") val id: Int? = null,
    @SerialName("name") val name: String = "",
    @SerialName("overs") val overs: Double = 0.0,
    @SerialName("maidens") val maidens: Int = 0,
    @SerialName("runs") val runs: Int = 0,
    @SerialName("wickets") val wickets: Int = 0,
    @SerialName("economy") val economy: Double = 0.0
)

@Serializable
data class UdrsInfo(
    @SerialName("matchId") val matchId: Int? = null,
    @SerialName("inningsId") val inningsId: Int? = null,
    @SerialName("timestamp") val timestamp: String? = null,
    @SerialName("team1Id") val team1Id: Int? = null,
    @SerialName("team1Remaining") val team1Remaining: Int = 0,
    @SerialName("team1Successful") val team1Successful: Int = 0,
    @SerialName("team1Unsuccessful") val team1Unsuccessful: Int = 0,
    @SerialName("team2Id") val team2Id: Int? = null,
    @SerialName("team2Remaining") val team2Remaining: Int = 0,
    @SerialName("team2Successful") val team2Successful: Int = 0,
    @SerialName("team2Unsuccessful") val team2Unsuccessful: Int = 0
)

@Serializable
data class CommentaryItem(
    @SerialName("over") val over: Double? = null,
    @SerialName("event") val event: String? = null,
    @SerialName("text") val text: String = "",
    @SerialName("timestamp") val timestamp: Long? = null
)
