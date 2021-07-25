package com.ellison.flappybird.model

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class RoadState (var offset: Dp = RoadWidthOffset) {
    fun move(): RoadState = copy(offset = offset - RoadMoveVelocity)

    fun reset(): RoadState = copy(offset = TempRoadWidthOffset)
}

val RoadWidthOffset = 0.dp
val TempRoadWidthOffset = 300.dp
val RoadMoveVelocity = 10.dp

val RoadStateList = listOf(
    RoadState(),
    RoadState(offset = TempRoadWidthOffset)
)