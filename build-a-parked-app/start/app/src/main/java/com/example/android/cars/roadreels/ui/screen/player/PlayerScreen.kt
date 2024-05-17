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

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val VIDEO_URI =
    "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"

data class PlayerState(
    val isLoading: Boolean = true,
    val isPlaying: Boolean = false,
    val durationMillis: Long,
    val currentPositionMillis: Long,
    val mediaMetadata: MediaMetadata
)

fun Player.toPlayerState(): PlayerState {
    return PlayerState(
        isLoading,
        isPlaying,
        duration,
        currentPosition,
        mediaMetadata,
    )
}

@Composable
fun PlayerScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current as Activity
    val coroutineScope = rememberCoroutineScope()

    val player = remember(context) { ExoPlayer.Builder(context).build() }
    var isShowingControls by remember { mutableStateOf(true) }
    var playerState by remember(player) { mutableStateOf(player.toPlayerState()) }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                super.onEvents(player, events)

                if (events.contains(Player.EVENT_MEDIA_METADATA_CHANGED)
                    || events.contains(Player.EVENT_IS_LOADING_CHANGED)
                    || events.contains(Player.EVENT_IS_PLAYING_CHANGED)
                ) {
                    playerState = player.toPlayerState()
                }
            }
        }

        player.addListener(listener)

        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }

    // Continually update to capture the current position
    LaunchedEffect(player) {
        while (true) {
            playerState = player.toPlayerState()
            delay(1000)
        }
    }

    val mediaSource =
        MediaItem.fromUri(VIDEO_URI)
            .buildUpon().setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle("Big Buck Bunny")
                    .setArtworkUri("https://peach.blender.org/wp-content/uploads/title_anouncement.jpg".toUri())
                    .build()
            ).build()

    // When either the player or the mediaSource change, react to that change
    LaunchedEffect(player, mediaSource) {
        player.setMediaItem(mediaSource)
        player.prepare()
        player.playWhenReady = true
    }

    val windowInsetsController = remember(context) { WindowCompat.getInsetsController(context.window, context.window.decorView) }

    LaunchedEffect(Unit) {
        context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    DisposableEffect(isShowingControls, playerState.isPlaying) {
        val coroutine = coroutineScope.launch {
            if (isShowingControls) {
                delay(5000)
                isShowingControls = false
            }
        }

        onDispose {
            coroutine.cancel()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            player.release()
            player.clearMediaItems()

            // Reset the requested orientation to the default
            context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

            windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
            windowInsetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        PlayerView(
            player,
            modifier = Modifier
                .fillMaxSize()
                .clickable { isShowingControls = !isShowingControls }
        )

        PlayerControls(
            modifier = Modifier
                .fillMaxSize(),
            visible = isShowingControls,
            playerState = playerState,
            onPlayPause = { if (playerState.isPlaying) player.pause() else player.play() },
            onSeek = { player.seekTo(it) }
        )
    }
}