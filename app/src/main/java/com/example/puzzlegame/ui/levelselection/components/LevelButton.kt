package com.example.puzzlegame.ui.levelselection.components

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// レベルボタンのコンポーネント
@Composable
fun LevelButton(
    levelNumber: Int,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .aspectRatio(1f)
            .padding(4.dp)
    ) {
        Text(
            text = levelNumber.toString(),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}