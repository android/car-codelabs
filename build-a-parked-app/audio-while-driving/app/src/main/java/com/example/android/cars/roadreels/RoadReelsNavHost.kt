/*
 * Copyright 2024 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.cars.roadreels

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.android.cars.roadreels.ui.screen.DetailScreen
import com.example.android.cars.roadreels.ui.screen.MainScreen
import com.example.android.cars.roadreels.ui.screen.Screen
import com.example.android.cars.roadreels.ui.screen.player.PlayerScreen

@Composable
fun RoadReelsNavHost(
    windowSizeClass: WindowSizeClass,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Main.name,
        enterTransition = { slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Up) },
        exitTransition = { slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Down) }
    ) {
        composable(route = Screen.Main.name) {
            MainScreen(
                onItemSelected = { id -> navController.navigate("${Screen.Detail.name}/$id") }
            )
        }
        composable(
            route = "${Screen.Detail.name}/{id}",
            arguments = listOf(navArgument(name = "id") { type = NavType.IntType })
        ) {
            DetailScreen(
                thumbnailId = it.arguments?.getInt("id")!!,
                windowSizeClass = windowSizeClass,
                onPlayButtonClicked = { navController.navigate(Screen.Player.name) }
            )
        }
        composable(
            route = Screen.Player.name,
            enterTransition = { EnterTransition.None  },
            exitTransition = { ExitTransition.None }
        ) {
            PlayerScreen(onClose = { navController.popBackStack() })
        }
    }
}