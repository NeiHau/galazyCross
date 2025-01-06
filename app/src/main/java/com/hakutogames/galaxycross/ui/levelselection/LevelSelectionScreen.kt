package com.hakutogames.galaxycross.ui.levelselection

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hakutogames.galaxycross.R
import com.hakutogames.galaxycross.data.GameLevels
import com.hakutogames.galaxycross.extension.findActivity
import com.hakutogames.galaxycross.ui.ext.observeWithLifecycle
import com.hakutogames.galaxycross.ui.levelselection.components.LevelSelectionItem
import com.hakutogames.galaxycross.ui.levelselection.components.TutorialCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LevelSelectionScreen(
    viewModel: LevelSelectionViewModel = hiltViewModel(),
    onLevelSelect: (Int) -> Unit,
    clearedLevels: Set<Int>,
    isTutorialCompleted: Boolean,
    onShowSnackbar: (String) -> Unit,
    onTutorialSelect: () -> Unit,
    onSettingIconTapped: () -> Unit,
    isReturningToLevelSelection: Boolean,
) {
    val availableLevelCount = GameLevels.getLevelCount()
    val isPremiumPurchased by viewModel.isPremiumPurchased.collectAsStateWithLifecycle()
    val scrollToLevelIndex by viewModel.scrollToLevelIndex.collectAsStateWithLifecycle()

    // Snackbarのメッセージを取得
    val premiumUnlockText = stringResource(R.string.premium_unlock)
    val premiumFailureText = stringResource(R.string.premium_failure)
    val premiumCannotBuyText = stringResource(R.string.premium_cannot_start_buy_process)

    viewModel.uiEvent.observeWithLifecycle { event ->
        when (event) {
            is LevelSelectionViewModel.UiEvent.PurchaseSuccess -> {
                onShowSnackbar(premiumUnlockText)
            }
            is LevelSelectionViewModel.UiEvent.PurchaseError -> {
                onShowSnackbar(premiumFailureText)
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!isPremiumPurchased && clearedLevels.contains(14) && isReturningToLevelSelection) {
            viewModel.setScrollToLevelIndex(15)
        }
    }

    LevelSelectionScreen(
        clearedLevels = clearedLevels,
        scrollToLevelIndex = scrollToLevelIndex,
        premiumCannotBuyText = premiumCannotBuyText,
        availableLevelCount = availableLevelCount,
        isPremiumPurchased = isPremiumPurchased,
        isTutorialCompleted = isTutorialCompleted,
        onShowSnackbar = onShowSnackbar,
        onTutorialSelect = onTutorialSelect,
        onLevelSelect = onLevelSelect,
        onSettingIconTapped = onSettingIconTapped,
        onStartPremiumPurchase = viewModel::startPremiumPurchase,
        clearScrollToLevelIndex = viewModel::clearScrollToLevelIndex,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LevelSelectionScreen(
    clearedLevels: Set<Int>,
    scrollToLevelIndex: Int?,
    premiumCannotBuyText: String,
    availableLevelCount: Int,
    isTutorialCompleted: Boolean,
    onShowSnackbar: (String) -> Unit,
    onTutorialSelect: () -> Unit,
    onSettingIconTapped: () -> Unit,
    onLevelSelect: (Int) -> Unit,
    isPremiumPurchased: Boolean,
    onStartPremiumPurchase: (Activity) -> Unit,
    clearScrollToLevelIndex: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(scrollToLevelIndex) {
        scrollToLevelIndex?.let { index ->
            if (index in 0 until availableLevelCount) {
                try {
                    delay(500)
                    coroutineScope.launch {
                        listState.animateScrollToItem(
                            index = index,
                            scrollOffset = 0,
                        )
                        delay(500)
                        clearScrollToLevelIndex()
                    }
                } catch (e: Exception) {
                    throw e
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.level_selection_appbar_title),
                        fontWeight = FontWeight.W500,
                    )
                },
                actions = {
                    IconButton(
                        onClick = onSettingIconTapped,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(
                                R.string.level_selection_appbar_icon_contentDescription,
                            ),
                        )
                    }
                },
            )
        },
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
                    modifier = Modifier.padding(bottom = 16.dp),
                )
            }

            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 12.dp),
            ) {
                items(
                    count = availableLevelCount,
                    key = { index -> index },
                ) { index ->
                    val isEnabled = when {
                        index < 15 -> {
                            if (index == 0) {
                                isTutorialCompleted
                            } else {
                                clearedLevels.contains(index - 1)
                            }
                        }
                        isPremiumPurchased -> clearedLevels.contains(index - 1)
                        else -> false
                    }
                    val isCleared = clearedLevels.contains(index)
                    val requiresPremium = index >= 15 && !isPremiumPurchased

                    LevelSelectionItem(
                        levelNumber = index + 1,
                        isPremiumUser = isPremiumPurchased,
                        isEnabled = isEnabled,
                        isCleared = isCleared,
                        requiresPremium = requiresPremium,
                        onClick = {
                            if (requiresPremium) {
                                val activity = context.findActivity()
                                if (activity != null) {
                                    onStartPremiumPurchase(activity)
                                } else {
                                    scope.launch {
                                        onShowSnackbar(premiumCannotBuyText)
                                    }
                                }
                            } else {
                                onLevelSelect(index)
                            }
                        },
                    )
                }
            }
        }
    }
}

// // プレミアム購入後のUIを表示
// @Preview(showBackground = true)
// @Composable
// fun LevelSelectionScreenPremiumPreview() {
//    val clearedLevels = setOf(0, 1, 2, 3, 4, 5)
//    val premiumCannotBuyText = "購入プロセスを開始できません"
//
//    LevelSelectionScreen(
//        clearedLevels = clearedLevels,
//        premiumCannotBuyText = premiumCannotBuyText,
//        availableLevelCount = 30,
//        isPremiumPurchased = true,
//        isTutorialCompleted = false,
//        onShowSnackbar = {},
//        onTutorialSelect = {},
//        onLevelSelect = {},
//        onSettingIconTapped = {},
//        onStartPremiumPurchase = {}
//    )
// }
//
//
// // プレミアム購入前のUIを表示
// @Preview(showBackground = true)
// @Composable
// fun LevelSelectionScreenNonPremiumPreview() {
//    val clearedLevels = setOf(0, 1, 2, 3, 4, 5)
//    val premiumCannotBuyText = "購入プロセスを開始できません"
//
//    LevelSelectionScreen(
//        clearedLevels = clearedLevels,
//        premiumCannotBuyText = premiumCannotBuyText,
//        availableLevelCount = 30,
//        isPremiumPurchased = false,
//        isTutorialCompleted = false,
//        onShowSnackbar = {},
//        onTutorialSelect = {},
//        onLevelSelect = {},
//        onSettingIconTapped = {},
//        onStartPremiumPurchase = {}
//    )
// }
