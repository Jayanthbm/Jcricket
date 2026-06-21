package com.jayanth.jcricket.ui.views

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jayanth.jcricket.ui.theme.BgDark
import com.jayanth.jcricket.ui.theme.BorderDark
import com.jayanth.jcricket.ui.theme.CardBgDark
import com.jayanth.jcricket.ui.theme.FormatOdiColor
import com.jayanth.jcricket.ui.theme.FormatT20Color
import com.jayanth.jcricket.ui.theme.FormatTestColor
import com.jayanth.jcricket.ui.theme.LiveColor
import com.jayanth.jcricket.ui.theme.ResultColor
import com.jayanth.jcricket.ui.theme.TextPrimaryDark
import com.jayanth.jcricket.ui.theme.TextSecondaryDark
import com.jayanth.jcricket.ui.theme.UpcomingColor
import com.jayanth.jcricket.ui.viewmodel.MatchViewModel
import com.jayanth.jcricket.ui.views.components.BatterStatsTable
import com.jayanth.jcricket.ui.views.components.BowlerStatsTable
import com.jayanth.jcricket.ui.views.components.FinishedScoreCard
import com.jayanth.jcricket.ui.views.components.MatchInfoCard
import com.jayanth.jcricket.ui.views.components.RecentBallsList
import com.jayanth.jcricket.ui.views.components.ScoreSummaryCard
import com.jayanth.jcricket.ui.views.components.UdrsDetailsCard
import com.jayanth.jcricket.ui.views.components.formatStartDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailsScreen(
    viewModel: MatchViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)

    val data by viewModel.matchDetails.collectAsState()
    val isLoading by viewModel.detailsLoading.collectAsState()
    val isRefreshing by viewModel.detailsRefreshing.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        // App Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(CardBgDark)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(BorderDark)
            ) {
                Text(
                    text = "←",
                    color = TextPrimaryDark,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // State Badge in Header
            data?.let { details ->
                val stateText = details.state
                val badgeColor = when (stateText.lowercase()) {
                    "preview" -> UpcomingColor
                    "in progress", "innings break" -> LiveColor
                    "complete", "finished" -> ResultColor
                    else -> TextSecondaryDark
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeColor.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = stateText.uppercase(),
                        color = badgeColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Progress bar during active background refresh
        if (isRefreshing) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = UpcomingColor,
                trackColor = Color.Transparent
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            if (isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = UpcomingColor,
                        modifier = Modifier.size(48.dp),
                        strokeWidth = 4.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Fetching match details...",
                        color = TextSecondaryDark,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else if (data == null) {
                androidx.compose.material3.AlertDialog(
                    onDismissRequest = { /* Keep dialog visible */ },
                    title = {
                        Text(
                            text = "Connection Error",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TextPrimaryDark
                        )
                    },
                    text = {
                        Text(
                            text = "Failed to load match details. Please check your network and try again.",
                            fontSize = 14.sp,
                            color = TextSecondaryDark
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = { viewModel.refreshMatchDetails() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = UpcomingColor,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Retry")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = onBack,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BorderDark,
                                contentColor = TextPrimaryDark
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Back")
                        }
                    },
                    containerColor = CardBgDark,
                    shape = RoundedCornerShape(16.dp)
                )
            } else {
                val details = data!!
                val isPreview = details.state.lowercase() == "preview"
                val isFinished = details.state.lowercase() == "complete" || details.state.lowercase() == "finished"
                val isInProgress = !isPreview && !isFinished

                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = { viewModel.refreshMatchDetails() },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // 1. Preview Layout: Match Info Card at Top
                        if (isPreview) {
                            MatchInfoCard(matchInfo = details.matchInfo)
                            
                            // Placeholder preview description card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, BorderDark),
                                colors = CardDefaults.cardColors(
                                    containerColor = CardBgDark,
                                    contentColor = TextPrimaryDark
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(18.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Match Preview",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimaryDark
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    val matchStartsText = if (details.matchInfo.startDate > 0) {
                                        "Match starts at ${formatStartDate(details.matchInfo.startDate)}"
                                    } else {
                                        details.status.ifEmpty { "This match hasn't started yet. Check back when the toss is announced!" }
                                    }
                                    
                                    Text(
                                        text = matchStartsText,
                                        fontSize = 14.sp,
                                        color = TextSecondaryDark,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        // 2. In Progress / Completed Layout: Score Summary at Top
                        if (!isPreview) {
                            val scoreDetails = details.matchScoreDetails
                            val live = details.live
                            if (scoreDetails != null && live == null) {
                                FinishedScoreCard(scoreDetails = scoreDetails)
                            } else if (live != null) {
                                ScoreSummaryCard(live = live, status = details.status)
                            }
                        }

                        // 3. Live Stats (Only during active game states)
                        val liveBlock = details.live
                        if (isInProgress && liveBlock != null) {
                            RecentBallsList(recentBalls = liveBlock.recentBalls)

                            BatterStatsTable(
                                striker = liveBlock.striker,
                                nonStriker = liveBlock.nonStriker
                            )

                            BowlerStatsTable(
                                bowlerStriker = liveBlock.bowlerStriker,
                                bowlerNonStriker = liveBlock.bowlerNonStriker
                            )

                            liveBlock.matchUdrs?.let { udrs ->
                                UdrsDetailsCard(
                                    matchUdrs = udrs,
                                    team1Name = details.matchInfo.team1.shortName,
                                    team2Name = details.matchInfo.team2.shortName
                                )
                            }
                        }

                        // 5. Match Info Card (Rendered at bottom for live & completed matches)
                        if (!isPreview) {
                            MatchInfoCard(matchInfo = details.matchInfo)
                        }
                    }
                }
            }
        }
    }
}
