package com.example.puzzlegame.ui.common.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.puzzlegame.ui.puzzle.components.TutorialAnimationView

@Composable
fun TutorialDialog(
    onDismiss: () -> Unit,
    onStartGame: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        val screenHeight = configuration.screenHeightDp.dp

        Surface(
            modifier = Modifier
                .width(screenWidth * 0.9f)
                .height(screenHeight * 1f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TutorialAnimationView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(screenHeight * 0.48f)
                            .width(screenWidth * 0.8f)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("1. アイテムを選択\n")
                            }
                            append("移動させたいアイテムをタップします。\n\n")

                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("2. 矢印キーで操作\n")
                            }
                            append("矢印ボタンを使って、アイテムを前後に動かします。\n\n")

                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("3. ゴールを目指そう\n")
                            }
                            append("星を避けながら、宇宙船をゴールまで導いてください。クリアするとゲーム終了です！\n\n")

                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("4. リセット\n")
                            }
                            append("「リセット」をタップして、最初からもう一度挑戦できます。")
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        textAlign = TextAlign.Center,
                    )
                }
                Button(
                    onClick = onStartGame,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(
                        text = "ゲームを開始する",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp)
                    )
                }
            }
        }
    }
}
