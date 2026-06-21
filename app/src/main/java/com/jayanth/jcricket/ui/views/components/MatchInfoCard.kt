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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.jayanth.jcricket.data.model.MatchInfo
import com.jayanth.jcricket.data.model.Team
import com.jayanth.jcricket.ui.theme.BgDark
import com.jayanth.jcricket.ui.theme.BorderDark
import com.jayanth.jcricket.ui.theme.CardBgDark
import com.jayanth.jcricket.ui.theme.TextPrimaryDark
import com.jayanth.jcricket.ui.theme.TextSecondaryDark
import com.jayanth.jcricket.ui.theme.UpcomingColor

@Composable
fun MatchInfoCard(
    matchInfo: MatchInfo,
    modifier: Modifier = Modifier
) {
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
            // Card title
            Text(
                text = "MATCH INFO",
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

            // Series Name & Description
            Text(
                text = matchInfo.seriesName,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = UpcomingColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            val desc = matchInfo.matchDesc
            if (!desc.isNullOrEmpty()) {
                Text(
                    text = desc,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Teams display (VS row)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamBlock(team = matchInfo.team1, modifier = Modifier.weight(1f))
                
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(BorderDark)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "VS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondaryDark
                    )
                }

                TeamBlock(team = matchInfo.team2, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Meta Info Grid (Format, Type, Venue, Date)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(BgDark.copy(alpha = 0.5f))
                    .border(BorderStroke(1.dp, BorderDark), RoundedCornerShape(8.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    MetaItem(label = "FORMAT", value = matchInfo.matchFormat, modifier = Modifier.weight(1f))
                    MetaItem(label = "TYPE", value = matchInfo.matchType, modifier = Modifier.weight(1f))
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    MetaItem(label = "VENUE", value = matchInfo.venueText, modifier = Modifier.weight(1f))
                }
                val dateText = matchInfo.startDateText
                if (!dateText.isNullOrEmpty()) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        MetaItem(label = "DATE", value = dateText, modifier = Modifier.weight(1f))
                    }
                }
            }

            // Toss Banner
            val toss = matchInfo.toss
            if (toss != null && toss.winner.isNotEmpty() && toss.decision.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(UpcomingColor.copy(alpha = 0.12f))
                        .border(BorderStroke(1.dp, UpcomingColor.copy(alpha = 0.4f)), RoundedCornerShape(8.dp))
                        .padding(10.dp, 12.dp)
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Toss: ")
                            }
                            append("${toss.winner} won the toss and elected to ${toss.decision.lowercase()}")
                        },
                        fontSize = 13.sp,
                        color = TextPrimaryDark,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun TeamBlock(team: Team, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!team.icon.isNullOrEmpty()) {
            SubcomposeAsyncImage(
                model = team.icon,
                contentDescription = team.name,
                loading = {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(BorderDark)
                    )
                },
                error = {
                    // Display nothing on image error
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(BgDark)
                    .padding(4.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
        }
        Text(
            text = team.shortName,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun MetaItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondaryDark,
            letterSpacing = 0.5.sp
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimaryDark,
            modifier = Modifier.padding(top = 2.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
