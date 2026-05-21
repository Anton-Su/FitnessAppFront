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
import com.example.fitnessapp.data.remote.RetrofitClient
import com.example.fitnessapp.data.remote.dto.ActivityRequest
import com.example.fitnessapp.data.preferences.TokenManager
import com.example.fitnessapp.data.preferences.SettingsDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt
import java.time.LocalDate

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
//    val met = remember(exercise?.type) {
//        when (exercise?.type?.lowercase()) {
//            "кардио", "cardio" -> 8.0
//            "силовая", "strength" -> 6.0
//            "йога", "yoga" -> 3.0
//            "растяжка", "stretching" -> 2.0
//            else -> 4.0
//        }
//    }
    val liveKcalPerMin = (met * weight * 3.5) / 200.0
    val liveKcal = (liveKcalPerMin * (seconds / 60.0)).coerceAtLeast(0.0)
//    val liveKcal = (liveKcalPerSecond * seconds).coerceAtLeast(0.0)
//    Log.e("ExerciseDetail", "Live kcal per second: $liveKcalPerSecond, total live kcal: $liveKcal")
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
                        viewModel.addCalories(rounded)

                        withContext(Dispatchers.IO) {
                            try {
                                val settings = SettingsDataStore(context)
                                val tokenManager = TokenManager(context)
                                RetrofitClient.init(context)
                                tokenManager.loadTokens()
                                val steps = settings.stepsFlow.first()
                                val caloriesNow = settings.caloriesFlow.first()
                                if (caloriesNow > 0) {
                                    RetrofitClient.authApi.createActivity(
                                        request = ActivityRequest(
                                            activity_date = LocalDate.now().toString(),
                                            steps = steps,
                                            burnt = caloriesNow,
                                            goal_achieved = false
                                        )
                                    )
                                    settings.setCalories(0)
                                }
                            } catch (_: Exception) {
                                // Отправка не удалась: локальный счётчик уже обновлён.
                            }
                        }
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
                        text = "Ты красавчик!",
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
    Log.e("ExerciseVideo", "Original URL: $videoUrl")
    val context = LocalContext.current
    val html = remember(videoUrl) {
        val built = buildVideoPreviewHtml(videoUrl)
        Log.e("ExerciseVideo", "HTML built, length: ${built.length}")
        built
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
                        javaScriptCanOpenWindowsAutomatically = true
                        domStorageEnabled = true
                        mediaPlaybackRequiresUserGesture = false
                        cacheMode = WebSettings.LOAD_DEFAULT
                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        userAgentString = "Mozilla/5.0 (Linux; Android 12; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36"
                        allowFileAccess = true
                        allowContentAccess = true
                        builtInZoomControls = true
                    }
                    webChromeClient = WebChromeClient()
                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            Log.e("ExerciseVideo", "WebView loading: $url")
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            Log.e("ExerciseVideo", "WebView finished: $url")
                        }

                        override fun shouldInterceptRequest(view: WebView?, request: android.webkit.WebResourceRequest?): android.webkit.WebResourceResponse? {
                            val url = request?.url.toString()
                            Log.e("ExerciseVideo", "Resource request: $url, isForMainFrame: ${request?.isForMainFrame}")
                            return super.shouldInterceptRequest(view, request)
                        }

                        override fun onReceivedError(view: WebView?, request: android.webkit.WebResourceRequest?, error: android.webkit.WebResourceError?) {
                            super.onReceivedError(view, request, error)
                            Log.e("ExerciseVideo", "WebView error: ${error?.description} for ${request?.url}")
                        }

                        override fun onReceivedHttpError(view: WebView?, request: android.webkit.WebResourceRequest?, errorResponse: android.webkit.WebResourceResponse?) {
                            super.onReceivedHttpError(view, request, errorResponse)
                            Log.e("ExerciseVideo", "HTTP error: statusCode=${errorResponse?.statusCode} for ${request?.url}")
                        }
                    }
                }
            },
            update = { webView ->
                Log.e("ExerciseVideo", "Loading HTML into WebView")
                Log.e("ExerciseVideo", "Context cache dir: ${context.cacheDir}")
                try {
                    val cacheDir = context.cacheDir
                    val htmlFile = java.io.File(cacheDir, "video_player.html")
                    htmlFile.writeText(html)
                    Log.e("ExerciseVideo", "File written successfully: ${htmlFile.absolutePath}")
                    val fileUrl = "file://" + htmlFile.absolutePath
                    Log.e("ExerciseVideo", "Loading from: $fileUrl")
                    webView.loadUrl(fileUrl)
                    Log.e("ExerciseVideo", "loadUrl called successfully")
                } catch (e: Exception) {
                    Log.e("ExerciseVideo", "Error writing HTML file or loading: ${e.message}, ${e.stackTraceToString()}")
                    Log.e("ExerciseVideo", "Falling back to loadDataWithBaseURL with empty baseURL")
                    webView.loadDataWithBaseURL(
                        "",
                        html,
                        "text/html",
                        "UTF-8",
                        null
                    )
                }
            }
        )
    }
}

private fun buildVideoPreviewHtml(videoUrl: String): String {
    val embedUrl = videoUrl.toYouTubeEmbedUrl()
    val sourceUrl = embedUrl ?: videoUrl
    Log.e("ExerciseVideo", "Original: $videoUrl, Embed: $embedUrl, Source: $sourceUrl")

    return if (embedUrl != null) {
        // Extract video ID from embed URL
        val videoId = embedUrl.substringAfterLast("/").substringBefore("?")
        """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    * { margin: 0; padding: 0; }
                    html, body {
                        width: 100%;
                        height: 100%;
                        background: transparent;
                    }
                    #player {
                        width: 100%;
                        height: 100%;
                    }
                </style>
            </head>
            <body>
                <div id="player"></div>
                <script>
                    var tag = document.createElement('script');
                    tag.src = "https://www.youtube.com/iframe_api";
                    var firstScriptTag = document.getElementsByTagName('script')[0];
                    firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
                    
                    var player;
                    function onYouTubeIframeAPIReady() {
                        console.log("YouTube API Ready, creating player for video: $videoId");
                        player = new YT.Player('player', {
                            height: '100%',
                            width: '100%',
                            videoId: '$videoId',
                            events: {
                                'onReady': onPlayerReady,
                                'onError': onPlayerError
                            },
                            playerVars: {
                                'fs': 1,
                                'rel': 0,
                                'modestbranding': 1,
                                'controls': 1
                            }
                        });
                    }
                    
                    function onPlayerReady(event) {
                        console.log("Player ready");
                        event.target.playVideo();
                    }
                    
                    function onPlayerError(event) {
                        console.error("Player error:", event.data);
                    }
                </script>
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
                    <source src="$sourceUrl">
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

    Log.e("ExerciseVideo", "Parsing URL: $normalized")

    val videoId = when {
        normalized.contains("youtu.be/") -> {
            val id = normalized.substringAfter("youtu.be/").substringBefore("?").substringBefore("&")
            Log.e("ExerciseVideo", "youtu.be format, ID: $id")
            id
        }
        normalized.contains("youtube.com/watch") -> {
            val id = Regex("[?&]v=([^&]+)").find(normalized)?.groupValues?.getOrNull(1)
            Log.e("ExerciseVideo", "youtube.com/watch format, ID: $id")
            id
        }
        normalized.contains("youtube.com/embed/") -> {
            val id = normalized.substringAfter("youtube.com/embed/").substringBefore("?").substringBefore("&")
            Log.e("ExerciseVideo", "youtube.com/embed format, ID: $id")
            id
        }
        else -> {
            Log.e("ExerciseVideo", "URL format not recognized")
            null
        }
    }?.takeIf { it.isNotBlank() } ?: return null

    val result = "https://www.youtube.com/embed/$videoId"
    Log.e("ExerciseVideo", "Final embed URL: $result")
    return result
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

