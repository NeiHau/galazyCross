package com.hakutogames.galaxycross.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.rememberNavController
import com.hakutogames.galaxycross.ui.theme.GalaxyCrossTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GalaxyCrossTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val coroutineScope = rememberCoroutineScope()
                val navController = rememberNavController()

                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                ) {
                    AppNavigation(
                        navController = navController,
                        onShowSnackbar = { message ->
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = message,
                                    duration = SnackbarDuration.Short,
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}