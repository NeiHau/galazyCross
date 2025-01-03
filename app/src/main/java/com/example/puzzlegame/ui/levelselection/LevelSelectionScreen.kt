package com.example.puzzlegame.ui.levelselection

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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.galaxycross.R
import com.example.puzzlegame.data.GameLevels
import com.example.puzzlegame.extension.findActivity
import com.example.puzzlegame.repository.BillingRepository
import com.example.puzzlegame.ui.levelselection.components.TutorialCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelSelectionScreen(
    viewModel: LevelSelectionViewModel = hiltViewModel(),
    onLevelSelect: (Int) -> Unit,
    clearedLevels: Set<Int>,
    isTutorialCompleted: Boolean,
    onTutorialSelect: () -> Unit,
    onSettingIconTapped: () -> Unit,
) {
    val context = LocalContext.current
    val availableLevelCount = GameLevels.getLevelCount()
    val scrollState by viewModel.scrollState.collectAsState(LevelSelectionViewModel.ScrollState())
    val isPremiumPurchased by viewModel.isPremiumPurchased.collectAsState(false)
    val purchaseResult by viewModel.purchaseResult.observeAsState()
    val scope = rememberCoroutineScope()

    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }

    // スナックバー文言
    val premiumUnlockText = stringResource(R.string.premium_unlock)
    val premiumFailureText = stringResource(R.string.premium_failure)
    val premiumCancelText = stringResource(R.string.premium_cancel)
    val premiumCannotBuyText = stringResource(R.string.premium_cannot_start_buy_process)

    LaunchedEffect(purchaseResult) {
        when (purchaseResult) {
            is BillingRepository.PurchaseResult.Success -> {
                snackbarHostState.showSnackbar(premiumUnlockText)
            }
            is BillingRepository.PurchaseResult.Error -> {
                snackbarHostState.showSnackbar(
                    "$premiumFailureText: ${(purchaseResult as BillingRepository.PurchaseResult.Error).message}"
                )
            }
            is BillingRepository.PurchaseResult.Canceled -> {
                snackbarHostState.showSnackbar(premiumCancelText)
            }
            null -> {}
        }
    }

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
                        text = stringResource(R.string.level_selection_appbar_title),
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                actions = {
                    IconButton(
                        onClick = onSettingIconTapped,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(R.string.level_selection_appbar_icon_contentDescription),
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
                                val activity = context.findActivity()
                                if (activity != null) {
                                    viewModel.startPremiumPurchase(activity)
                                } else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(premiumCannotBuyText)
                                    }
                                }
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
    val stateDescription = when {
        requiresPremium -> "プレミアム機能未解除"
        isCleared -> "クリア済み"
        isEnabled -> "プレイ可能"
        else -> "未開放"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .semantics {
                contentDescription = "レベル $levelNumber - $stateDescription"
                if (isEnabled || requiresPremium) {
                    role = Role.Button
                    onClick(label = "レベル ${levelNumber}を選択", action = null)
                }
            }
            .then(
                if (isEnabled || requiresPremium) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            ),
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
                        text = "プレミアムセット購入で遊べます",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            if (requiresPremium) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = if (isCleared) Color(0xFFFFC20E) else Color.Gray
                )
            }
        }
    }
}
