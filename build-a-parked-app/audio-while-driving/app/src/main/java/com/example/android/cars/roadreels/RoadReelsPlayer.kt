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

import android.car.Car
import android.car.drivingstate.CarUxRestrictions
import android.car.drivingstate.CarUxRestrictionsManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.car.app.features.CarFeatures
import androidx.media3.common.ForwardingSimpleBasePlayer
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.google.common.util.concurrent.ListenableFuture

@UnstableApi
class RoadReelsPlayer(context: Context) :
    ForwardingSimpleBasePlayer(ExoPlayer.Builder(context).build()) {

    private var pausedByUxRestrictions = false
    private var shouldPreventPlay = false
    private lateinit var carUxRestrictionsManager: CarUxRestrictionsManager

    init {
        with(context) {
            // Only listen to UX restrictions if the device is running Android Automotive OS
            if (packageManager.hasSystemFeature(PackageManager.FEATURE_AUTOMOTIVE)) {
                val car = Car.createCar(context)
                carUxRestrictionsManager =
                    car.getCarManager(Car.CAR_UX_RESTRICTION_SERVICE) as CarUxRestrictionsManager

                val isBackgroundAudioWhileDrivingSupported = CarFeatures.isFeatureEnabled(
                    context,
                    CarFeatures.FEATURE_BACKGROUND_AUDIO_WHILE_DRIVING
                )

                // Get the initial UX restrictions and update the player state
                shouldPreventPlay = !isBackgroundAudioWhileDrivingSupported &&
                        carUxRestrictionsManager.currentCarUxRestrictions.isRequiresDistractionOptimization
                invalidateState()

                // Register a listener to update the player state as the UX restrictions change
                carUxRestrictionsManager.registerListener { carUxRestrictions: CarUxRestrictions ->
                    shouldPreventPlay = !isBackgroundAudioWhileDrivingSupported &&
                            carUxRestrictions.isRequiresDistractionOptimization

                    if (!shouldPreventPlay && pausedByUxRestrictions) {
                        handleSetPlayWhenReady(true)
                        invalidateState()
                    } else if (shouldPreventPlay && isPlaying) {
                        pausedByUxRestrictions = true
                        handleSetPlayWhenReady(false)
                        invalidateState()
                    }
                }
            }
        }

        addListener(object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                if (events.contains(EVENT_IS_PLAYING_CHANGED) && isPlaying) {
                    pausedByUxRestrictions = false
                }
            }
        })
    }

    override fun getState(): State {
        val state = super.getState()

        return state.buildUpon()
            .setAvailableCommands(
                state.availableCommands.buildUpon().removeIf(COMMAND_PLAY_PAUSE, shouldPreventPlay)
                    .build()
            ).build()
    }

    override fun handleRelease(): ListenableFuture<*> {
        if (::carUxRestrictionsManager.isInitialized) {
            carUxRestrictionsManager.unregisterListener()
        }
        return super.handleRelease()
    }
}