package com.example.fitnessapp.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.example.fitnessapp.domain.model.Gender

/**
 * Красивый выбор пола с сегментированными кнопками.
 */
@Composable
fun GenderSelector(
    selectedGender: Gender,
    onGenderSelected: (Gender) -> Unit
) {
    val borderColor = MaterialTheme.colorScheme.outline
    val maleColor = Color(0xFF1976D2)  // Синий
    val femaleColor = Color(0xFFE91E63)  // Розовый
    val textColorSelected = Color.White
    val textColorUnselected = MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(2.dp, borderColor, RoundedCornerShape(14.dp))
    ) {

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(
                    if (selectedGender == Gender.FEMALE) femaleColor else Color.Transparent
                )
                .clickable { onGenderSelected(Gender.FEMALE) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Жен",
                color = if (selectedGender == Gender.FEMALE)
                    textColorSelected else textColorUnselected,
                fontWeight = FontWeight.Bold
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(
                    if (selectedGender == Gender.MALE) maleColor else Color.Transparent
                )
                .clickable { onGenderSelected(Gender.MALE) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Муж",
                color = if (selectedGender == Gender.MALE)
                    textColorSelected else textColorUnselected,
                fontWeight = FontWeight.Bold
            )
        }
    }
}