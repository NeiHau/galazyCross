package com.hakutogames.galaxycross.ui.levelselection.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LevelSelectionItem(
    modifier: Modifier,
    levelNumber: Int,
    isEnabled: Boolean,
    isCleared: Boolean,
    requiresPremium: Boolean,
    isPremiumUser: Boolean,
    onClick: () -> Unit,
) {
    val isDarkTheme = isSystemInDarkTheme()
    val stateDescription = when {
        requiresPremium -> "プレミアム機能未解除"
        isCleared -> "クリア済み"
        isEnabled -> "プレイ可能"
        else -> "未開放"
    }
    val cardHeight = if (requiresPremium && levelNumber >= 16) 100.dp else 72.dp

    val containerColorValue = when {
        requiresPremium && !isPremiumUser && levelNumber >= 16 -> MaterialTheme.colorScheme.secondaryContainer
        isEnabled -> if (isDarkTheme) Color(0xFFa9a9a9) else MaterialTheme.colorScheme.surface
        else -> Color.Gray
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(cardHeight)
            .semantics {
                contentDescription = "レベル $levelNumber - $stateDescription"
                if (isEnabled || requiresPremium) {
                    role = Role.Button
                    onClick(label = "レベル ${levelNumber}を選択", action = null)
                }
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColorValue,
        ),
    ) {
        if (requiresPremium && levelNumber >= 16) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "レベル $levelNumber",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                        )
                        Text(
                            text = "(追加コンテンツご購入で遊べます)",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                    FilledTonalButton(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .width(160.dp)
                            .height(44.dp),
                        onClick = onClick,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        ),
                    ) {
                        Text(
                            text = "タップして購入する",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterEnd),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (isEnabled) {
                            Modifier.clickable(onClick = onClick)
                        } else {
                            Modifier
                        },
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = "レベル $levelNumber",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (isEnabled || requiresPremium) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            Color.LightGray
                        },
                    )
                }
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = if (isCleared) Color(0xFFFFC20E) else Color.Gray,
                )
            }
        }
    }
}
