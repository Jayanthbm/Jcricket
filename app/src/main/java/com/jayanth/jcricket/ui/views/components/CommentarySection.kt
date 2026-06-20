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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jayanth.jcricket.data.model.CommentaryItem
import com.jayanth.jcricket.ui.theme.BorderDark
import com.jayanth.jcricket.ui.theme.CardBgDark
import com.jayanth.jcricket.ui.theme.EventFourColor
import com.jayanth.jcricket.ui.theme.EventSixColor
import com.jayanth.jcricket.ui.theme.EventWicketColor
import com.jayanth.jcricket.ui.theme.TextPrimaryDark
import com.jayanth.jcricket.ui.theme.TextSecondaryDark

@Composable
fun CommentarySection(
    commentary: List<CommentaryItem>,
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
                text = "COMMENTARY",
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

            if (commentary.isEmpty()) {
                Text(
                    text = "No commentary details available yet.",
                    color = TextSecondaryDark,
                    fontSize = 13.sp
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    commentary.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Left side: Over number & event tags
                            Column(
                                modifier = Modifier.padding(end = 12.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                item.over?.let { overNum ->
                                    Text(
                                        text = overNum.toString(),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimaryDark,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                
                                val event = item.event
                                if (!event.isNullOrEmpty() && event != "NONE") {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    val badgeColor = when (event.uppercase()) {
                                        "WICKET" -> EventWicketColor
                                        "FOUR" -> EventFourColor
                                        "SIX" -> EventSixColor
                                        else -> BorderDark
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(badgeColor)
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = event,
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            // Right side: commentary description
                            Text(
                                text = item.text,
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                color = TextPrimaryDark,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (index < commentary.lastIndex) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .height(0.5.dp)
                                    .background(BorderDark.copy(alpha = 0.5f))
                            )
                        }
                    }
                }
            }
        }
    }
}
