package com.jayanth.jcricket.ui.views.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jayanth.jcricket.data.model.BatterInfo
import com.jayanth.jcricket.data.model.BowlerInfo
import com.jayanth.jcricket.ui.theme.BorderDark
import com.jayanth.jcricket.ui.theme.CardBgDark
import com.jayanth.jcricket.ui.theme.ResultColor
import com.jayanth.jcricket.ui.theme.TextPrimaryDark
import com.jayanth.jcricket.ui.theme.TextSecondaryDark
import java.util.Locale

@Composable
fun BatterStatsTable(
    striker: BatterInfo?,
    nonStriker: BatterInfo?,
    modifier: Modifier = Modifier
) {
    val batters = listOfNotNull(
        striker?.let { it to true },
        nonStriker?.let { it to false }
    )

    if (batters.isEmpty()) return

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
                text = "BATTING",
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

            Spacer(modifier = Modifier.height(10.dp))

            // Table Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Batter", modifier = Modifier.weight(2.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark)
                Text(text = "R", modifier = Modifier.weight(1f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark, textAlign = TextAlign.End)
                Text(text = "B", modifier = Modifier.weight(1f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark, textAlign = TextAlign.End)
                Text(text = "4s", modifier = Modifier.weight(1f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark, textAlign = TextAlign.End)
                Text(text = "6s", modifier = Modifier.weight(1f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark, textAlign = TextAlign.End)
                Text(text = "S/R", modifier = Modifier.weight(1.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark, textAlign = TextAlign.End)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(BorderDark)
            )

            // Table Rows
            batters.forEach { (batter, isStriker) ->
                val rowBackground = if (isStriker) ResultColor.copy(alpha = 0.05f) else Color.Transparent
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(rowBackground)
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(2.5f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = batter.name,
                            fontSize = 13.sp,
                            fontWeight = if (isStriker) FontWeight.Bold else FontWeight.Medium,
                            color = TextPrimaryDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (isStriker) {
                            Text(
                                text = "*",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = ResultColor,
                                modifier = Modifier.padding(start = 2.dp)
                            )
                        }
                    }
                    Text(text = batter.runs.toString(), modifier = Modifier.weight(1f), fontSize = 13.sp, color = TextPrimaryDark, textAlign = TextAlign.End, fontFamily = FontFamily.Monospace)
                    Text(text = batter.balls.toString(), modifier = Modifier.weight(1f), fontSize = 13.sp, color = TextPrimaryDark, textAlign = TextAlign.End, fontFamily = FontFamily.Monospace)
                    Text(text = batter.fours.toString(), modifier = Modifier.weight(1f), fontSize = 13.sp, color = TextPrimaryDark, textAlign = TextAlign.End, fontFamily = FontFamily.Monospace)
                    Text(text = batter.sixes.toString(), modifier = Modifier.weight(1f), fontSize = 13.sp, color = TextPrimaryDark, textAlign = TextAlign.End, fontFamily = FontFamily.Monospace)
                    
                    val formattedSr = String.format(Locale.US, "%.2f", batter.strikeRate)
                    Text(text = formattedSr, modifier = Modifier.weight(1.5f), fontSize = 13.sp, color = TextPrimaryDark, textAlign = TextAlign.End, fontFamily = FontFamily.Monospace)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(BorderDark.copy(alpha = 0.5f))
                )
            }
        }
    }
}

@Composable
fun BowlerStatsTable(
    bowlerStriker: BowlerInfo?,
    bowlerNonStriker: BowlerInfo?,
    modifier: Modifier = Modifier
) {
    val bowlers = listOfNotNull(
        bowlerStriker?.let { it to true },
        bowlerNonStriker?.let { it to false }
    )

    if (bowlers.isEmpty()) return

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
                text = "BOWLING",
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

            Spacer(modifier = Modifier.height(10.dp))

            // Table Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Bowler", modifier = Modifier.weight(2.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark)
                Text(text = "O", modifier = Modifier.weight(1f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark, textAlign = TextAlign.End)
                Text(text = "M", modifier = Modifier.weight(1f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark, textAlign = TextAlign.End)
                Text(text = "R", modifier = Modifier.weight(1f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark, textAlign = TextAlign.End)
                Text(text = "W", modifier = Modifier.weight(1f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark, textAlign = TextAlign.End)
                Text(text = "Econ", modifier = Modifier.weight(1.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark, textAlign = TextAlign.End)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(BorderDark)
            )

            // Table Rows
            bowlers.forEach { (bowler, isActive) ->
                val rowBackground = if (isActive) ResultColor.copy(alpha = 0.05f) else Color.Transparent
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(rowBackground)
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(2.5f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = bowler.name,
                            fontSize = 13.sp,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                            color = TextPrimaryDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (isActive) {
                            Text(
                                text = "*",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = ResultColor,
                                modifier = Modifier.padding(start = 2.dp)
                            )
                        }
                    }
                    Text(text = bowler.overs.toString(), modifier = Modifier.weight(1f), fontSize = 13.sp, color = TextPrimaryDark, textAlign = TextAlign.End, fontFamily = FontFamily.Monospace)
                    Text(text = bowler.maidens.toString(), modifier = Modifier.weight(1f), fontSize = 13.sp, color = TextPrimaryDark, textAlign = TextAlign.End, fontFamily = FontFamily.Monospace)
                    Text(text = bowler.runs.toString(), modifier = Modifier.weight(1f), fontSize = 13.sp, color = TextPrimaryDark, textAlign = TextAlign.End, fontFamily = FontFamily.Monospace)
                    Text(text = bowler.wickets.toString(), modifier = Modifier.weight(1f), fontSize = 13.sp, color = TextPrimaryDark, textAlign = TextAlign.End, fontFamily = FontFamily.Monospace)
                    
                    val formattedEcon = String.format(Locale.US, "%.2f", bowler.economy)
                    Text(text = formattedEcon, modifier = Modifier.weight(1.5f), fontSize = 13.sp, color = TextPrimaryDark, textAlign = TextAlign.End, fontFamily = FontFamily.Monospace)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(BorderDark.copy(alpha = 0.5f))
                )
            }
        }
    }
}
