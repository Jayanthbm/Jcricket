package com.jayanth.jcricket.ui.views.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jayanth.jcricket.data.model.LiveInfo
import com.jayanth.jcricket.ui.theme.BorderDark
import com.jayanth.jcricket.ui.theme.CardBgDark
import com.jayanth.jcricket.ui.theme.LiveColor
import com.jayanth.jcricket.ui.theme.ResultColor
import com.jayanth.jcricket.ui.theme.TextPrimaryDark
import com.jayanth.jcricket.ui.theme.TextSecondaryDark

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScoreSummaryCard(
    live: LiveInfo,
    status: String,
    modifier: Modifier = Modifier
) {
    val scoreRunsBrush = remember {
        Brush.linearGradient(
            colors = listOf(Color(0xFF60A5FA), Color(0xFFA78BFA))
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
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
            // Main score readout
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = buildAnnotatedString {
                            if (live.battingTeam.isNotEmpty()) {
                                append("${live.battingTeam} ")
                            }
                            withStyle(SpanStyle(brush = scoreRunsBrush)) {
                                append(live.currentScore)
                            }
                        },
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimaryDark
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "(${live.overs} overs)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondaryDark,
                        modifier = Modifier.padding(bottom = 4.dp),
                        fontFamily = FontFamily.Monospace
                    )
                }

                if (status.isNotEmpty()) {
                    Text(
                        text = status,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ResultColor,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(BorderDark)
            )
            Spacer(modifier = Modifier.height(14.dp))

            // Details row (Run rates, partnership, etc.)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (live.runRate > 0.0) {
                    ScoreDetailItem(label = "RUN RATE", value = live.runRate.toString())
                }
                if (live.requiredRunRate > 0.0) {
                    ScoreDetailItem(label = "REQ. RR", value = live.requiredRunRate.toString())
                }
                if (live.remRunsToWin > 0) {
                    ScoreDetailItem(label = "RUNS TO WIN", value = live.remRunsToWin.toString())
                }
                if (!live.partnershipText.isNullOrEmpty() && live.partnershipText != "0 (0)") {
                    ScoreDetailItem(label = "PARTNERSHIP", value = live.partnershipText)
                }
            }

            // Last Wicket Banner
            val lastWicket = live.lastWicket
            if (!lastWicket.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
                        .background(LiveColor.copy(alpha = 0.05f))
                        .border(
                            BorderStroke(1.dp, BorderDark),
                            RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                        )
                ) {
                    // Red indicator line on left
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(54.dp)
                            .background(LiveColor)
                    )
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "LAST WICKET",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondaryDark,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = lastWicket,
                            fontSize = 12.sp,
                            color = TextPrimaryDark,
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreDetailItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondaryDark,
            letterSpacing = 0.5.sp
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark,
            modifier = Modifier.padding(top = 2.dp),
            fontFamily = FontFamily.Monospace
        )
    }
}
