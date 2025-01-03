package com.example.puzzlegame.ui.setting

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hakutogames.galaxycross.BuildConfig



sealed class SettingItem {
//    data object SystemMode : SettingItem()
    data object Terms : SettingItem()
}

data class SettingsSection(
    val items: List<Pair<SettingItem, String>>,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onAppBarBackButtonTapped: () -> Unit,
    onTermsTapped: () -> Unit,
) {
    val isDarkTheme = isSystemInDarkTheme()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "設定",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onAppBarBackButtonTapped) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "戻る",
                            tint = if (isDarkTheme) Color.White else Color.Black
                        )
                    }
                }
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(top = 24.dp)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            SettingContentsList(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .weight(1f, fill = false),
                onTermsTapped = onTermsTapped,
                isDarkTheme = isDarkTheme
            )
            VersionInfoText(isDarkTheme = isDarkTheme)
        }
    }
}


@Composable
private fun VersionInfoText(isDarkTheme: Boolean) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = if(isDarkTheme) Color.Red else Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 24.dp),
        text = "Version ${BuildConfig.VERSION_NAME}",
        color = if (isDarkTheme) Color.White else Color.Black,
        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold,
    )
}



@Composable
fun SettingContentsList(
    modifier: Modifier = Modifier,
    onTermsTapped: () -> Unit,
    isDarkTheme: Boolean
) {
    val settingsSections = listOf(
        SettingsSection(
            items = listOf(
                // SettingItem.SystemMode to "システムモード設定",
                SettingItem.Terms to "利用規約",
            ),
        ),
    )

    SettingContentsListBody(
        modifier = modifier,
        sections = settingsSections,
        onItemClicked = { item ->
            when (item) {
                // SettingItem.SystemMode -> {}
                SettingItem.Terms -> onTermsTapped()
            }
        },
        isDarkTheme = isDarkTheme
    )
}

@Composable
fun SettingContentsListBody(
    modifier: Modifier = Modifier,
    sections: List<SettingsSection>,
    onItemClicked: (SettingItem) -> Unit,
    isDarkTheme: Boolean
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        sections.forEachIndexed { index, section ->
            Spacer(modifier = Modifier.height(8.dp))
            SettingsSectionContainer(
                items = section.items,
                onItemClicked = onItemClicked,
                isDarkTheme = isDarkTheme
            )
            if (index != sections.size - 1) {
                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}

@Composable
private fun SettingsSectionContainer(
    modifier: Modifier = Modifier,
    items: List<Pair<SettingItem, String>>,
    onItemClicked: (SettingItem) -> Unit,
    isDarkTheme: Boolean,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column {
            items.forEachIndexed { index, (item, text) ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClicked(item) }
                        .padding(vertical = 16.dp, horizontal = 16.dp)
                ) {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = if (isDarkTheme) Color.Black else MaterialTheme.colorScheme.onSurface
                    )
                }
                if (index != items.size - 1) {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = if (isDarkTheme) Color.Black else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                }
            }
        }
    }
}