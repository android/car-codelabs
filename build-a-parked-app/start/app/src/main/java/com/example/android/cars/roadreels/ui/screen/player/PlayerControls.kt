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

import android.text.format.DateUtils
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.PauseCircle
import androidx.compose.material.icons.twotone.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.example.android.cars.roadreels.R

@Composable
fun PlayerControls(
    visible: Boolean,
    playerState: PlayerState,
    onPlayPause: () -> Unit,
    onSeek: (seekToMillis: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(modifier = modifier.background(Color.Black.copy(alpha = .5f))) {
            TopControls(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.screen_edge_padding))
                    .align(Alignment.TopCenter),
                title = playerState.mediaMetadata.title?.toString()
            )

            CenterControls(
                modifier = Modifier
                    .align(Alignment.Center),
                isPlaying = playerState.isPlaying,
                onPlayPause = onPlayPause
            )

            BottomControls(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.screen_edge_padding))
                    .align(Alignment.BottomCenter),
                durationMillis = playerState.durationMillis,
                currentPositionMillis = playerState.currentPositionMillis,
                onSeek = onSeek
            )
        }
    }
}


@Composable
fun TopControls(title: String?, modifier: Modifier = Modifier) {
    Box(modifier) {
        if (title != null) {
            Text(
                title,
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
        }
    }
}

@Composable
fun CenterControls(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    onPlayPause: () -> Unit = {}
) {
    Row(modifier = modifier) {
        IconButton(
            modifier = Modifier.size(64.dp),
            onClick = onPlayPause
        ) {
            Icon(
                if (isPlaying) Icons.TwoTone.PauseCircle else Icons.TwoTone.PlayCircle,
                modifier = Modifier.fillMaxSize(),
                contentDescription = "Toggle play/pause",
                tint = Color.White
            )
        }
    }
}

@Composable
fun BottomControls(
    durationMillis: Long,
    currentPositionMillis: Long,
    onSeek: (seekToMillis: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        if (durationMillis > 0) {
            var desiredSeekMillis by remember { mutableFloatStateOf(Float.NaN) }

            Slider(
                value = if (!desiredSeekMillis.isNaN()) desiredSeekMillis else currentPositionMillis.toFloat(),
                onValueChange = { desiredSeekMillis = it },
                onValueChangeFinished = {
                    onSeek(desiredSeekMillis.toLong())
                    desiredSeekMillis = Float.NaN
                },
                valueRange = 0f..durationMillis.toFloat(),
            )
            Text(
                text = "${
                    DateUtils.formatElapsedTime(currentPositionMillis / 1000)
                } / ${
                    DateUtils.formatElapsedTime(durationMillis / 1000)
                }",
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}