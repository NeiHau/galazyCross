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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
        "レベル${currentLevel + 1}クリア！"
    }

    AlertDialog(
        modifier = Modifier.fillMaxWidth(),
        onDismissRequest = {},
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = dialogTitle,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
            )
        },
        text = {
            val dialogMessage = if (currentLevel == TUTORIAL_LEVEL_INDEX) {
                "次に進みましょう。"
            } else {
                "次はどうしますか？"
            }
            Text(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                text = dialogMessage,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.W500,
                fontSize = 16.sp,
            )
        },
        confirmButton = {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (hasNextLevel || currentLevel == TUTORIAL_LEVEL_INDEX) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onNextLevel,
                    ) {
                        Text("次のステージへ")
                    }
                }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onReplay,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("もう一度プレイ")
                }
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onShowLevelSelection,
                ) {
                    Text("レベル選択に戻る")
                }
            }
        },
        shape = MaterialTheme.shapes.medium
    )
}