package com.example.puzzlegame.ui.puzzle.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
fun TutorialOverlay(
    tutorialIndex: Int,
    onStepComplete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(2f) // オーバーレイを最前面に表示
            .pointerInput(tutorialIndex) {
                if (tutorialIndex == 1) {
                    detectTapGestures { offset ->
                        // チュートリアルステップ1でのタップ判定
                        val circleRadius = 50.dp.toPx() // ハイライトサークルの半径
                        val circleX = 140.dp.toPx() // xOffset = 140.dp
                        val circleY = 360.dp.toPx() // yOffset = 360.dp
                        val distance = sqrt((offset.x - circleX).pow(2) + (offset.y - circleY).pow(2))
                        if (distance <= circleRadius) {
                            onStepComplete()
                        }
                        // ハイライトサークル外をタップした場合は無視
                    }
                } else {
                    detectTapGestures {
                        // ステップ2以降では任意のタップで次のステップへ
                        onStepComplete()
                    }
                }
            }
    ) {
        // モーダルバリア（背景を暗くする）
        if (tutorialIndex in 1..4) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // 全体を暗くする
                drawRect(
                    color = Color.Gray.copy(alpha = 0.3f),
                    size = size
                )
                // ステップ1の場合、ハイライトサークルを透明にする
                if (tutorialIndex == 1) {
                    drawCircle(
                        color = Color.Transparent,
                        radius = 50.dp.toPx(), // ハイライトサークルの半径
                        center = Offset(140.dp.toPx(), 360.dp.toPx()), // xOffset, yOffset
                        blendMode = BlendMode.Clear
                    )
                }
                // ステップ2以降では他のハイライト方法を追加可能
            }
        }

        // チュートリアルステップの表示
        when (tutorialIndex) {
            1 -> TutorialStep(
                alignment = Alignment.TopStart,
                xOffset = 140.dp,
                yOffset = 360.dp,
                message = "動かしたいアイテムをタップしてみましょう。",
                circleColor = Color.Red,
                cardAlignment = Alignment.BottomStart,
                cardXOffset = 0.dp,
                cardYOffset = -(220).dp,
                onTap = onStepComplete // ステップ1のタップは透明な穴内で処理される
            )
            2 -> TutorialStep(
                alignment = Alignment.TopEnd,
                xOffset = (-57).dp,
                yOffset = 64.dp,
                message = "ここでアイテムを動かすことができます。",
                circleColor = Color.Blue,
                cardAlignment = Alignment.BottomStart,
                cardXOffset = 0.dp,
                cardYOffset = 16.dp,
                onTap = onStepComplete
            )
            3 -> TutorialStep(
                alignment = Alignment.BottomStart,
                xOffset = 55.dp,
                yOffset = 30.dp,
                message = "これは数字の３です！\nここをタップすると次の数字の説明に以降します。",
                circleColor = Color.Green,
                cardAlignment = Alignment.BottomStart,
                cardXOffset = 0.dp,
                cardYOffset = 16.dp,
                onTap = onStepComplete
            )
            4 -> TutorialStep(
                alignment = Alignment.BottomEnd,
                xOffset = (-81).dp,
                yOffset = 30.dp,
                message = "これは数字の４です！\nここをタップするとチュートリアルを終了します。",
                circleColor = Color.Yellow,
                cardAlignment = Alignment.BottomStart,
                cardXOffset = 0.dp,
                cardYOffset = 16.dp,
                onTap = onStepComplete
            )
            else -> {}
        }
    }
}


@Composable
fun TutorialStep(
    alignment: Alignment,
    xOffset: Dp,
    yOffset: Dp,
    message: String,
    circleColor: Color,
    cardAlignment: Alignment,
    cardXOffset: Dp,
    cardYOffset: Dp,
    onTap: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                // 背景全体のクリックを検出
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onTap
            )
    ) {
        // ハイライトサークル
        Box(
            modifier = Modifier
                .align(alignment)
                .offset(x = xOffset, y = yOffset)
                .size(width = 160.dp, height = 100.dp)
                .border(
                    width = 2.dp,
                    color = circleColor,
                    shape = RoundedCornerShape(size = 4.dp),
                )
        )

        // 説明テキスト
        Card(
            modifier = Modifier
                .align(cardAlignment)
                .offset(x = cardXOffset, y = cardYOffset)
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Text(
                text = message,
                modifier = Modifier.padding(16.dp),
                color = circleColor,
                fontSize = 12.sp
            )
        }
    }
}
