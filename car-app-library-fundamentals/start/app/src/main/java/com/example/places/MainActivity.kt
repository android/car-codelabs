/*
 * Copyright 2023 Google LLC
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

package com.example.places

/*
try05 : https://developer.android.com/codelabs/car-app-library-fundamentals 와 https://thinking-face.tistory.com/383
Could not find androidx.compose.ui:ui-test-junit4:.
Required by:
project :app
처음 구동 한번은 됐음.
에서 제공하는 car-app-library-fundamentals/start 폴더의 Project를 Rebuild하는데 Grale Sync issues에서 계속 위 에러가 발생하고 있는데, 해결 방법을 알려줘
*/

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.places.data.PlacesRepository
import com.example.places.data.model.Place
import com.example.places.data.model.toIntent
import com.example.places.ui.theme.PlacesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PlacesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        Text(
                            text = "Places",
                            style = MaterialTheme.typography.displayLarge,
                            modifier = Modifier.padding(8.dp)
                        )
                        PlaceList(places = PlacesRepository().getPlaces())
                    }
                }
            }
        }
    }
}

@Composable
fun PlaceList(places: List<Place>) {
    val context = LocalContext.current

    LazyColumn {
        items(places.size) {
            val place = places[it]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .border(
                        2.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        context.startActivity(place.toIntent(Intent.ACTION_VIEW))
                    }
                    .padding(8.dp)
            ) {
                Icon(
                    Icons.Default.Place,
                    "Place icon",
                    modifier = Modifier.align(CenterVertically),
                    tint = MaterialTheme.colorScheme.surfaceTint
                )
                Column {
                    Text(
                        text = place.name,
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = place.description,
                        style = MaterialTheme.typography.bodyMedium,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }

            }
        }
    }
}