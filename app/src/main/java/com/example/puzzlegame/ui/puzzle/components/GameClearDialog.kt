package com.example.puzzlegame.ui.puzzle.components

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

@Composable
fun GameClearDialog(
    currentLevel: Int,
    hasNextLevel: Boolean,
    onReplay: () -> Unit,
    onNextLevel: () -> Unit,
    onShowLevelSelection: () -> Unit
) {
    // クリア時のダイアログをカスタマイズしたAlertDialogで表示します
    AlertDialog(
        // ダイアログの外側をタップしても閉じない設定
        onDismissRequest = { /* ダイアログを閉じないようにする */ },

        // ダイアログのタイトル部分
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // お祝いのメッセージを表示
                Text(
                    text = "ゲームクリア！",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )

                // 現在のレベルを表示
                Text(
                    text = "レベル${currentLevel + 1}完了",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        },

        // ダイアログの本文
        text = {
            Text(
                text = "赤い車を出口まで移動させました！\n次はどうしますか？",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },

        // ボタン群の配置
        confirmButton = {
            // ボタンを縦に配置するためのColumn
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 次のレベルへ進むボタン（最終レベル以外で表示）
                if (hasNextLevel) {
                    Button(
                        onClick = onNextLevel,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("次のレベルへ")
                    }
                }

                // 現在のレベルをもう一度プレイするボタン
                Button(
                    onClick = onReplay,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("もう一度プレイ")
                }

                // レベル選択に戻るボタン
                OutlinedButton(
                    onClick = onShowLevelSelection,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("レベル選択に戻る")
                }
            }
        },

        // ダイアログ全体のスタイリング
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),

        // ダイアログの形状をカスタマイズ
        shape = MaterialTheme.shapes.medium
    )
}