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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jayanth.jcricket.data.model.UdrsInfo
import com.jayanth.jcricket.ui.theme.BorderDark
import com.jayanth.jcricket.ui.theme.CardBgDark
import com.jayanth.jcricket.ui.theme.TextPrimaryDark
import com.jayanth.jcricket.ui.theme.TextSecondaryDark
import com.jayanth.jcricket.ui.theme.UpcomingColor

@Composable
fun UdrsDetailsCard(
    matchUdrs: UdrsInfo,
    team1Name: String,
    team2Name: String,
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
            Text(
                text = "DRS REVIEWS REMAINING",
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Team 1 DRS Box
                DrsTeamBox(
                    teamName = team1Name,
                    remaining = matchUdrs.team1Remaining,
                    successful = matchUdrs.team1Successful,
                    unsuccessful = matchUdrs.team1Unsuccessful,
                    modifier = Modifier.weight(1f)
                )

                // Team 2 DRS Box
                DrsTeamBox(
                    teamName = team2Name,
                    remaining = matchUdrs.team2Remaining,
                    successful = matchUdrs.team2Successful,
                    unsuccessful = matchUdrs.team2Unsuccessful,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun DrsTeamBox(
    teamName: String,
    remaining: Int,
    successful: Int,
    unsuccessful: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .border(BorderStroke(1.dp, BorderDark), RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = teamName,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimaryDark,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = remaining.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = UpcomingColor,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            text = "S: $successful | U: $unsuccessful",
            fontSize = 11.sp,
            color = TextSecondaryDark,
            modifier = Modifier.padding(top = 4.dp),
            textAlign = TextAlign.Center
        )
    }
}
