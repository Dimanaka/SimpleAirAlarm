package com.simpleairalarm.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simpleairalarm.R
import com.simpleairalarm.domain.model.AlertLevel
import com.simpleairalarm.domain.model.AlertType

@Composable
fun AlertStatusCard(
    regionName: String,
    alertLevel: AlertLevel,
    alertType: AlertType,
    onChangeRegionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (cardBgColor, contentColor, titleText, iconRes) = when (alertLevel) {
        AlertLevel.RED -> Quadruple(
            Color(0xFFD32F2F),
            Color.White,
            "🔴 ПОНІТРЯНА ТРИВОГА",
            R.drawable.ic_launcher_foreground
        )
        AlertLevel.YELLOW -> Quadruple(
            Color(0xFFFBC02D),
            Color.Black,
            "🟡 ПІДВИЩЕНА НЕБЕЗПЕКА",
            R.drawable.ic_launcher_foreground
        )
        AlertLevel.NONE -> Quadruple(
            Color(0xFF2E7D32),
            Color.White,
            "🟢 ТРИВОГИ НЕМАЄ",
            R.drawable.ic_launcher_foreground
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBgColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = contentColor
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = titleText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                    Text(
                        text = regionName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = contentColor
                    )
                }
            }

            if (alertLevel != AlertLevel.NONE) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Загроза: ${alertType.titleUa}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onChangeRegionClick,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Змінити регіон")
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
