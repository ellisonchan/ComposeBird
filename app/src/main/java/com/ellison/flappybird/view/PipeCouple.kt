package com.ellison.flappybird.view

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ellison.flappybird.model.*
import com.ellison.flappybird.util.LogUtil
import com.ellison.flappybird.viewmodel.GameViewModel

@Composable
fun PipeCouple(
    modifier: Modifier = Modifier,
    state: ViewState = ViewState(),
    pipeIndex: Int = 0
) {
    LogUtil.printLog(message = "PipeCouple()")
    val viewModel: GameViewModel = viewModel()

    val pipeState = state.pipeStateList[pipeIndex]

    Box(
        modifier
    ) {
        // New a list of pipes to keep moving ,when offset to device, invisible.
        LogUtil.printLog(
            message = "PipePair Box upHeight :${pipeState.upHeight}" +
                " downHeight:${pipeState.downHeight}" +
                " offset:${pipeState.offset}"
        )

        GetUpPipe(height = pipeState.upHeight,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = pipeState.offset)
        )

        GetDownPipe(height = pipeState.downHeight,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = pipeState.offset)
        )

        if (state.playZoneSize.first > 0) {
            val playZoneWidthInDP = with(LocalDensity.current) {
                state.playZoneSize.first.toDp()
            }

            LogUtil.printLog(message = "Zone width:$playZoneWidthInDP pipe offset:${pipeState.offset}")
            if (pipeState.offset < - playZoneWidthInDP - PipeResetThreshold) {
                // Send pipe reset action.
                LogUtil.printLog(message = "Pipe reset")
                viewModel.dispatch(GameAction.PipeExit, pipeIndex = pipeIndex)
            }
        }
    }
}

@Preview(widthDp = 411, heightDp = 660)
@Composable
fun PipeScreen() {
    val pipeState = PreviewPipeState

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        GetUpPipe(height = pipeState.upHeight,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = pipeState.offset)
        )

        GetDownPipe(height = pipeState.downHeight,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = pipeState.offset)
        )
    }
}