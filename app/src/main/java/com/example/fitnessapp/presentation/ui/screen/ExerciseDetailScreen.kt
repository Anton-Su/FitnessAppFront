package com.example.fitnessapp.presentation.ui.screen

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.fitnessapp.domain.model.Exercise
import com.example.fitnessapp.presentation.ui.component.FitnessTopBar
import com.example.fitnessapp.presentation.viewmodel.FitnessViewModel
import com.example.fitnessapp.service.SecondsCounterService
import com.example.fitnessapp.worker.CaloriesUploadScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

/**
 * Экран деталей упражнения.
 */
@Composable
fun ExerciseDetailScreen(
    navController: NavHostController,
    exerciseId: Int,
    fromRecommendation: Boolean,
    viewModel: FitnessViewModel
) {
    var exercise by remember { mutableStateOf<Exercise?>(null) }
    var seconds by remember { mutableStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }
    var sessionSummary by remember { mutableStateOf<ExerciseSessionSummary?>(null) }
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val weight = viewModel.weight.collectAsState().value
    Log.e("ExerciseDetail", "Current weight: $weight kg, base calories per second: ${exercise?.caloriesBurnt} kcal/s")
    val met = (exercise?.caloriesBurnt ?: 0.0).takeIf { it > 0.0 } ?: 0.0
    val liveKcalPerMin = (met * weight * 3.5) / 200.0
    val liveKcal = (liveKcalPerMin * (seconds / 60.0)).coerceAtLeast(0.0)

    LaunchedEffect(exerciseId) {
        exercise = viewModel.loadExerciseById(exerciseId)
    }

    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == SecondsCounterService.ACTION_TICK) {
                    seconds = intent.getIntExtra(SecondsCounterService.EXTRA_SECONDS, 0)
                }
            }
        }

        val filter = IntentFilter(SecondsCounterService.ACTION_TICK)
        ContextCompat.registerReceiver(
            context,
            receiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    Scaffold(
        topBar = {
            FitnessTopBar(
                title = exercise?.name ?: "Упражнение",
                canNavigateBack = true,
                onBackClick = { navController.navigate("exercises") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Text(
                    text = exercise?.description ?: "Описание упражнения загружается с сервера...",
                    modifier = Modifier.padding(18.dp),
                    style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.SansSerif)
                )
                Text(
                    text = "Здесь могла бы быть реклама",
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                    style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
                Text(
                    text = exercise?.caloriesBurnt.toString() + " эффективных ккал в минуту",
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                    style = MaterialTheme.typography.bodyMedium.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                )
            }

            exercise?.let {
                Text(text = it.type.uppercase(), style = MaterialTheme.typography.bodyMedium)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Как надо",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                        )
                        Log.e("ExerciseDetail", "Exercise video URL: ${it.videoUrl}")
                        // Exercise video URL: https://www.youtube.com/watch?v=fdYHHvyT8kQ
                        ExerciseVideoPreview(videoUrl = it.videoUrl)
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = if (isRunning) "Секунды идут" else "Готово к старту",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = formatSeconds(seconds),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                    Text(
                        text = "Прямо сейчас: ${liveKcal.roundToInt()} ккал",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = "Калории считаются в реальном времени.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Button(
                onClick = {
                    seconds = 0
                    isRunning = true
                    sessionSummary = null
                    ContextCompat.startForegroundService(
                        context,
                        Intent(context, SecondsCounterService::class.java).apply {
                            action = SecondsCounterService.ACTION_START
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isRunning
            ) {
                Text("Выполнить упражнение")
            }

            Button(
                onClick = {
                    isRunning = false
                    val secs = seconds
                    seconds = 0
                    val minutes = secs / 60.0
                    val kcal = (liveKcalPerMin * minutes).coerceAtLeast(0.0)
                    val rounded = kcal.roundToInt()
                    if (secs > 0) {
                        sessionSummary = ExerciseSessionSummary(durationSeconds = secs, calories = rounded)
                    }
                    if (fromRecommendation && secs > 0) {
                        viewModel.markDoEverytimeTaskDone()
                    }

                    coroutineScope.launch {
                        try {
                            Log.e("ExerciseDetail", "Exercise completed: duration ${formatDurationHuman(secs)}, calories: $rounded kcal")
                            withContext(Dispatchers.IO) { viewModel.addCalories(rounded) }
                            CaloriesUploadScheduler.scheduleNext(context)
                        } catch (_: Exception) {
                            // Игнорируем ошибки расчёта/сохранения для UX без падений.
                        }
                    }

                    ContextCompat.startForegroundService(
                        context,
                        Intent(context, SecondsCounterService::class.java).apply {
                            action = SecondsCounterService.ACTION_STOP
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isRunning
            ) {
                Text("Закончить упражнение")
            }

            sessionSummary?.let { summary ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Круто!",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                        )
                        Text(
                            text = "Сжёг ${summary.calories} ккал за ${formatDurationHuman(summary.durationSeconds)}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Айда ещё",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun ExerciseVideoPreview(videoUrl: String) {
    val html = remember(videoUrl) {
        buildVideoPreviewHtml(videoUrl)
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            factory = { context ->
                WebView(context).apply {
                    setBackgroundColor(Color.TRANSPARENT)
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        mediaPlaybackRequiresUserGesture = false
                        cacheMode = WebSettings.LOAD_DEFAULT
                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        userAgentString = "Mozilla/5.0 (Linux; Android 12; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36"
                    }
                    webChromeClient = WebChromeClient()
                    webViewClient = WebViewClient()
                }
            },
            update = { webView ->
                // ВАЖНО: baseUrl должен быть https://www.youtube.com, иначе
                // YouTube IFrame API заблокирует запросы из-за file:// origin
                webView.loadDataWithBaseURL(
                    "https://www.youtube.com",
                    html,
                    "text/html",
                    "UTF-8",
                    null
                )
            }
        )
    }
}

private fun buildVideoPreviewHtml(videoUrl: String): String {
    val embedUrl = videoUrl.toYouTubeEmbedUrl()

    return if (embedUrl != null) {
        val videoId = embedUrl.substringAfterLast("/").substringBefore("?")
        """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    * { margin: 0; padding: 0; box-sizing: border-box; }
                    html, body { width: 100%; height: 100%; background: transparent; }
                    iframe { width: 100%; height: 100%; border: none; display: block; }
                </style>
            </head>
            <body>
                <iframe
                    src="https://www.youtube.com/embed/$videoId?playsinline=1&rel=0&modestbranding=1"
                    allowfullscreen
                    allow="autoplay; encrypted-media; fullscreen">
                </iframe>
            </body>
            </html>
        """.trimIndent()
    } else {
        """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    html, body {
                        margin: 0;
                        padding: 0;
                        width: 100%;
                        height: 100%;
                        background: transparent;
                        overflow: hidden;
                    }
                    video {
                        width: 100%;
                        height: 100%;
                        background: #000;
                    }
                </style>
            </head>
            <body>
                <video controls playsinline>
                    <source src="$videoUrl">
                    Ваш браузер не поддерживает встроенное видео.
                </video>
            </body>
            </html>
        """.trimIndent()
    }
}

private fun String.toYouTubeEmbedUrl(): String? {
    val normalized = trim()
    if (normalized.isEmpty()) return null

    val videoId = when {
        normalized.contains("youtu.be/") ->
            normalized.substringAfter("youtu.be/").substringBefore("?").substringBefore("&")
        normalized.contains("youtube.com/watch") ->
            Regex("[?&]v=([^&]+)").find(normalized)?.groupValues?.getOrNull(1)
        normalized.contains("youtube.com/embed/") ->
            normalized.substringAfter("youtube.com/embed/").substringBefore("?").substringBefore("&")
        else -> null
    }?.takeIf { it.isNotBlank() } ?: return null

    return "https://www.youtube.com/embed/$videoId"
}

private fun formatSeconds(seconds: Int): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return "%02d:%02d:%02d".format(h, m, s)
}

private data class ExerciseSessionSummary(
    val durationSeconds: Int,
    val calories: Int
)

private fun formatDurationHuman(seconds: Int): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return when {
        h > 0 -> "${h} ч ${m} мин ${s} сек"
        m > 0 -> "${m} мин ${s} сек"
        else -> "${s} сек"
    }
}