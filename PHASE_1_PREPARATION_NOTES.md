# Phase 1 Preparation Notes: Jcricket

This document details the analysis and mapping of the React web scores codebase to Kotlin and Jetpack Compose configurations.

---

## 1. React reference application dependency analysis

*   **Repository path**: `/Users/jayanthbharadwajm/development/cricket-scores-web`
*   **Direct dependencies**: React 19 (`react`, `react-dom`).
*   **Dev dependencies**: Vite, ESLint.
*   **Aesthetic & Styling mechanism**: The React codebase uses basic **Vanilla CSS**. No styled-components or Tailwind CSS. Theme swapping (light/dark mode) is accomplished by setting a `data-theme` attribute on the HTML root element (`document.documentElement.setAttribute("data-theme", "dark")`) which toggles variables in the CSS :root stylesheets.

---

## 2. API contracts & Kotlin mapping schemas

The Jcricket backend worker utilizes standard JSON structures. These tables and definitions map these JSON contracts directly to strict Kotlin structures optimized for `kotlinx-serialization`.

### Generic envelope
```kotlin
package com.jayanth.jcricket.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class ApiResponse<T>(
    @SerialName("success") val success: Boolean,
    @SerialName("data") val data: T
)
```

### 2.1 /matches API Models
*   **Route**: `GET https://cricket-scores.jayanthbharadwajm.workers.dev/matches`
*   **Purpose**: Fetch all active and completed matches.

```kotlin
package com.jayanth.jcricket.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class MatchesData(
    @SerialName("matches") val matches: List<Match>,
    @SerialName("count") val count: Int,
    @SerialName("lastUpdated") val lastUpdated: Long,
    @SerialName("lastUpdatedText") val lastUpdatedText: String? = null
)

@Serializable
data class Match(
    @SerialName("matchId") val matchId: Int,
    @SerialName("seriesName") val seriesName: String,
    @SerialName("matchDesc") val matchDesc: String? = null,
    @SerialName("matchFormat") val matchFormat: String, // T20, ODI, TEST
    @SerialName("statusType") val statusType: String, // Upcoming, Live, Completed
    @SerialName("team1") val team1: Team,
    @SerialName("team2") val team2: Team,
    @SerialName("score") val score: Score,
    @SerialName("displayScore") val displayScore: String? = null,
    @SerialName("state") val state: String, // Preview, In Progress, Complete
    @SerialName("stateTitle") val stateTitle: String? = null,
    @SerialName("status") val status: String,
    @SerialName("shortStatus") val shortStatus: String? = null,
    @SerialName("venueText") val venueText: String,
    @SerialName("startDate") val startDate: Long,
    @SerialName("startDateText") val startDateText: String? = null,
    @SerialName("matchType") val matchType: String,
    @SerialName("sortKey") val sortKey: Long
)

@Serializable
data class Team(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("shortName") val shortName: String,
    @SerialName("imageId") val imageId: Int? = null,
    @SerialName("icon") val icon: String? = null // Fully formed URL to proxy image
)

@Serializable
data class Score(
    @SerialName("team1") val team1: String? = null,
    @SerialName("team2") val team2: String? = null
)
```

### 2.2 /matches/:matchId API Models
*   **Route**: `GET https://cricket-scores.jayanthbharadwajm.workers.dev/matches/:matchId`
*   **Purpose**: Fetch granular in-play summaries, batting/bowling details, and text commentary.

```kotlin
package com.jayanth.jcricket.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class MatchDetailsData(
    @SerialName("matchFormat") val matchFormat: String,
    @SerialName("state") val state: String,
    @SerialName("status") val status: String,
    @SerialName("matchInfo") val matchInfo: MatchInfo,
    @SerialName("live") val live: LiveInfo? = null, // Null for "Preview" states
    @SerialName("commentary") val commentary: List<CommentaryItem> = emptyList(),
    @SerialName("lastUpdated") val lastUpdated: Long? = null
)

@Serializable
data class MatchInfo(
    @SerialName("matchId") val matchId: Int,
    @SerialName("seriesName") val seriesName: String,
    @SerialName("matchDesc") val matchDesc: String? = null,
    @SerialName("matchFormat") val matchFormat: String,
    @SerialName("venueText") val venueText: String,
    @SerialName("startDate") val startDate: Long,
    @SerialName("startDateText") val startDateText: String? = null,
    @SerialName("matchType") val matchType: String,
    @SerialName("team1") val team1: Team,
    @SerialName("team2") val team2: Team,
    @SerialName("toss") val toss: TossInfo? = null
)

@Serializable
data class TossInfo(
    @SerialName("winner") val winner: String,
    @SerialName("decision") val decision: String // Batting or Bowl
)

@Serializable
data class LiveInfo(
    @SerialName("inningsId") val inningsId: Int,
    @SerialName("battingTeam") val battingTeam: String,
    @SerialName("currentScore") val currentScore: String,
    @SerialName("overs") val overs: Double,
    @SerialName("runRate") val runRate: Double,
    @SerialName("requiredRunRate") val requiredRunRate: Double = 0.0,
    @SerialName("remRunsToWin") val remRunsToWin: Int = 0,
    @SerialName("oversRem") val oversRem: Int = 0,
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
    @SerialName("runs") val runs: Int,
    @SerialName("balls") val balls: Int
)

@Serializable
data class BatterInfo(
    @SerialName("name") val name: String,
    @SerialName("runs") val runs: Int,
    @SerialName("balls") val balls: Int,
    @SerialName("fours") val fours: Int,
    @SerialName("sixes") val sixes: Int,
    @SerialName("strikeRate") val strikeRate: Double
)

@Serializable
data class BowlerInfo(
    @SerialName("id") val id: Int? = null,
    @SerialName("name") val name: String,
    @SerialName("overs") val overs: Double,
    @SerialName("maidens") val maidens: Int,
    @SerialName("runs") val runs: Int,
    @SerialName("wickets") val wickets: Int,
    @SerialName("economy") val economy: Double
)

@Serializable
data class UdrsInfo(
    @SerialName("matchId") val matchId: Int? = null,
    @SerialName("inningsId") val inningsId: Int? = null,
    @SerialName("timestamp") val timestamp: String? = null,
    @SerialName("team1Id") val team1Id: Int? = null,
    @SerialName("team1Remaining") val team1Remaining: Int,
    @SerialName("team1Successful") val team1Successful: Int = 0,
    @SerialName("team1Unsuccessful") val team1Unsuccessful: Int = 0,
    @SerialName("team2Id") val team2Id: Int? = null,
    @SerialName("team2Remaining") val team2Remaining: Int,
    @SerialName("team2Successful") val team2Successful: Int = 0,
    @SerialName("team2Unsuccessful") val team2Unsuccessful: Int = 0
)

@Serializable
data class CommentaryItem(
    @SerialName("over") val over: Double? = null,
    @SerialName("event") val event: String? = null, // e.g., WICKET, FOUR, SIX, NONE
    @SerialName("text") val text: String,
    @SerialName("timestamp") val timestamp: Long? = null
)
```

---

## 3. UI/UX Style guide specs & Jetpack Compose mappings

Below is the mapping of design system rules, spacing tokens, typography styles, and color specifications.

### 3.1 Dark Theme Palette mapping
We configure Jetpack Compose colors mapping directly to CSS selectors inside `[data-theme='dark']` layout blocks:

| Web CSS Token | Color Hex | Recommended Compose Variable | Purpose |
| :--- | :--- | :--- | :--- |
| `--bg` | `#111827` | `ColorScheme.background` | App background view |
| `--card-bg` | `#1F2937` | `ColorScheme.surface` | Dashboard cards & details box layout background |
| `--border` | `#374151` | `ColorScheme.outline` | Separator line layouts & outline shapes |
| `--text-primary` | `#F9FAFB` | `ColorScheme.onBackground` / `onSurface` | Main labels, scores, and headings |
| `--text-secondary` | `#9CA3AF` | `ColorScheme.onSurfaceVariant` | Subtitles, overs count, headers, labels |
| `--live` | `#F87171` | `CustomColors.live` | Wickets text, blinking live pulse indicators |
| `--upcoming` | `#60A5FA` | `ColorScheme.primary` | Next match timestamps, upcoming games indicators |
| `--result` | `#4ADE80` | `CustomColors.result` | Completed match outcome strings, striker asterisks |

### 3.2 Dynamic match format accent mappings
```kotlin
val FormatT20Color = Color(0xFFFB923C)   // CSS --t20: #fb923c
val FormatOdiColor = Color(0xFF38BDF8)   // CSS --odi: #38bdf8
val FormatTestColor = Color(0xFFA78BFA)  // CSS --test: #a78bfa
```

### 3.3 Dynamic delivery/commentary event colors
Used inside `RecentBallsList` and `CommentarySection` to display colored badges:
```kotlin
val EventWicketColor = Color(0xFFF87171)   // CSS .wicket / .wicket-event: #f87171
val EventFourColor = Color(0xFF60A5FA)     // CSS .boundary-four / .four-event: #60a5fa
val EventSixColor = Color(0xFFA78BFA)      // CSS .boundary-six / .six-event: #a78bfa
val EventExtraColor = Color(0xFFEAB308)    // CSS .wide / .noball: #eab308
```

### 3.4 Gradient details
The React main dashboard score runs and highlighted texts utilize a linear diagonal gradient. This can be recreated in Compose using:
```kotlin
val ScoreRunsBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xFF60A5FA), // Blue accent: #60a5fa
        Color(0xFFA78BFA)  // Purple accent: #a78bfa
    )
)
```

### 3.5 Border Radii & Spacing
*   **Card borders**: Rounded corners of size `12.dp` (CSS `border-radius: 12px` inside details card).
*   **Inner elements / Sub-boxes**: Rounded corners of `8.dp` (CSS `border-radius: 8px`).
*   **Delivery circles**: Rounded shape of size `50%` (circular badges).
*   **Margins & Spacers**:
    *   Match details margins: `16.dp` layout margins.
    *   Details inner cards padding: `18.dp` padding space.

---

## 4. Coding styles & formatting guidelines

To maintain code health across all native modules:
1.  **Indentation**: Strict **4 spaces** indentation (defined in `.editorconfig`).
2.  **File naming**: Kotlin classes should match the PascalCase notation (e.g. `MainActivity.kt`), package directories must be all lowercase.
3.  **Nullable handling**: All optional fields must be annotated using Kotlin nullable `?` flags and support standard null checks or fallbacks inside Jetpack Compose elements (e.g. `score.team1 ?: "-"`).
4.  **Compose preview structures**: Every UI view must provide a `@Preview` composable utilizing the custom dark layout settings.
