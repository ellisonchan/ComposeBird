package com.ellison.flappybird.view

import android.view.MotionEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ellison.flappybird.LogUtil
import com.ellison.flappybird.R
import com.ellison.flappybird.ScoreFontFamily
import com.ellison.flappybird.model.GameStatus
import com.ellison.flappybird.model.ViewState
import com.ellison.flappybird.ui.theme.GroundDividerPurple

@Composable
fun ScoreBoard(
    modifier: Modifier = Modifier,
    state: ViewState = ViewState(),
    clickable: Clickable = Clickable()
) {
    LogUtil.printLog(message = "StatusBoard() score:${state.score} max:${state.bestScore}")

    when (state.gameStatus) {
        GameStatus.Running -> RealTimeBoard(modifier, state.score)
        GameStatus.Over -> GameOverBoard(modifier, state.score, state.bestScore, clickable)
        else -> {}
    }
}

@Composable
fun RealTimeBoard(modifier: Modifier, score: Int = 13) {
    Box(
        modifier.fillMaxSize()
    ) {
        Text(
            text = score.toString(),
            modifier = Modifier
                .align(Center)
                .offset(y = RealTimeScoreHeightOffset),
            color = GroundDividerPurple,
            fontSize = SimpleScoreTextSize,
            fontFamily = ScoreFontFamily
        )
    }
}

@Composable
fun GameOverBoard(modifier: Modifier, score: Int = 13, maxScore: Int = 100, clickable: Clickable = Clickable()) {
    Box(
        modifier
    ) {
        Column(
            modifier.wrapContentSize()
                .offset(y = OverScoreHeightOffset)
                .align(Center)
        ) {
            // Score board
            GameOverScoreBoard(
                Modifier.align(CenterHorizontally),
                score,
                maxScore
            )

            Spacer(
                modifier = Modifier
                    .wrapContentWidth()
                    .height(40.dp)
            )

            // Control buttons
            GameOverButton(modifier = Modifier.wrapContentSize().align(CenterHorizontally), clickable)
        }
    }
}

@Composable
fun GameOverScoreBoard(modifier: Modifier, score: Int = 13, maxScore: Int = 100) {
    Box(
        modifier = modifier.wrapContentSize()
    ) {

        // Score board background
        Image(
            painter = painterResource(id = R.drawable.score_board_bg),
            contentScale = ContentScale.FillBounds,
            contentDescription = null,
            modifier = Modifier
                .width(ScoreBoardWidth)
                .height(ScoreBoardHeight)
                .align(Center)
        )

        Column(modifier = Modifier
            .align(Center)
            .wrapContentSize()) {

            LabelScoreField(modifier, R.drawable.score_bg, score)

            Spacer(
                modifier = Modifier
                    .wrapContentWidth()
                    .height(3.dp)
            )

            LabelScoreField(modifier, R.drawable.best_score_bg, maxScore)
        }
    }
}

@Composable
fun GameOverButton(modifier: Modifier, clickable: Clickable = Clickable()) {
    Row(
        modifier.wrapContentSize()
    ) {
        // Restart button
        Image(
            painter = painterResource(id = R.drawable.restart_button),
            contentScale = ContentScale.Fit,
            contentDescription = null,
            modifier = Modifier
                // .wrapContentSize()
                .size(ControlButtonWidth, ControlButtonHeight)
                // .scale(2f)
                .align(CenterVertically)
                .clickable(true) {
                    LogUtil.printLog(message = "Restart tapped")
                    clickable.onRestart()
                }
        )

        Spacer(modifier = Modifier
            .width(8.dp)
            .wrapContentHeight()
        )

        // Exit button
        Image(
            painter = painterResource(id = R.drawable.exit_button),
            contentScale = ContentScale.Fit,
            contentDescription = null,
            modifier = Modifier
                // .wrapContentSize()
                .size(ControlButtonWidth, ControlButtonHeight)
                // .scale(2f)
                .align(CenterVertically)
                .clickable(true) {
                    LogUtil.printLog(message = "Exit tapped")
                    clickable.onExit()
                }
        )
    }
}

@Composable
fun LabelScoreField(modifier: Modifier, infoDrawable: Int = R.drawable.score_bg, score: Int = 13) {
    Column(
        modifier = modifier.wrapContentSize()
    ) {
        // Score info image
        Image(
            painter = painterResource(id = infoDrawable),
            contentScale = ContentScale.Fit,
            contentDescription = null,
            modifier = Modifier
                // .wrapContentSize()
                .size(ScoreInfoButtonWidth, ScoreInfoButtonHeight)
                .align(CenterHorizontally)
        )

        Spacer(modifier = Modifier
            .wrapContentWidth()
            .height(3.dp))

        // Score
        Text(
            text = score.toString(),
            modifier = Modifier.align(CenterHorizontally),
            color = GroundDividerPurple,
            fontSize = OverScoreTextSize,
            fontFamily = ScoreFontFamily
        )
    }
}

@Preview()
@Composable
fun LabelScoreFieldPreview() {
    LabelScoreField(Modifier.wrapContentSize())
}

@Preview()
@Composable
fun GameOverScoreBoardPreview() {
    GameOverScoreBoard(Modifier.wrapContentSize())
}

@Preview()
@Composable
fun GameOverButtonPreview() {
    GameOverButton(Modifier.wrapContentSize())
}

@Preview(widthDp = 411, heightDp = 660)
@Composable
fun GameOverBoardPreview() {
    GameOverBoard(Modifier.fillMaxSize())
}

@Preview(widthDp = 411, heightDp = 660)
@Composable
fun RealTimeBoardPreview() {
    RealTimeBoard(Modifier.fillMaxSize())
}

val RealTimeScoreHeightOffset = (-150).dp
val OverScoreHeightOffset = 0.dp

val SimpleScoreTextSize = 60.sp
val OverScoreTextSize = 60.sp

val ScoreBoardWidth = 180.dp
val ScoreBoardHeight = 240.dp

val ScoreInfoButtonWidth = 80.dp
val ScoreInfoButtonHeight = 25.dp

val ControlButtonWidth = 120.dp
val ControlButtonHeight = 40.dp