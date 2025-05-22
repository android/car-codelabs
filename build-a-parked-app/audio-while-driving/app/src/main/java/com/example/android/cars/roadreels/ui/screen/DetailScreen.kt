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

package com.example.android.cars.roadreels.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.PlayArrow
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.android.cars.roadreels.R

@Composable
fun DetailScreen(
    thumbnailId: Int,
    windowSizeClass: WindowSizeClass,
    onPlayButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(dimensionResource(R.dimen.screen_edge_padding))
    ) {
        if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
            Column {
                Thumbnail(
                    thumbnailId = thumbnailId,
                    onPlayButtonClicked = onPlayButtonClicked
                )

                Description(stringResource(R.string.bbb_description))
            }
        } else {
            Row(
                modifier = Modifier
                    .sizeIn(maxWidth = dimensionResource(R.dimen.detail_max_width))
                    .align(Alignment.TopCenter)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(.5f)
                ) {
                    Thumbnail(
                        thumbnailId = thumbnailId,
                        onPlayButtonClicked = onPlayButtonClicked
                    )
                }

                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.detail_spacer_width)))

                Description(stringResource(R.string.bbb_description))
            }
        }
    }
}

@Composable
fun Thumbnail(
    thumbnailId: Int,
    onPlayButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Image(
            painterResource(thumbnailId),
            modifier = Modifier.clip(MaterialTheme.shapes.medium),
            contentDescription = "Big Buck Bunny still"
        )

        FilledIconButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onPlayButtonClicked
        ) {
            Icon(
                Icons.TwoTone.PlayArrow,
                contentDescription = "Play"
            )
        }
    }
}

@Composable
fun Description(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        modifier,
        style = MaterialTheme.typography.bodyLarge
    )
}