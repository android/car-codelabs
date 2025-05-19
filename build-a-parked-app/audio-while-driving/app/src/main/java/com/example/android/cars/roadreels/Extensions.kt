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

import android.content.Context
import android.content.pm.PackageManager
import android.view.Window
import android.view.WindowManager

// Directly updating a window's attributes doesn't actually change them
// i.e. window.attributes.layoutInDisplayModeCutout = ...
// doesn't change that parameter.
// window.setAttributes must be called for changes to take effect
fun Window.updateAttributes(block: WindowManager.LayoutParams.() -> Unit) {
    val layoutParams = WindowManager.LayoutParams()
    layoutParams.copyFrom(this.attributes)
    layoutParams.apply(block)
    this.attributes = layoutParams
}

enum class SupportedOrientation {
    Landscape,
    Portrait,
}

fun Context.supportedOrientations(): List<SupportedOrientation> {
    return when (Pair(
        packageManager.hasSystemFeature(PackageManager.FEATURE_SCREEN_LANDSCAPE),
        packageManager.hasSystemFeature(PackageManager.FEATURE_SCREEN_PORTRAIT)
    )) {
        Pair(true, false) -> listOf(SupportedOrientation.Landscape)
        Pair(false, true) -> listOf(SupportedOrientation.Portrait)
        // For backwards compat, if neither feature is declared, both can be assumed to be supported
        else -> listOf(SupportedOrientation.Landscape, SupportedOrientation.Portrait)
    }
}