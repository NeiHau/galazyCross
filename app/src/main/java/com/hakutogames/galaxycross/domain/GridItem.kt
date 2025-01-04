package com.hakutogames.galaxycross.domain

import androidx.compose.ui.geometry.Offset

data class GridItem(
    val id: String,
    val position: Offset,
    val length: Int,
    val isHorizontal: Boolean,
    val isTarget: Boolean = false,
    val imageIndex: Int = 0,
)
