package com.example.fitnessapp.presentation.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitnessapp.domain.model.Exercise

@Composable
fun ExerciseCard(exercise: Exercise, onOpen: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = exercise.name,
                style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold)
            )
            Text(
                text = exercise.description,
                style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.SansSerif)
            )
            Text(
                text = "Тип: ${exercise.type}",
                style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.primary)
            )
            Button(onClick = onOpen) {
                Text("Открыть детали")
            }
        }
    }
}