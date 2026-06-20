package com.jayanth.jcricket.ui.views

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.jayanth.jcricket.data.model.Match
import com.jayanth.jcricket.data.model.Team
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
import com.jayanth.jcricket.ui.views.components.formatStartDate

@Composable
fun MatchCard(
    match: Match,
    onClick: (Match) -> Unit,
    modifier: Modifier = Modifier
) {
    val isLive = match.statusType.lowercase() == "live"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(match) },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderDark),
        colors = CardDefaults.cardColors(
            containerColor = CardBgDark,
            contentColor = TextPrimaryDark
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            // Header Row: Series Name and badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = match.seriesName,
                    color = TextSecondaryDark,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Format Badge
                    val formatColor = when (match.matchFormat.uppercase()) {
                        "T20" -> FormatT20Color
                        "ODI" -> FormatOdiColor
                        "TEST" -> FormatTestColor
                        else -> UpcomingColor
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(formatColor.copy(alpha = 0.15f))
                            .border(BorderStroke(1.dp, formatColor.copy(alpha = 0.5f)))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = match.matchFormat,
                            color = formatColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Status Badge
                    val statusBadgeColor = when (match.statusType.lowercase()) {
                        "live" -> LiveColor
                        "upcoming" -> UpcomingColor
                        "completed", "result" -> ResultColor
                        else -> TextSecondaryDark
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(statusBadgeColor.copy(alpha = 0.12f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        if (isLive) {
                            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                            val alpha by infiniteTransition.animateFloat(
                                initialValue = 0.3f,
                                targetValue = 1f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(800, easing = LinearEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "alpha"
                            )
                            Box(
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(LiveColor.copy(alpha = alpha))
                            )
                        }
                        Text(
                            text = match.statusType,
                            color = statusBadgeColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Teams & Scores Columns
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TeamRow(team = match.team1, score = match.score.team1)
                TeamRow(team = match.team2, score = match.score.team2)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Divider Line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(BorderDark)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Footer / Match Status
            val footerText = if (match.state == "Preview" && match.startDate > 0) {
                "Match starts at ${formatStartDate(match.startDate)}"
            } else {
                match.status
            }

            Text(
                text = footerText,
                color = if (isLive) LiveColor else TextSecondaryDark,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun TeamRow(team: Team, score: String?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // Team Icon with coil loader and error handling
            if (!team.icon.isNullOrEmpty()) {
                SubcomposeAsyncImage(
                    model = team.icon,
                    contentDescription = team.name,
                    loading = {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(BorderDark)
                        )
                    },
                    error = {
                        // Error callback: displays nothing (hides image container, matches CSS error)
                    },
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(BgDark)
                        .padding(2.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = team.shortName,
                color = TextPrimaryDark,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = score ?: "-",
            color = TextPrimaryDark,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}
