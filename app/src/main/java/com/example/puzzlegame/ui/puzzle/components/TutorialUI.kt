package com.example.puzzlegame.ui.puzzle.components

import androidx.compose.ui.geometry.Rect
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.puzzlegame.local.db.ClearedLevelsDataStore
import kotlinx.coroutines.delay

@Composable
fun TutorialOverlay(
    modifier: Modifier = Modifier,
    tutorialIndex: Int,
    step: ClearedLevelsDataStore.TutorialStep,
    onStepComplete: () -> Unit,
) {
    var showDialog by remember { mutableStateOf(true) }
    var showTutorial by remember { mutableStateOf(true) }

    Box(modifier = modifier.fillMaxSize()) {
        // インデックスベースのチュートリアル表示
        when (tutorialIndex) {
            1 -> {
                // 最初のチュートリアル: ゲーム説明
                TutorialDialog(
                    title = "ようこそ！",
                    message = "このゲームは宇宙船をゴールまで導くパズルゲームです。\n" +
                            "障害物を避けながら、宇宙船をゴールまで移動させましょう。",
                    onDismiss = {
                        // まずダイアログを閉じる
                        showTutorial = false
                        // その後、次のステップに進む
                        onStepComplete()
                    }
                )
            }
            2 -> {
                // 2番目のチュートリアル: 横移動の説明
                val horizontalGridTarget = remember {
                    // 横向きグリッドの位置を計算してRectを返す
                    Rect(
                        offset = Offset.Zero,
                        size = Size.Zero // 実際の実装ではグリッドの位置とサイズを計算
                    )
                }
                TutorialHighlight(
                    message = "横向きのグリッドをタップすると、\n" +
                            "左右に移動できます",
                    onDismiss = {
                        showTutorial = false  // チュートリアルを非表示に
                        onStepComplete()      // 次のステップへ
                    },
                    spotlightTarget = horizontalGridTarget
                )
            }
            3 -> {
                // 3番目のチュートリアル: 縦移動の説明
                val verticalGridTarget = remember {
                    // 縦向きグリッドの位置を計算してRectを返す
                    Rect(
                        offset = Offset.Zero,
                        size = Size.Zero // 実際の実装ではグリッドの位置とサイズを計算
                    )
                }
                TutorialHighlight(
                    message = "縦向きのグリッドをタップすると、\n" +
                            "上下に移動できます",
                    onDismiss = onStepComplete,
                    spotlightTarget = verticalGridTarget
                )
            }
            4 -> {
                // 4番目のチュートリアル: ゴール説明
                val goalTarget = remember {
                    // ゴールの位置を計算してRectを返す
                    Rect(
                        offset = Offset.Zero,
                        size = Size.Zero // 実際の実装ではゴールの位置とサイズを計算
                    )
                }
                TutorialHighlight(
                    message = "赤い宇宙船をゴールまで\n" +
                            "移動させるとクリアです！",
                    onDismiss = onStepComplete,
                    spotlightTarget = goalTarget
                )
            }
        }
    }

    LaunchedEffect(showTutorial) {
        if (!showTutorial) {
            // 少し遅延を入れて、UIの遷移をスムーズにする
            delay(100)
            onStepComplete()
        }
    }
}

@Composable
private fun TutorialDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("次へ")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
fun TutorialHighlight(
    message: String,
    onDismiss: () -> Unit,
    spotlightTarget: Rect? = null // スポットライトを当てる領域
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, // リップル効果を無効化
                onClick = onDismiss
            )
            .spotlightEffect(
                spotlightArea = spotlightTarget ?: Rect.Zero,
                alpha = 0.7f
            )
    ) {
        // メッセージカード
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp)
                .clickable(enabled = false) {}, // カード自体のクリックを無効化
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("次へ")
                }
            }
        }
    }
}

// ホール効果のための拡張関数
fun Modifier.spotlightEffect(
    spotlightArea: Rect,
    alpha: Float = 0.7f
) = drawBehind {
    // 全体を暗くする背景を描画
    drawRect(
        color = Color.Black.copy(alpha = alpha),
        size = size
    )

    // 指定された領域を透明にしてホール効果を作成
    drawRect(
        color = Color.Transparent,
        topLeft = spotlightArea.topLeft,
        size = spotlightArea.size,
        blendMode = BlendMode.Clear
    )
}