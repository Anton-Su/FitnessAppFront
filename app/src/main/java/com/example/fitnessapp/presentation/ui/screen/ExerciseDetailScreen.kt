package com.example.fitnessapp.presentation.ui.screen
import coil.compose.AsyncImage

import androidx.compose.ui.graphics.Color
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import androidx.core.net.toUri

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
    val isRunning by viewModel.isRunning.collectAsState()
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
                when (intent?.action) {
                    SecondsCounterService.ACTION_TICK -> {
                        seconds = intent.getIntExtra(SecondsCounterService.EXTRA_SECONDS, 0)
                        viewModel.setExerciseRunning(true)
                    }
                    SecondsCounterService.ACTION_STATE -> {
                        seconds = intent.getIntExtra(SecondsCounterService.EXTRA_SECONDS, seconds)
                        viewModel.setExerciseRunning(
                            intent.getBooleanExtra(SecondsCounterService.EXTRA_IS_RUNNING, false)
                        )
                    }
                }
            }
        }

        val filter = IntentFilter().apply {
            addAction(SecondsCounterService.ACTION_TICK)
            addAction(SecondsCounterService.ACTION_STATE)
        }
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
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    // Заголовок секции описания
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(4.dp, 20.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(2.dp)
                                )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Об упражнении",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 0.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    // Описание
                    Text(
                        text = exercise?.description ?: "Описание упражнения загружается с сервера...",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            lineHeight = 24.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "${exercise?.caloriesBurnt} ккал / мин",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                            Text(
                                text = "эффективный расход энергии",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.65f)
                                )
                            )
                        }
                    }
                    Text(
                        text = "Здесь могла бы быть реклама, но мы особенные и не хотим её показывать. Вместо этого мы просто расскажем тебе, что она могла быть.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                            letterSpacing = 0.3.sp
                        )
                    )
                }
            }
            exercise?.let {
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
                    viewModel.setExerciseRunning(true)
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
                    viewModel.setExerciseRunning(false)
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
                            text = "Сожжено ${summary.calories} ккал за ${formatDurationHuman(summary.durationSeconds)}",
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

//@SuppressLint("SetJavaScriptEnabled")
//@Composable
//private fun ExerciseVideoPreview(videoUrl: String) {
//    val html = remember(videoUrl) {
//        buildVideoPreviewHtml(videoUrl)
//    }
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(20.dp),
//        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
//    ) {
//        AndroidView(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(220.dp),
//            factory = { context ->
//                WebView(context).apply {
//                    setBackgroundColor(Color.TRANSPARENT)
//                    settings.apply {
//                        javaScriptEnabled = true
//                        domStorageEnabled = true
//                        mediaPlaybackRequiresUserGesture = false
//                        cacheMode = WebSettings.LOAD_DEFAULT
//                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
//                        userAgentString = "Mozilla/5.0 (Linux; Android 12; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36"
//                    }
//                    webChromeClient = WebChromeClient()
//                    webViewClient = WebViewClient()
//                }
//            },
//            update = { webView ->
//                // ВАЖНО: baseUrl должен быть https://www.youtube.com, иначе
//                // YouTube IFrame API заблокирует запросы из-за file:// origin
//                webView.loadDataWithBaseURL(
//                    "https://www.youtube.com",
//                    html,
//                    "text/html",
//                    "UTF-8",
//                    null
//                )
//            }
//        )
//    }
//}

//private fun buildVideoPreviewHtml(videoUrl: String): String {
//    val embedUrl = videoUrl.toYouTubeEmbedUrl()
//
//    return if (embedUrl != null) {
//        val videoId = embedUrl.substringAfterLast("/").substringBefore("?")
//        """
//            <!DOCTYPE html>
//            <html>
//            <head>
//                <meta charset="UTF-8">
//                <meta name="viewport" content="width=device-width, initial-scale=1.0">
//                <style>
//                    * { margin: 0; padding: 0; box-sizing: border-box; }
//                    html, body { width: 100%; height: 100%; background: transparent; }
//                    iframe { width: 100%; height: 100%; border: none; display: block; }
//                </style>
//            </head>
//            <body>
//                <iframe
//                    src="https://www.youtube.com/embed/$videoId?playsinline=1&rel=0&modestbranding=1"
//                    allowfullscreen
//                    allow="autoplay; encrypted-media; fullscreen">
//                </iframe>
//            </body>
//            </html>
//        """.trimIndent()
//    } else {
//        """
//            <!DOCTYPE html>
//            <html>
//            <head>
//                <meta name="viewport" content="width=device-width, initial-scale=1.0">
//                <style>
//                    html, body {
//                        margin: 0;
//                        padding: 0;
//                        width: 100%;
//                        height: 100%;
//                        background: transparent;
//                        overflow: hidden;
//                    }
//                    video {
//                        width: 100%;
//                        height: 100%;
//                        background: #000;
//                    }
//                </style>
//            </head>
//            <body>
//                <video controls playsinline>
//                    <source src="$videoUrl">
//                    Ваш браузер не поддерживает встроенное видео.
//                </video>
//            </body>
//            </html>
//        """.trimIndent()
//    }
//}

//private fun String.toYouTubeEmbedUrl(): String? {
//    val normalized = trim()
//    if (normalized.isEmpty()) return null
//
//    val videoId = when {
//        normalized.contains("youtu.be/") ->
//            normalized.substringAfter("youtu.be/").substringBefore("?").substringBefore("&")
//        normalized.contains("youtube.com/watch") ->
//            Regex("[?&]v=([^&]+)").find(normalized)?.groupValues?.getOrNull(1)
//        normalized.contains("youtube.com/embed/") ->
//            normalized.substringAfter("youtube.com/embed/").substringBefore("?").substringBefore("&")
//        else -> null
//    }?.takeIf { it.isNotBlank() } ?: return null
//
//    return "https://www.youtube.com/embed/$videoId"
//}

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



@Composable
private fun ExerciseVideoPreview(videoUrl: String) {
    val context = LocalContext.current
    val videoId = remember(videoUrl) { extractYouTubeId(videoUrl) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        if (videoId != null) {
            val thumbnailUrl = "https://img.youtube.com/vi/$videoId/hqdefault.jpg"

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, videoUrl.toUri())
                        context.startActivity(intent)
                    }
            ) {
                AsyncImage(
                    model = thumbnailUrl,
                    contentDescription = "Превью видео",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                )
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Смотреть",
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.Center)
                )
                Text(
                    text = "Смотреть на YouTube",
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Видео недоступно", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

private fun extractYouTubeId(url: String): String? {
    val patterns = listOf(
        Regex("youtu\\.be/([\\w-]{11})"),
        Regex("[?&]v=([\\w-]{11})"),
        Regex("embed/([\\w-]{11})")
    )
    return patterns.firstNotNullOfOrNull { it.find(url)?.groupValues?.get(1) }
}