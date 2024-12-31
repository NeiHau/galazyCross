package com.example.puzzlegame.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.runtime.snapshotFlow
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
    isTutorialCompleted: Boolean,
    onTutorialSelect: () -> Unit,
    isPremiumPurchased: Boolean, // 課金状態を追加
    onPremiumPurchase: () -> Unit, // 課金処理用のコールバック
    viewModel: LevelSelectionViewModel = hiltViewModel()
) {
    val availableLevelCount = GameLevels.getLevelCount()
    val scrollState by viewModel.scrollState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(scrollState) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
        ) {
            if (!isTutorialCompleted) {
                TutorialCard(
                    onClick = onTutorialSelect,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(availableLevelCount) { index ->
                    val isEnabled = when {
                        index < 15 -> clearedLevels.contains(index - 1) || index == 0
                        isPremiumPurchased -> clearedLevels.contains(index - 1)
                        else -> false
                    }
                    val isCleared = clearedLevels.contains(index)
                    val requiresPremium = index >= 15 && !isPremiumPurchased

                    LevelSelectionItem(
                        levelNumber = index + 1,
                        isEnabled = isEnabled,
                        isCleared = isCleared,
                        requiresPremium = requiresPremium,
                        onClick = {
                            if (requiresPremium) {
                                onPremiumPurchase()
                            } else {
                                onLevelSelect(index)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LevelSelectionItem(
    levelNumber: Int,
    isEnabled: Boolean,
    isCleared: Boolean,
    requiresPremium: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                requiresPremium -> MaterialTheme.colorScheme.secondaryContainer
                isEnabled -> MaterialTheme.colorScheme.surface
                else -> Color.Gray
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "レベル $levelNumber",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isEnabled || requiresPremium)
                        MaterialTheme.colorScheme.onSurface
                    else Color.LightGray
                )
                if (requiresPremium) {
                    Text(
                        text = "プレミアム解除で遊べます",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            if (requiresPremium) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "プレミアムコンテンツ",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = if (isCleared) "クリア済み" else "未クリア",
                    tint = if (isCleared) Color.Yellow else Color.Gray
                )
            }
        }
    }
}

// チュートリアル用のカードコンポーネント
@Composable
private fun TutorialCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // チュートリアルアイコン
            Icon(
                imageVector = Icons.Default.Done, // または適切なアイコンを選択
                contentDescription = "チュートリアル",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )

            // チュートリアルのテキスト
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "チュートリアル（まずはここから！）",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "ゲームの基本的な遊び方を学びましょう",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }

            // 矢印アイコン
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = "開始する",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
