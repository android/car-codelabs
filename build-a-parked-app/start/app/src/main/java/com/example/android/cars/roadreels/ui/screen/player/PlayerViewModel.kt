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

import android.app.Application
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

val MEDIA_SOURCE =
    MediaItem.fromUri("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
        .buildUpon().setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle("Big Buck Bunny")
                .setArtworkUri("https://peach.blender.org/wp-content/uploads/title_anouncement.jpg".toUri())
                .build()
        ).build()

@UnstableApi
class PlayerViewModel(application: Application) : AndroidViewModel(application), Player.Listener {
    private val _player = MutableStateFlow<Player?>(null)
    val player: MutableStateFlow<Player?> get() = _player

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: MutableStateFlow<PlayerUiState> get() = _uiState

    private var hideControlsJob: Job? = null
    private var playerUiStateUpdateJob: Job? = null

    init {
        viewModelScope.launch {
            _player.onEach { player ->
                playerUiStateUpdateJob?.cancel()

                if (player != null) {
                    initializePlayer(player)

                    playerUiStateUpdateJob = viewModelScope.launch {
                        while (true) {
                            delay(500)
                            _uiState.getAndUpdate {
                                it.withPlayerState(player)
                            }
                        }
                    }
                }
            }.collect()
        }

        _player.update { ExoPlayer.Builder(application).build() }
    }

    private fun initializePlayer(player: Player) {
        player.setMediaItem(MEDIA_SOURCE)
        player.prepare()
        player.playWhenReady = true

        player.addListener(object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                super.onEvents(player, events)

                if (events.contains(Player.EVENT_MEDIA_METADATA_CHANGED)
                    || events.contains(Player.EVENT_IS_LOADING_CHANGED)
                    || events.contains(Player.EVENT_IS_PLAYING_CHANGED)
                ) {
                    _uiState.getAndUpdate {
                        it.withPlayerState(player)
                    }
                }
            }
        })
    }

    fun play() {
        _player.value?.play()
        hideControls()
    }

    fun pause() {
        _player.value?.pause()
    }

    fun seekTo(millis: Long) {
        _player.value?.seekTo(millis)
    }

    fun showControls() {
        _uiState.getAndUpdate { it.copy(isShowingControls = true) }
        hideControlsJob = viewModelScope.launch {
            delay(5000)
            _uiState.getAndUpdate { it.copy(isShowingControls = false) }
        }
    }

    fun hideControls() {
        hideControlsJob?.cancel()
        _uiState.getAndUpdate { it.copy(isShowingControls = false) }
    }

    override fun onCleared() {
        super.onCleared()
        _player.value?.release()
    }
}