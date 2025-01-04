package com.hakutogames.galaxycross.ui.common.dialog

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.AssetDataSource
import com.google.android.exoplayer2.upstream.DataSource

class AssetDataSourceFactory(private val context: Context) : DataSource.Factory {
    override fun createDataSource(): DataSource {
        return AssetDataSource(context)
    }
}

@Composable
fun AnswerDialog(
    onDismiss: () -> Unit,
    levelIndex: Int,
) {
    val context = LocalContext.current
    val assetName = "level_${levelIndex + 1}_answer.mp4"
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            val dataSourceFactory = AssetDataSourceFactory(context)
            val mediaItem = MediaItem.Builder()
                .setUri(Uri.parse("asset:///$assetName"))
                .build()
            val mediaSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaItem)

            setMediaSource(mediaSource)
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(player) {
        onDispose {
            player.release()
        }
    }

    Dialog(onDismissRequest = {}) {
        Surface(
            modifier = Modifier
                .width(screenWidth * 1f)
                .height(screenHeight * 0.8f),
            shape = MaterialTheme.shapes.medium,
            color = Color.White,
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 16.dp),
                    text = "レベル ${levelIndex + 1} の解答動画",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black,
                )
                Text(
                    modifier = Modifier.padding(bottom = 16.dp),
                    text = "画面をタップして動画を操作できます。",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black,
                    fontSize = 12.sp,
                )
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(color = Color.Black),
                    factory = {
                        PlayerView(context).apply {
                            this.player = player
                            useController = true
                            setShowRewindButton(false)
                            setShowFastForwardButton(false)
                        }
                    },
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    modifier = Modifier.padding(bottom = 8.dp),
                    onClick = onDismiss,
                ) {
                    Text("閉じる")
                }
            }
        }
    }
}
