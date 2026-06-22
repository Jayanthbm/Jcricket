@file:OptIn(com.google.android.horologist.annotations.ExperimentalHorologistApi::class)
package com.jayanth.jcricket.wear.ui.views

import androidx.compose.ui.graphics.Color

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.TitleCard
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberColumnState
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.jayanth.jcricket.data.model.CommentaryItem
import com.jayanth.jcricket.data.model.Match
import com.jayanth.jcricket.data.model.MatchDetailsData
import com.jayanth.jcricket.wear.ui.theme.BgDark
import com.jayanth.jcricket.wear.ui.theme.CardBgDark
import com.jayanth.jcricket.wear.ui.theme.FormatOdiColor
import com.jayanth.jcricket.wear.ui.theme.FormatT20Color
import com.jayanth.jcricket.wear.ui.theme.FormatTestColor
import com.jayanth.jcricket.wear.ui.theme.LiveColor
import com.jayanth.jcricket.wear.ui.theme.ResultColor
import com.jayanth.jcricket.wear.ui.theme.TextPrimaryDark
import com.jayanth.jcricket.wear.ui.theme.TextSecondaryDark
import com.jayanth.jcricket.wear.ui.theme.UpcomingColor
import com.jayanth.jcricket.wear.ui.viewmodel.WearMatchViewModel
import com.jayanth.jcricket.wear.ui.viewmodel.WearUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WearHomeScreen(
    viewModel: WearMatchViewModel,
    modifier: Modifier = Modifier
) {
    val selectedMatch by viewModel.selectedMatch.collectAsState()
    val matchDetails by viewModel.matchDetails.collectAsState()
    val detailsLoading by viewModel.detailsLoading.collectAsState()
    val isAmbient by viewModel.isAmbient.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.setPollingActive(true)
            } else if (event == Lifecycle.Event.ON_PAUSE) {
                viewModel.setPollingActive(false)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    AppScaffold {
        if (selectedMatch != null) {
            WearMatchDetailsScreen(
                match = selectedMatch!!,
                details = matchDetails,
                isLoading = detailsLoading,
                isAmbient = isAmbient,
                onBack = { viewModel.selectMatch(null) }
            )
        } else {
            WearMatchListScreen(
                viewModel = viewModel,
                isAmbient = isAmbient,
                onMatchClick = { match -> viewModel.selectMatch(match) }
            )
        }
    }
}

@Composable
fun WearMatchListScreen(
    viewModel: WearMatchViewModel,
    isAmbient: Boolean,
    onMatchClick: (Match) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val columnState = rememberColumnState()

    ScreenScaffold(
        scrollState = columnState,
        timeText = { if (!isAmbient) TimeText() }
    ) {
        ScalingLazyColumn(
            columnState = columnState,
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Text(
                    text = "Live Scores",
                    style = MaterialTheme.typography.title3,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            when (val state = uiState) {
                is WearUiState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isAmbient) {
                                Text("Loading...", color = TextSecondaryDark, fontSize = 12.sp)
                            } else {
                                CircularProgressIndicator(indicatorColor = UpcomingColor)
                            }
                        }
                    }
                }
                is WearUiState.Success -> {
                    if (state.matches.isEmpty()) {
                        item {
                            Text(
                                text = "No active matches",
                                color = TextSecondaryDark,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        }
                    } else {
                        items(state.matches.size) { index ->
                            val match = state.matches[index]
                            WearMatchCard(
                                match = match,
                                isAmbient = isAmbient,
                                onClick = { onMatchClick(match) },
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
                is WearUiState.Error -> {
                    item {
                        Text(
                            text = "Error: ${state.message}",
                            color = LiveColor,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (!isAmbient) {
                item {
                    Chip(
                        onClick = { viewModel.fetchMatches(isRefresh = true) },
                        colors = ChipDefaults.secondaryChipColors(
                            backgroundColor = CardBgDark,
                            contentColor = TextPrimaryDark
                        ),
                        label = {
                            Text(
                                text = if (isRefreshing) "Refreshing..." else "Refresh",
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun WearMatchCard(
    match: Match,
    isAmbient: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formatColor = if (isAmbient) {
        TextSecondaryDark
    } else {
        when (match.matchFormat.uppercase()) {
            "T20" -> FormatT20Color
            "ODI" -> FormatOdiColor
            "TEST" -> FormatTestColor
            else -> UpcomingColor
        }
    }

    val isLive = match.statusType.lowercase() == "live"
    val statusColor = if (isAmbient) {
        TextSecondaryDark
    } else {
        if (isLive) LiveColor else TextSecondaryDark
    }

    TitleCard(
        onClick = onClick,
        title = {
            Text(
                text = "${match.team1.shortName} vs ${match.team2.shortName}",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        time = {
            Text(
                text = match.matchFormat,
                color = formatColor,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp
            )
        },
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            val t1Score = match.score.team1
            val t2Score = match.score.team2
            if (!t1Score.isNullOrEmpty() || !t2Score.isNullOrEmpty()) {
                Text(
                    text = "${match.team1.shortName}: ${t1Score ?: "-"}",
                    fontSize = 11.sp,
                    color = TextPrimaryDark,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "${match.team2.shortName}: ${t2Score ?: "-"}",
                    fontSize = 11.sp,
                    color = TextPrimaryDark,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            val statusText = if (match.state == "Preview" && match.startDate > 0) {
                formatStartDate(match.startDate)
            } else {
                match.status
            }

            Text(
                text = statusText,
                color = statusColor,
                fontSize = 10.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun WearMatchDetailsScreen(
    match: Match,
    details: MatchDetailsData?,
    isLoading: Boolean,
    isAmbient: Boolean,
    onBack: () -> Unit
) {
    androidx.activity.compose.BackHandler(onBack = onBack)
    val columnState = rememberColumnState()

    ScreenScaffold(
        scrollState = columnState,
        timeText = { if (!isAmbient) TimeText() }
    ) {
        ScalingLazyColumn(
            columnState = columnState,
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Text(
                    text = "${match.team1.shortName} vs ${match.team2.shortName}",
                    style = MaterialTheme.typography.title3,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isAmbient) {
                            Text("Loading...", color = TextSecondaryDark, fontSize = 12.sp)
                        } else {
                            CircularProgressIndicator(indicatorColor = UpcomingColor)
                        }
                    }
                }
            } else if (details != null) {
                val live = details.live
                if (live != null) {
                    item {
                        Card(
                            onClick = {},
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (live.battingTeam.isNotEmpty()) {
                                    Text(
                                        text = live.battingTeam.uppercase(),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.sp,
                                        color = if (isAmbient) TextPrimaryDark else UpcomingColor
                                    )
                                }
                                Text(
                                    text = live.currentScore,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    color = TextPrimaryDark
                                )
                                Text(
                                    text = "Overs: ${live.overs}",
                                    fontSize = 11.sp,
                                    color = TextSecondaryDark,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    if (live.runRate > 0.0 || live.requiredRunRate > 0.0) {
                        item {
                            Card(
                                onClick = {},
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    if (live.runRate > 0.0) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("CRR:", fontSize = 10.sp, color = TextSecondaryDark)
                                            Text(live.runRate.toString(), fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                        }
                                    }
                                    if (live.requiredRunRate > 0.0) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("RRR:", fontSize = 10.sp, color = TextSecondaryDark)
                                            Text(live.requiredRunRate.toString(), fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    val striker = live.striker
                    val nonStriker = live.nonStriker
                    val bowler = live.bowlerStriker
                    val bowlerNon = live.bowlerNonStriker

                    if (striker != null || nonStriker != null || bowler != null || bowlerNon != null) {
                        item {
                            Card(
                                onClick = {},
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    if (striker != null && striker.name.isNotEmpty()) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "${striker.name}*",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimaryDark,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Text(
                                                text = "${striker.runs} (${striker.balls})",
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily.Monospace,
                                                color = TextPrimaryDark
                                            )
                                        }
                                    }
                                    if (nonStriker != null && nonStriker.name.isNotEmpty()) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = nonStriker.name,
                                                fontSize = 10.sp,
                                                color = TextSecondaryDark,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Text(
                                                text = "${nonStriker.runs} (${nonStriker.balls})",
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily.Monospace,
                                                color = TextSecondaryDark
                                            )
                                        }
                                    }

                                    val hasBowlers = (bowler != null && bowler.name.isNotEmpty()) || (bowlerNon != null && bowlerNon.name.isNotEmpty())
                                    if (hasBowlers) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(0.5.dp)
                                                .background(TextSecondaryDark.copy(alpha = 0.2f))
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))

                                        if (bowler != null && bowler.name.isNotEmpty()) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = "${bowler.name}*",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = TextPrimaryDark,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                Text(
                                                    text = "${bowler.wickets}-${bowler.runs} (${bowler.overs})",
                                                    fontSize = 10.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    color = TextPrimaryDark
                                                )
                                            }
                                        }

                                        if (bowlerNon != null && bowlerNon.name.isNotEmpty()) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = bowlerNon.name,
                                                    fontSize = 10.sp,
                                                    color = TextSecondaryDark,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                Text(
                                                    text = "${bowlerNon.wickets}-${bowlerNon.runs} (${bowlerNon.overs})",
                                                    fontSize = 10.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    color = TextSecondaryDark
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    val recentBalls = live.recentBalls
                    if (!recentBalls.isNullOrEmpty()) {
                        item {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "RECENT BALLS",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondaryDark,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    recentBalls.takeLast(5).forEach { ball ->
                                        val cleanBall = ball.trim()
                                        if (cleanBall.isNotEmpty()) {
                                            WearBallBadge(ball = cleanBall, isAmbient = isAmbient)
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    val scoreDetails = details.matchScoreDetails
                    if (scoreDetails != null) {
                        item {
                            Card(
                                onClick = {},
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "RESULT",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondaryDark,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    scoreDetails.inningsScoreList.forEach { innings ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = innings.batTeamName,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimaryDark
                                            )
                                            Text(
                                                text = "${innings.score}/${innings.wickets} (${innings.overs})",
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily.Monospace,
                                                color = TextPrimaryDark
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        item {
                            Card(
                                onClick = {},
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "MATCH INFO",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondaryDark,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = details.matchInfo.seriesName,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isAmbient) TextPrimaryDark else UpcomingColor,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    val desc = details.matchInfo.matchDesc
                                    if (!desc.isNullOrEmpty()) {
                                        Text(
                                            text = desc,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimaryDark,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                    if (details.matchInfo.venueText.isNotEmpty()) {
                                        Text(
                                            text = details.matchInfo.venueText,
                                            fontSize = 10.sp,
                                            color = TextSecondaryDark,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                    val startTimestamp = details.matchInfo.startDate
                                    if (startTimestamp > 0) {
                                        Text(
                                            text = formatStartDate(startTimestamp),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = TextSecondaryDark,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                    val toss = details.matchInfo.toss
                                    if (toss != null && toss.winner.isNotEmpty() && toss.decision.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        val tossBgColor = if (isAmbient) CardBgDark else UpcomingColor.copy(alpha = 0.12f)
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(tossBgColor, shape = RoundedCornerShape(4.dp))
                                                .padding(6.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${toss.winner} won toss & elected to ${toss.decision.lowercase()}",
                                                fontSize = 9.sp,
                                                color = TextPrimaryDark,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Status or result message
                item {
                    val statusText = if (match.state == "Preview" && match.startDate > 0) {
                        formatStartDate(match.startDate)
                    } else {
                        match.status
                    }
                    val currentStatusColor = if (isAmbient) {
                        TextSecondaryDark
                    } else {
                        if (match.statusType.lowercase() == "live") LiveColor else ResultColor
                    }
                    Text(
                        text = statusText,
                        color = currentStatusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            } else {
                item {
                    Text(
                        text = "Failed to load match details",
                        color = TextSecondaryDark,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (!isAmbient) {
                item {
                    Chip(
                        onClick = onBack,
                        colors = ChipDefaults.secondaryChipColors(
                            backgroundColor = CardBgDark,
                            contentColor = TextPrimaryDark
                        ),
                        label = {
                            Text(
                                text = "Back",
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun WearBallBadge(ball: String, isAmbient: Boolean = false) {
    val (bg, fg) = if (isAmbient) {
        Pair(CardBgDark, TextPrimaryDark)
    } else {
        when {
            ball.lowercase().contains("w") -> Pair(LiveColor, TextPrimaryDark)
            ball.contains("4") -> Pair(FormatOdiColor, TextPrimaryDark)
            ball.contains("6") -> Pair(FormatTestColor, TextPrimaryDark)
            ball.lowercase().contains("wd") || ball.lowercase().contains("nb") || ball.lowercase().contains("b") || ball.lowercase().contains("lb") -> Pair(Color(0xFFEAB308), BgDark)
            else -> Pair(CardBgDark, TextPrimaryDark)
        }
    }

    Box(
        modifier = Modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(bg),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = ball,
            color = fg,
            fontSize = 9.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
    }
}

private fun formatStartDate(timestamp: Long): String {
    if (timestamp <= 0) return ""
    return try {
        val date = Date(timestamp)
        val format = SimpleDateFormat("EEE d MMM hh:mm a", Locale.getDefault())
        format.format(date)
    } catch (e: Exception) {
        ""
    }
}
