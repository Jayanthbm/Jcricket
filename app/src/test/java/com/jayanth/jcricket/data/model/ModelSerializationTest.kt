package com.jayanth.jcricket.data.model

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ModelSerializationTest {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Test
    fun testOversSerializer_withDoubleValue() {
        val jsonInput = """
            {
                "inningsId": 1,
                "battingTeam": "IND",
                "currentScore": "150/3",
                "overs": 14.2,
                "runRate": 10.5
            }
        """.trimIndent()

        val liveInfo = json.decodeFromString<LiveInfo>(jsonInput)
        assertEquals("14.2", liveInfo.overs)
    }

    @Test
    fun testOversSerializer_withEmptyStringValue() {
        val jsonInput = """
            {
                "inningsId": 1,
                "battingTeam": "IND",
                "currentScore": "0/0",
                "overs": "",
                "runRate": 0.0
            }
        """.trimIndent()

        val liveInfo = json.decodeFromString<LiveInfo>(jsonInput)
        assertEquals("", liveInfo.overs)
    }

    @Test
    fun testLiveInfo_withMissingInningsId() {
        val jsonInput = """
            {
                "battingTeam": "",
                "currentScore": "0/0",
                "overs": "",
                "runRate": 0
            }
        """.trimIndent()

        val liveInfo = json.decodeFromString<LiveInfo>(jsonInput)
        assertEquals(0, liveInfo.inningsId)
        assertEquals("", liveInfo.battingTeam)
    }

    @Test
    fun testMatchDetailsData_fullParsing() {
        val jsonInput = """
            {
                "matchFormat": "T20",
                "state": "In Progress",
                "status": "India won by 8 wickets",
                "matchInfo": {
                    "matchId": 12345,
                    "seriesName": "ICC Men's T20 World Cup 2026",
                    "matchFormat": "T20",
                    "venueText": "St Lucia",
                    "startDate": 1718890200000,
                    "matchType": "T20I",
                    "team1": {
                        "id": 1,
                        "name": "India",
                        "shortName": "IND",
                        "icon": "http://example.com/ind.png"
                    },
                    "team2": {
                        "id": 2,
                        "name": "Australia",
                        "shortName": "AUS",
                        "icon": "http://example.com/aus.png"
                    }
                },
                "live": {
                    "inningsId": 2,
                    "battingTeam": "IND",
                    "currentScore": "120/2",
                    "overs": "12.4",
                    "runRate": 9.47,
                    "striker": {
                        "name": "Virat Kohli",
                        "runs": 45,
                        "balls": 30,
                        "fours": 4,
                        "sixes": 1,
                        "strikeRate": 150.0
                    }
                },
                "commentary": [
                    {
                        "over": 12.4,
                        "event": "FOUR",
                        "text": "Four runs scored!"
                    }
                ]
            }
        """.trimIndent()

        val details = json.decodeFromString<MatchDetailsData>(jsonInput)
        assertEquals("T20", details.matchFormat)
        assertEquals("In Progress", details.state)
        assertEquals(12345, details.matchInfo.matchId)
        assertEquals("India", details.matchInfo.team1.name)
        assertNotNull(details.live)
        assertEquals("12.4", details.live?.overs)
        assertEquals("Virat Kohli", details.live?.striker?.name)
        assertEquals(45, details.live?.striker?.runs)
        assertEquals(1, details.commentary.size)
        assertEquals(12.4, details.commentary[0].over)
    }

    @Test
    fun testTestMatchDetails_parsing() {
        val jsonInput = """
            {
                "success": true,
                "data": {
                    "matchFormat": "TEST",
                    "state": "In Progress",
                    "status": "Day 4: 3rd Session - England need 321 runs",
                    "matchInfo": {
                        "matchId": 129563,
                        "seriesName": "New Zealand tour of England, 2026",
                        "matchDesc": "2nd Test",
                        "matchFormat": "TEST",
                        "venueText": "Kennington Oval, London",
                        "startDate": 1781690400000,
                        "matchType": "International",
                        "team1": { "id": 13, "name": "New Zealand", "shortName": "NZ" },
                        "team2": { "id": 9, "name": "England", "shortName": "ENG" }
                    },
                    "live": {
                        "inningsId": 4,
                        "battingTeam": "ENG",
                        "currentScore": "150/4",
                        "overs": 34.1,
                        "runRate": 4.39,
                        "requiredRunRate": 0,
                        "remRunsToWin": 0,
                        "oversRem": 21.5,
                        "recentOvers": "B4",
                        "recentBalls": ["B4"],
                        "bowlerStriker": {
                            "id": 9067,
                            "name": "Matt Henry",
                            "overs": 11.1,
                            "runs": 25,
                            "wickets": 1,
                            "economy": 2.2
                        },
                        "bowlerNonStriker": {
                            "id": 15925,
                            "name": "William ORourke",
                            "overs": 7,
                            "runs": 42,
                            "wickets": 1,
                            "economy": 6
                        }
                    }
                }
            }
        """.trimIndent()

            val envelope = json.decodeFromString<ApiResponse<MatchDetailsData>>(jsonInput)
            assertNotNull(envelope.data)
            val details = envelope.data!!
            assertEquals("TEST", details.matchFormat)
            assertEquals(21.5, details.live?.oversRem ?: 0.0, 0.01)
            assertEquals(0, details.live?.bowlerStriker?.maidens ?: -1)
    }
}
