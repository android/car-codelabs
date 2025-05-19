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

package com.example.android.cars.roadreels.ui.screen.player

import android.content.pm.ActivityInfo
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.example.android.cars.roadreels.LocalControllableInsets
import com.example.android.cars.roadreels.SupportedOrientation
import com.example.android.cars.roadreels.supportedOrientations

data class PlayerUiState(
    val isShowingControls: Boolean = false,
    val isLoading: Boolean = true,
    val isPlaying: Boolean = false,
    val durationMillis: Long = -1,
    val currentPositionMillis: Long = -1,
    val mediaMetadata: MediaMetadata = MediaMetadata.Builder().build()
) {
    fun withPlayerState(player: Player): PlayerUiState {
        return copy(
            isLoading = player.isLoading,
            isPlaying = player.isPlaying,
            durationMillis = player.duration,
            currentPositionMillis = player.currentPosition,
            mediaMetadata = player.mediaMetadata
        )
    }
}

@UnstableApi
@Composable
fun PlayerScreen(
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = viewModel()
) {
    val activity = checkNotNull(LocalActivity.current)
    val windowInsetsController =
        WindowCompat.getInsetsController(
            activity.window,
            activity.window.decorView
        )

    val player by viewModel.player.collectAsState()
    val playerUiState by viewModel.uiState.collectAsState()

    val controllableInsetsTypeMask by rememberUpdatedState(LocalControllableInsets.current)

    DisposableEffect(Unit) {
        // Only automatically set the orientation to landscape if the device supports landscape.
        // On devices that are portrait only, the activity may enter a compat mode and won't get to
        // use the full window available if so. The same applies if the app's window is portrait
        // in multi-window mode.
        if (activity.supportedOrientations()
                .contains(SupportedOrientation.Landscape) && !activity.isInMultiWindowMode
        ) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        }

        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars().and(controllableInsetsTypeMask))
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        onDispose {
            // Reset the requested orientation to the default
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

            windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
            windowInsetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        viewModel.play()
    }
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        viewModel.pause()
    }

    // When the system bars can be hidden, ignore them when applying padding to the player and
    // controls so they don't jump around as the system bars disappear. If they can't be hidden,
    // include them so everything is visible
    var windowInsetsForPadding = WindowInsets.displayCutout
    if (controllableInsetsTypeMask.and(WindowInsetsCompat.Type.statusBars()) == 0) {
        windowInsetsForPadding = windowInsetsForPadding.union(WindowInsets.statusBars)
    }
    if (controllableInsetsTypeMask.and(WindowInsetsCompat.Type.navigationBars()) == 0) {
        windowInsetsForPadding = windowInsetsForPadding.union(WindowInsets.navigationBars)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(windowInsetsForPadding)
    ) {
        player?.let {
            PlayerView(
                it,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { if (playerUiState.isShowingControls) viewModel.hideControls() else viewModel.showControls() }
            )

            PlayerControls(
                modifier = Modifier
                    .fillMaxSize(),
                uiState = playerUiState,
                onClose = onClose,
                onPlayPause = { if (playerUiState.isPlaying) viewModel.pause() else viewModel.play() },
                onSeek = viewModel::seekTo
            )
        }
    }
}