package com.jayanth.jcricket.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jayanth.jcricket.data.model.Match
import com.jayanth.jcricket.ui.theme.BgDark
import com.jayanth.jcricket.ui.theme.CardBgDark
import com.jayanth.jcricket.ui.theme.LiveColor
import com.jayanth.jcricket.ui.theme.TextPrimaryDark
import com.jayanth.jcricket.ui.theme.TextSecondaryDark
import com.jayanth.jcricket.ui.theme.UpcomingColor
import com.jayanth.jcricket.ui.viewmodel.DashboardUiState
import com.jayanth.jcricket.ui.viewmodel.MatchViewModel
import com.jayanth.jcricket.ui.views.components.getRelativeTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MatchViewModel,
    onMatchClick: (Match) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.dashboardUiState.collectAsState()
    val isRefreshing by viewModel.refreshingMatches.collectAsState()
    val timeTick by viewModel.timeTick.collectAsState()

    val scoreRunsBrush = remember {
        Brush.linearGradient(
            colors = listOf(Color(0xFF60A5FA), Color(0xFFA78BFA))
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        // App Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(CardBgDark)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append("Cricket ")
                            withStyle(SpanStyle(brush = scoreRunsBrush)) {
                                append("Scores")
                            }
                        },
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimaryDark
                    )
                    Text(
                        text = "Real-time live cricket scores",
                        fontSize = 13.sp,
                        color = TextSecondaryDark,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    
                    // Relocated: Small relative sync text below subtitle
                    if (uiState is DashboardUiState.Success) {
                        val successState = uiState as DashboardUiState.Success
                        val syncLabel = remember(successState.lastUpdated, timeTick) {
                            getRelativeTime(successState.lastUpdated)
                        }
                        Text(
                            text = "Last updated: $syncLabel",
                            fontSize = 11.sp,
                            color = TextSecondaryDark.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 4.dp),
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
        }

        // Progress bar below the header during active background syncing
        if (isRefreshing) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = UpcomingColor,
                trackColor = Color.Transparent
            )
        }

        // Content Area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when (val state = uiState) {
                is DashboardUiState.Loading -> {
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
                            text = "Fetching matches...",
                            color = TextSecondaryDark,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                is DashboardUiState.Error -> {
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
                                text = state.message,
                                fontSize = 14.sp,
                                color = TextSecondaryDark
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = { viewModel.fetchMatches() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = UpcomingColor,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Retry")
                            }
                        },
                        containerColor = CardBgDark,
                        shape = RoundedCornerShape(16.dp)
                    )
                }

                is DashboardUiState.Success -> {
                    PullToRefreshBox(
                        isRefreshing = isRefreshing,
                        onRefresh = { viewModel.fetchMatches(isRefresh = true) },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (state.matches.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No matches scheduled.",
                                    color = TextSecondaryDark,
                                    fontSize = 15.sp
                                )
                            }
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(state.matches, key = { it.matchId }) { match ->
                                    MatchCard(
                                        match = match,
                                        onClick = onMatchClick
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
