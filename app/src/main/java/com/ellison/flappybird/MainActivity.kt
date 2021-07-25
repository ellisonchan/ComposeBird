package com.ellison.flappybird

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ellison.flappybird.model.GameAction
import com.ellison.flappybird.model.GameStatus
import com.ellison.flappybird.ui.theme.FlappyBirdTheme
import com.ellison.flappybird.view.Clickable
import com.ellison.flappybird.view.GameScreen
import com.ellison.flappybird.viewmodel.GameViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Expand screen to status bar.
        StatusBarUtil.transparentStatusBar(this)

        setContent {
            FlappyBirdTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    val gameViewModel: GameViewModel = viewModel()

                    // Send a auto tick action to view model and trigger game start.
                    LaunchedEffect(key1 = Unit) {
                        while (isActive) {
                            delay(AutoTickDuration)
                            if (gameViewModel.viewState.value.gameStatus != GameStatus.Waiting) {
                                gameViewModel.dispatch(GameAction.AutoTick)
                            }
                        }
                    }

                    val lifecycleOwner = LocalLifecycleOwner.current
                    DisposableEffect(key1 = Unit) {
                        val observer = object : LifecycleObserver {
                            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
                            fun onPause() {
                                // Todo send pause action
                            }

                            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
                            fun onResume() {
                                // Todo send resume action
                            }
                        }

                        lifecycleOwner.lifecycle.addObserver(observer)
                        onDispose {
                            lifecycleOwner.lifecycle.removeObserver(observer)
                        }
                    }

                    Flappy(Clickable(

                        onStart = {
                            gameViewModel.dispatch(GameAction.Start)
                        },

                        onTap = {
                            gameViewModel.dispatch(GameAction.TouchLift)
                        },

                        onRestart = {
                            gameViewModel.dispatch(GameAction.Restart)
                        },

                        onExit = {
                            finish()
                        }
                    ))
                }
            }
        }
    }
}

@Composable
fun Flappy(clickable: Clickable = Clickable()) {
    GameScreen(clickable = clickable)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FlappyBirdTheme {
        Flappy()
    }
}

const val AutoTickDuration = 100L // 300L Control bird and pipe speed.