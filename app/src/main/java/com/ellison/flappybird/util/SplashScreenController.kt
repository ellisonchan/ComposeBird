package com.ellison.flappybird.util

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Application
import android.graphics.Path
import android.os.Build
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.annotation.RequiresApi
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import com.ellison.flappybird.R
import com.ellison.flappybird.viewmodel.GameViewModel

class SplashScreenController(
    private val splashScreen: SplashScreen,
    private val viewModel: GameViewModel
) {
    private val defaultExitDuration: Long by lazy {
        viewModel.getApplication<Application>()
            .resources.getInteger(R.integer.splash_exit_total_duration).toLong()
    }

    fun customizeSplashScreen() {
        // keepSplashScreenLonger()
        customizeSplashScreenExit()
    }

    // Keep splash screen showing till data initialized.
    private fun keepSplashScreenLonger() {
        Log.d("Splash", "SplashActivity#keepSplashScreenLonger()")
        splashScreen.setKeepVisibleCondition { !viewModel.isDataReady() }
    }

    // Customize splash screen exit animator.
    private fun customizeSplashScreenExit() {
        splashScreen.setOnExitAnimationListener { splashScreenViewProvider ->
            Log.d(
                "Splash", "SplashScreen#onSplashScreenExit view:$splashScreenViewProvider"
                        + " view:${splashScreenViewProvider.view}"
                        + " icon:${splashScreenViewProvider.iconView}"
            )

            val onExit = {
                splashScreenViewProvider.remove()
            }

            // defaultExitDuration = getRemainingDuration(splashScreenViewProvider)

            // hookViewLayout(splashScreenViewProvider)

            showSplashExitAnimator(splashScreenViewProvider.view, onExit)

            showSplashIconExitAnimator(splashScreenViewProvider.iconView, onExit)
        }
    }

    // Show exit animator for splash screen view.
    private fun showSplashExitAnimator(splashScreenView: View, onExit: () -> Unit = {}) {
        Log.d("Splash", "showSplashExitAnimator() splashScreenView:$splashScreenView" +
                " context:${splashScreenView.context}" +
                " parent:${splashScreenView.parent}")

        // Create your custom animation set.
        val slideUp = ObjectAnimator.ofFloat(
            splashScreenView,
            View.TRANSLATION_Y,
            0f,
            -splashScreenView.height.toFloat()
        )

        val slideLeft = ObjectAnimator.ofFloat(
            splashScreenView,
            View.TRANSLATION_X,
            0f,
            -splashScreenView.width.toFloat()
        )

        val scaleXOut = ObjectAnimator.ofFloat(
            splashScreenView,
            View.SCALE_X,
            1.0f,
            0f
        )

        val alphaOut = ObjectAnimator.ofFloat(
            splashScreenView,
            View.ALPHA,
            1f,
            0f
        )

        val scaleOut = ObjectAnimator.ofFloat(
            splashScreenView,
            View.SCALE_X,
            View.SCALE_Y,
            Path().apply {
                moveTo(1.0f, 1.0f)
                lineTo(0f, 0f)
            }
        )

        AnimatorSet().run {
            duration = defaultExitDuration
            interpolator = AnticipateInterpolator()
            Log.d("Splash", "showSplashExitAnimator() duration:$duration")

            // playTogether(alphaOut)
            playTogether(scaleOut, alphaOut)

            doOnEnd {
                Log.d("Splash", "showSplashExitAnimator() onEnd")
                // Log.d("Splash", "showSplashExitAnimator() onEnd remove")
                // onExit()
            }

            start()
        }
    }

    // Show exit animator for splash icon.
    private fun showSplashIconExitAnimator(iconView: View, onExit: () -> Unit = {}) {
        Log.d("Splash", "showSplashIconExitAnimator()" +
                " iconView[:${iconView.width}, ${iconView.height}]" +
                " translation[:${iconView.translationX}, ${iconView.translationY}]")

        val alphaOut = ObjectAnimator.ofFloat(
            iconView,
            View.ALPHA,
            1f,
            0f
        )

        // Bird scale out animator.
        val scaleOut = ObjectAnimator.ofFloat(
            iconView,
            View.SCALE_X,
            View.SCALE_Y,
            Path().apply {
                moveTo(1.0f, 1.0f)
                lineTo(0.3f, 0.3f)
            }
        )

        // Bird slide up to center.
        val slideUp = ObjectAnimator.ofFloat(
            iconView,
            View.TRANSLATION_Y,
            0f,
            // iconView.translationY,
            -(iconView.height).toFloat() * 2.25f
        ).apply {
            addUpdateListener {
                Log.d("Splash", "showSplashIconExitAnimator() translationY:${iconView.translationY}")
            }
        }

        AnimatorSet().run {
            interpolator = AnticipateInterpolator()
            duration = defaultExitDuration
            Log.d("Splash", "showSplashIconExitAnimator() duration:$duration")

            playTogether(alphaOut, scaleOut, slideUp)
            // playTogether(scaleOut, slideUp)
            // playTogether(slideUp)

            doOnEnd {
                Log.d("Splash", "showSplashIconExitAnimator() onEnd remove")
                onExit()
            }

            start()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun hookViewLayout(splashScreenViewProvider: SplashScreenViewProvider) {
        Log.d("Splash", "hookViewLayout()")
        val rootWindowInsets = splashScreenViewProvider.view.rootWindowInsets

        Log.d("Splash", "hookViewLayout() rootWindowInsets:$rootWindowInsets" +
                // "\n systemWindowInsets:${rootWindowInsets.systemWindowInsets}" +
                " top:${rootWindowInsets.systemWindowInsetTop}" +
                " bottom${rootWindowInsets.systemWindowInsetBottom}" +
                " icon translationY:${splashScreenViewProvider.iconView.translationY}")
    }

    private fun getRemainingDuration(splashScreenView: SplashScreenViewProvider): Long {
        // Get the duration of the animated vector drawable.
        val animationDuration = splashScreenView.iconAnimationDurationMillis

        // Get the start time of the animation.
        val animationStart = splashScreenView.iconAnimationStartMillis

        // Calculate the remaining duration of the animation.
        return if (animationDuration == 0L || animationStart == 0L)
            defaultExitDuration
        else (animationDuration - SystemClock.uptimeMillis() + animationStart)
            .coerceAtLeast(0L)
    }
}