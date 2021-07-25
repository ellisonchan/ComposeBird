package com.ellison.flappybird.model

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class BirdState(var birdHeight: Dp = DefaultBirdHeightOffset, var isLifting: Boolean = false) {
    fun lift(): BirdState =
        copy(birdHeight = birdHeight - BirdLiftVelocity, isLifting = true)

    fun fall(): BirdState =
        copy(birdHeight = birdHeight + BirdFallVelocity, isLifting = false)

    fun over(groundOffset: Dp): BirdState =
        copy(birdHeight = groundOffset)

    fun quickFall(): BirdState =
        copy(birdHeight = birdHeight + BirdQuickFallVelocity)
}

val DefaultBirdHeightOffset = 0.dp

val HighBirdHeightOffset = (-272).dp

val LowBirdHeightOffset = 272.dp

val BirdSizeWidth = 72.dp // 48.dp // Control bird's size
val BirdSizeHeight = 50.dp // 48.dp // Control bird's size

// Need consider bird's height when calculating hit ground or not.
// val BirdHitGroundThreshold = 0.dp
val BirdHitGroundThreshold = BirdSizeHeight / 2 // BirdSizeHeight / 3

const val BirdFallToGroundTimes = 20
var BirdFallVelocity = 8.dp
var BirdQuickFallVelocity = BirdFallVelocity * 4

val BirdLiftVelocity = BirdFallVelocity * 8
val BirdQuickLiftVelocity = BirdLiftVelocity * 1.5f