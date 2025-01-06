package com.hakutogames.galaxycross.ui.common.dialog

import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
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
    isPremiumUser: Boolean,
    onReplay: () -> Unit,
    onNextLevel: () -> Unit,
    onShowLevelSelection: (Int?) -> Unit,
) {
    val dialogTitle = if (currentLevel == TUTORIAL_LEVEL_INDEX) {
        "チュートリアル完了！"
    } else {
        "レベル${currentLevel + 1}クリア！"
    }

    LaunchedEffect(Unit) {
        Log.d(
            "GameClearDialog",
            """
        currentLevel = $currentLevel
        isPremiumUser = $isPremiumUser
        hasNextLevel = $hasNextLevel
            """.trimIndent(),
        )
    }

    AlertDialog(
        modifier = Modifier.fillMaxWidth(),
        onDismissRequest = {},
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = dialogTitle,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
            )
        },
        text = {
            val dialogMessage = if (currentLevel == TUTORIAL_LEVEL_INDEX) {
                "次に進みましょう。"
            } else if (!isPremiumUser && currentLevel == 14) {
                "レベル16以降に挑戦するには、追加コンテンツのご購入が必要です。\n\nレベル選択画面からご購入頂けます。"
            } else {
                "次はどうしますか？"
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                text = dialogMessage,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
            )
        },
        confirmButton = {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                when {
                    // (2) 無課金ユーザー: レベル15未満
                    hasNextLevel && !isPremiumUser && currentLevel < 14 -> {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onNextLevel,
                        ) {
                            Text("次のレベルへ")
                        }
                    }

                    // (3) 課金ユーザー: currentLevel < 16
                    hasNextLevel && isPremiumUser && currentLevel < 16 -> {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onNextLevel,
                        ) {
                            Text("次のレベルへ")
                        }
                    }

                    // (1) 課金ユーザー: currentLevel >= 16 (etc.)
                    hasNextLevel && isPremiumUser -> {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onNextLevel,
                        ) {
                            Text("次のレベルへ")
                        }
                    }
                }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onReplay,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                    ),
                ) {
                    Text("もう一度プレイ")
                }
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        val scrollIndex = if (!isPremiumUser && currentLevel == 14) {
                            15
                        } else {
                            null
                        }
                        onShowLevelSelection(scrollIndex)
                    },
                ) {
                    Text("レベル選択に戻る")
                }
            }
        },
        shape = MaterialTheme.shapes.medium,
    )
}
