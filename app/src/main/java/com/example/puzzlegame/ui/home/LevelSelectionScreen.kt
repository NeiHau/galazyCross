package com.example.puzzlegame.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.puzzlegame.data.GameLevels

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelSelectionScreen(
    onLevelSelect: (Int) -> Unit,
    clearedLevels: Set<Int>,
    viewModel: LevelSelectionViewModel = hiltViewModel()
) {
    val availableLevelCount = GameLevels.getLevelCount()
    val scrollState by viewModel.scrollState.collectAsState()

    // LazyListStateをrememberで保持
    val listState = rememberLazyListState()

    // 初期スクロール位置の設定
    LaunchedEffect(scrollState) {
        // 保存された位置が0以外の場合のみスクロール位置を復元
        if (scrollState.index > 0 || scrollState.offset > 0) {
            listState.scrollToItem(
                index = scrollState.index,
                scrollOffset = scrollState.offset
            )
        }
    }

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
            state = listState,  // LazyListStateを設定
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(availableLevelCount) { index ->
                val isEnabled = if (index == 0) {
                    true
                } else {
                    clearedLevels.contains(index - 1)
                }
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
