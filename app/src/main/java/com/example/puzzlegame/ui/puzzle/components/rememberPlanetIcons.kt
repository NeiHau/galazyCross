package com.example.puzzlegame.ui.puzzle.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.hakutogames.galaxycross.R

@Composable
fun rememberPlanetIcons(): List<Painter> {
    val planet1 = painterResource(id = R.drawable.ic_planet_1)
    val planet2 = painterResource(id = R.drawable.ic_planet_2)
    val planet3 = painterResource(id = R.drawable.ic_planet_3)
    val planet4 = painterResource(id = R.drawable.ic_planet_4)
    val planet5 = painterResource(id = R.drawable.ic_planet_5)
    val planet6 = painterResource(id = R.drawable.ic_planet_6)
    val planet7 = painterResource(id = R.drawable.ic_planet_7)
    val planet8 = painterResource(id = R.drawable.ic_planet_8)
    val planet9 = painterResource(id = R.drawable.ic_planet_9)
    val planet10 = painterResource(id = R.drawable.ic_planet_10)
    val planet11 = painterResource(id = R.drawable.ic_planet_11)
    val planet12 = painterResource(id = R.drawable.ic_planet_12)
    val planet13 = painterResource(id = R.drawable.ic_planet_13)
    val planet14 = painterResource(id = R.drawable.ic_planet_14)

    return listOf(
        planet1, planet2, planet3, planet4, planet5, planet6, planet7,
        planet8, planet9, planet10, planet11, planet12, planet13, planet14
    )
}