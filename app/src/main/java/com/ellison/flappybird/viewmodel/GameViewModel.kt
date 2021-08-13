package com.ellison.flappybird.viewmodel

import android.app.Application
import android.os.SystemClock
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ellison.flappybird.util.DensityUtil
import com.ellison.flappybird.util.LogUtil
import com.ellison.flappybird.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        const val WORK_DURATION = 2000L
    }

    private val initTime = SystemClock.uptimeMillis()

    fun isDataReady() = SystemClock.uptimeMillis() - initTime > WORK_DURATION

    private val _viewState = MutableStateFlow(ViewState())

    val viewState = _viewState.asStateFlow()

    fun dispatch(action: GameAction,
                 playZoneSize: Pair<Int, Int> = Pair(0, 0),
                 pipeIndex: Int = -1,
                 roadIndex: Int = -1
    ) {
        LogUtil.printLog(message = "dispatch action:$action size:[${playZoneSize.first},${playZoneSize.second}]")

        if (playZoneSize.first > 0 && playZoneSize.second > 0) {
            viewState.value.playZoneSize = playZoneSize
        }

        if (pipeIndex > -1) {
            viewState.value.targetPipeIndex = pipeIndex
        }

        if (roadIndex > -1) {
            viewState.value.targetRoadIndex = roadIndex
        }

        response(action, viewState.value)
    }

    private fun response(action: GameAction, state: ViewState) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                emit(when (action) {
                    GameAction.Start -> run {
                        LogUtil.printLog(message = "ACTION Start")
                        state.copy(
                            gameStatus = GameStatus.Running,
                        )
                    }

                    GameAction.AutoTick -> run {
                        LogUtil.printLog(message = "ACTION AutoTick")

                        // Do nothing when still waiting.
                        if (state.gameStatus == GameStatus.Waiting) {
                            return@run state
                        }

                        // Only quick fall when dying status.
                        if (state.gameStatus == GameStatus.Dying) {
                            val newBirdState = state.birdState.quickFall()
                            return@run state.copy(
                                birdState = newBirdState
                            )
                        }

                        if (state.gameStatus == GameStatus.Over) {
                            return@run state.copy()
                        }

                        LogUtil.printLog(message = "AutoTick pipe move & bird fall")

                        // Move pipes left.
                        val newPipeStateList: List<PipeState> = listOf(
                            state.pipeStateList[0].move(),
                            state.pipeStateList[1].move()
                        )

                        // Bird fall.
                        val newBirdState = state.birdState.fall()

                        // Move road.
                        val newRoadStateList: List<RoadState> = listOf(
                            state.roadStateList[0].move(),
                            state.roadStateList[1].move()
                        )

                        state.copy(
                            gameStatus = GameStatus.Running,
                            birdState = newBirdState,
                            pipeStateList = newPipeStateList,
                            roadStateList = newRoadStateList
                        )
                    }

                    GameAction.TouchLift -> run {
                        LogUtil.printLog(message = "ACTION TouchLift")

                        if (state.gameStatus == GameStatus.Over) {
                            return@run state.copy()
                        }

                        // Not lift when already dying.
                        if (state.gameStatus == GameStatus.Dying) {
                            return@run state.copy()
                        }

                        // Bird lift
                        val newBirdState = state.birdState.lift()

                        state.copy(
                            gameStatus = GameStatus.Running,
                            birdState = newBirdState
                        )
                    }

                    GameAction.ScreenSizeDetect -> run {
                        LogUtil.printLog(message = "ACTION ScreenSizeDetect")

                        // Correct pipes' height and distance value according to true screen size.
                        // 1. Convert px to dp
                        val playZoneHeightInDp = DensityUtil.dxToDp(
                            getApplication<Application>().resources,
                            state.playZoneSize.second
                        )

                        // 2. Change height and distance
                        TotalPipeHeight = playZoneHeightInDp.dp
                        HighPipe = TotalPipeHeight * MaxPipeFraction
                        MiddlePipe = TotalPipeHeight * CenterPipeFraction
                        LowPipe = TotalPipeHeight * MinPipeFraction
                        PipeDistance = TotalPipeHeight * PipeDistanceFraction

                        // change bird constants.
                        BirdSizeHeight = PipeDistance * BirdPipeDistanceFraction
                        BirdSizeWidth = BirdSizeHeight * 1.44f

                        // 3. Reset pipe state instance
                        val newPipeStateList: List<PipeState> = listOf(
                            state.pipeStateList[0].correct(),
                            state.pipeStateList[1].correct()
                        )

                        // reset bird state instance
                        val newBirdState = state.birdState.correct()

                        LogUtil.printLog(message = "newPipeStateList:$newPipeStateList + newBirdState:$newBirdState")

                        state.copy(
                            birdState = newBirdState,
                            pipeStateList = newPipeStateList
                        )
                    }

                    GameAction.PipeExit -> run {
                        LogUtil.printLog(message = "ACTION PipeReset")

                        // Reset the pipe.
                        val newPipeStateList: List<PipeState> =
                            if (state.targetPipeIndex == 0) {
                                listOf(
                                    state.pipeStateList[0].reset(),
                                    state.pipeStateList[1]
                                )
                            } else {
                                listOf(
                                    state.pipeStateList[0],
                                    state.pipeStateList[1].reset()
                                )
                            }

                        state.copy(
                            gameStatus = GameStatus.Running,
                            pipeStateList = newPipeStateList
                        )
                    }

                    GameAction.RoadExit -> run {
                        LogUtil.printLog(message = "ACTION RoadExit")

                        val newRoadState: List<RoadState> =
                            if (state.targetRoadIndex == 0) {
                                listOf(state.roadStateList[0].reset(), state.roadStateList[1])
                            } else {
                                listOf(state.roadStateList[0], state.roadStateList[1].reset())
                            }

                        state.copy(
                            gameStatus = GameStatus.Running,
                            roadStateList = newRoadState
                        )
                    }

                    GameAction.HitGround -> run {
                        LogUtil.printLog(message = "ACTION HitGround")
                        state.copy(
                            gameStatus = GameStatus.Over
                        )
                    }

                    GameAction.HitPipe -> run {
                        LogUtil.printLog(message = "ACTION HitPipe")

                        // Not quick fall again when already dying.
                        if (state.gameStatus == GameStatus.Dying) {
                            return@run state.copy()
                        }

                        // Bird quick fall and wait to hit ground.
                        // Bird fall.
                        val newBirdState = state.birdState.quickFall()

                        state.copy(
                            gameStatus = GameStatus.Dying,
                            birdState = newBirdState
                        )
                    }

                    GameAction.CrossedPipe -> run {
                        LogUtil.printLog(message = "ACTION CrossedPipe")
                        val targetPipeState = state.pipeStateList[state.targetPipeIndex]

                        // Need consider pipe's offset due to reset flag but count action triggered.
                        // Not update score when current pipe already calculated.
                        if (targetPipeState.counted
                                || (!targetPipeState.counted && targetPipeState.offset > 0.dp)) {
                            return@run state.copy()
                        }
                        LogUtil.printLog(message = "ACTION CrossedPipe With:$targetPipeState")

                        // Mark current pipe.
                        val countedPipeState = targetPipeState.count()
                        val newPipeStateList = if (state.targetPipeIndex == 0) {
                            listOf(countedPipeState, state.pipeStateList[1])
                        } else {
                            listOf(state.pipeStateList[0], countedPipeState)
                        }

                        state.copy(
                            pipeStateList = newPipeStateList,
                            score = state.score + 1,
                            bestScore = (state.score + 1).coerceAtLeast(state.bestScore)
                        )
                    }

                    GameAction.Restart -> run {
                        LogUtil.printLog(message = "ACTION Restart Max Score:${state.bestScore}")
                        // Keep max score status.
                        state.reset(state.bestScore)
                    }

//                    GameAction.Pause -> run {
//                        state.copy(
//                            gameStatus = GameStatus.Paused
//                        )
//                    }
//
//                    GameAction.Resume -> run {
//                        state.copy(
//                            gameStatus = GameStatus.Running
//                        )
//                    }
                })
            }
        }
    }

    private fun emit(state: ViewState) {
        _viewState.value = state
    }
}