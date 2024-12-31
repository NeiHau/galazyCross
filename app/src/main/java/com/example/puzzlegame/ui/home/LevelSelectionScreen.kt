package com.example.puzzlegame.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.puzzlegame.data.GameLevels

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelSelectionScreen(
    onLevelSelect: (Int) -> Unit,
    clearedLevels: Set<Int>,
) {
    // GameLevels.RAW_LEVELSの代わりに、利用可能なレベル数を取得
    val availableLevelCount = GameLevels.getLevelCount()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "レベル選択",
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(availableLevelCount) { index ->
                // 前のレベルをクリアしているかをチェック
                val isEnabled = if (index == 0) {
                    true // 最初のレベルは常に有効
                } else {
                    clearedLevels.contains(index - 1) // 前レベルがクリア済みなら有効
                }

                // クリア済みかどうかをチェック
                val isCleared = clearedLevels.contains(index)

                LevelSelectionItem(
                    levelNumber = index + 1,
                    isEnabled = isEnabled,
                    isCleared = isCleared,
                    onClick = { onLevelSelect(index) }
                )
            }
        }
    }
}

@Composable
private fun LevelSelectionItem(
    levelNumber: Int,
    isEnabled: Boolean,
    isCleared: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable(enabled = isEnabled, onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEnabled) MaterialTheme.colorScheme.surface else Color.Gray
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // レベル番号
            Text(
                text = "レベル $levelNumber",
                style = MaterialTheme.typography.titleMedium,
                color = if (isEnabled) MaterialTheme.colorScheme.onSurface else Color.LightGray,
                modifier = Modifier.weight(1f)
            )
            // 星アイコン（クリア済みなら黄色、未クリアなら灰色）
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = if (isCleared) "クリア済み" else "未クリア",
                tint = if (isCleared) Color.Yellow else Color.Gray
            )
        }
    }
}
