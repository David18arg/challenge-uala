package com.david.cityapp.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.david.cityapp.R
import kotlinx.coroutines.delay

@Composable
fun SplashLottie(onFinished: () -> Unit, duration: Long?) {

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.splash_lottie))
    
    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier = Modifier.fillMaxSize().background(Color.White)
    )
    
    LaunchedEffect(composition) {
        delay(duration ?: 2000L)
        onFinished()
    }
}