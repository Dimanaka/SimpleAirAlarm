package com.simpleairalarm.ui.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.simpleairalarm.MainActivity
import com.simpleairalarm.data.repository.UserPreferencesRepository
import com.simpleairalarm.domain.model.AlertLevel
import kotlinx.coroutines.flow.first

class SimpleAirAlarmWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val userPreferencesRepository = UserPreferencesRepository(context)
        val prefs = userPreferencesRepository.userPreferencesFlow.first()

        val regionName = prefs.selectedRegionName.ifEmpty { "Київська область" }
        val alertLevel = AlertLevel.NONE

        provideContent {
            WidgetContent(
                regionName = regionName,
                alertLevel = alertLevel
            )
        }
    }

    @Composable
    private fun WidgetContent(
        regionName: String,
        alertLevel: AlertLevel
    ) {
        val (bgColor, statusText) = when (alertLevel) {
            AlertLevel.RED -> Color(0xFFD32F2F) to "🔴 Повітряна тривога"
            AlertLevel.YELLOW -> Color(0xFFFBC02D) to "🟡 Підвищена загроза"
            AlertLevel.NONE -> Color(0xFF2E7D32) to "🟢 Тривоги немає"
        }

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(bgColor)
                .padding(12.dp)
                .clickable(actionStartActivity<MainActivity>()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = regionName,
                style = TextStyle(
                    color = ColorProvider(Color.White, Color.White),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = GlanceModifier.height(4.dp))
            Text(
                text = statusText,
                style = TextStyle(
                    color = ColorProvider(Color.White, Color.White),
                    fontSize = 14.sp
                )
            )
        }
    }
}

class SimpleAirAlarmWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = SimpleAirAlarmWidget()
}
