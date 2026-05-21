package com.example.fitnessapp.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Красивый сегментированный переключатель для статуса активности.
 * Показывает "В норме" и "Отдыхаю" как две кнопки.
 */
@Composable
fun ActivityStatusToggle(
    isActive: Boolean,
    onStatusChange: (Boolean) -> Unit
) {
    val borderColor = MaterialTheme.colorScheme.outline
    val activeColor = Color(0xFF2E7D32)  // Зелёный
    val restColor = Color(0xFF1565C0)    // Синий
    val textColorSelected = Color.White
    val textColorUnselected = MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(2.dp, borderColor, RoundedCornerShape(14.dp))
    ) {

        // "В норме" button
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(
                    if (isActive) activeColor else Color.Transparent
                )
                .clickable { onStatusChange(true) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                "В норме",
                color = if (isActive)
                    textColorSelected else textColorUnselected,
                fontWeight = FontWeight.Bold
            )
        }

        // "Отдыхаю" button
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(
                    if (!isActive) restColor else Color.Transparent
                )
                .clickable { onStatusChange(false) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Отдыхаю",
                color = if (!isActive)
                    textColorSelected else textColorUnselected,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

