package com.jayanth.jcricket.ui.views.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jayanth.jcricket.ui.theme.BorderDark
import com.jayanth.jcricket.ui.theme.CardBgDark
import com.jayanth.jcricket.ui.theme.EventExtraColor
import com.jayanth.jcricket.ui.theme.EventFourColor
import com.jayanth.jcricket.ui.theme.EventSixColor
import com.jayanth.jcricket.ui.theme.EventWicketColor
import com.jayanth.jcricket.ui.theme.TextPrimaryDark
import com.jayanth.jcricket.ui.theme.TextSecondaryDark

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecentBallsList(
    recentBalls: List<String>,
    modifier: Modifier = Modifier
) {
    if (recentBalls.isEmpty()) return

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
            Text(
                text = "RECENT BALLS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondaryDark,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(BorderDark)
            )

            Spacer(modifier = Modifier.height(14.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                recentBalls.forEach { ball ->
                    val lowerBall = ball.lowercase()
                    val isWicket = lowerBall.contains("w") && !lowerBall.contains("wd")
                    val isFour = lowerBall == "4"
                    val isSix = lowerBall == "6"
                    val isExtra = lowerBall.contains("wd") || lowerBall.contains("nb")

                    val backgroundColor = when {
                        isWicket -> EventWicketColor
                        isFour -> EventFourColor
                        isSix -> EventSixColor
                        isExtra -> EventExtraColor
                        else -> BorderDark
                    }

                    val textColor = when {
                        isExtra -> Color.Black
                        isWicket || isFour || isSix -> Color.White
                        else -> TextPrimaryDark
                    }

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(backgroundColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = ball,
                            color = textColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
