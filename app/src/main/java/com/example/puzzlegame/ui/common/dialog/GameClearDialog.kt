package com.example.puzzlegame.ui.common.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

const val TUTORIAL_LEVEL_INDEX = -1

@Composable
fun GameClearDialog(
    currentLevel: Int,
    hasNextLevel: Boolean,
    onReplay: () -> Unit,
    onNextLevel: () -> Unit,
    onShowLevelSelection: () -> Unit
) {
    val dialogTitle = if (currentLevel == TUTORIAL_LEVEL_INDEX) {
        "チュートリアル完了！"
    } else {
        "ゲームクリア！"
    }

    val dialogSubtitle = if (currentLevel == TUTORIAL_LEVEL_INDEX) {
        ""
    } else {
        "レベル${currentLevel + 1}完了"
    }

    AlertDialog(
        modifier = Modifier.fillMaxWidth(),
        onDismissRequest = {},
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = dialogTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = dialogSubtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        },
        text = {
            val dialogMessage = if (currentLevel == TUTORIAL_LEVEL_INDEX) {
                "次に進みましょう。"
            } else {
                "宇宙船がゴールに到達しました！\n次はどうしますか？"
            }

            Text(
                text = dialogMessage,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (hasNextLevel || currentLevel == TUTORIAL_LEVEL_INDEX) {
                    Button(
                        onClick = onNextLevel,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("次のステージへ")
                    }
                }
                Button(
                    onClick = onReplay,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("もう一度プレイ")
                }
                OutlinedButton(
                    onClick = onShowLevelSelection,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("レベル選択に戻る")
                }
            }
        },
        shape = MaterialTheme.shapes.medium
    )
}