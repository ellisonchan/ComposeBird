package com.ellison.flappybird.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ellison.flappybird.model.HighPipe

import com.ellison.flappybird.R
import com.ellison.flappybird.model.LowPipe

@Composable
fun Pipe(
    height: Dp = HighPipe,
    up: Boolean = true, // Pipe layout in up or down
    modifier: Modifier = Modifier
) {

    Box(
        modifier
            .wrapContentWidth()
            .height(height)
    ) {
        Column {
            if (up) {
                PipePillar(Modifier.align(CenterHorizontally), height - 30.dp)
                PipeCover()
            } else {
                PipeCover()
                PipePillar(Modifier.align(CenterHorizontally), height - 30.dp)
            }
        }
    }
}

@Composable
fun PipeCover() {
    Image(
        painter = painterResource(id = R.drawable.pipe_cover),
        contentScale = ContentScale.FillBounds,
        contentDescription = null,
        modifier = Modifier.size(PipeCoverWidth, PipeCoverHeight)
    )
}

@Composable
fun PipePillar(modifier: Modifier = Modifier, height: Dp = 120.dp) {
    Image(
        painter = painterResource(id = R.drawable.pipe_pillar),
        contentScale = ContentScale.FillBounds,
        contentDescription = null,
        modifier = modifier.size(50.dp, height)
    )
}

@Composable
fun GetUpPipe(height: Dp, modifier: Modifier) {
    Pipe(
        height = height,
        modifier = modifier
    )
}

@Composable
fun GetDownPipe(height: Dp, modifier: Modifier) {
    Pipe(
        height = height,
        up = false,
        modifier = modifier
    )
}

//@Preview
//@Composable
//fun Cover() {
//    PipeCover()
//}
//
//@Preview
//@Composable
//fun Pillar() {
//    PipePillar()
//}
//
//@Preview
//@Composable
//fun UpPipe() {
//    Pipe(height = LowPipe)
//}
//
//@Preview
//@Composable
//fun DownPipe() {
//    Pipe(
//        height = LowPipe,
//        up = false
//    )
//}

@Preview(widthDp = 411, heightDp = 660)
@Composable
fun PipeGroupPreview() {
    Row(
        modifier = Modifier.wrapContentSize()
    ) {
        Spacer(modifier = Modifier.width(60.dp))

        Column(
            modifier = Modifier.wrapContentWidth().fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            PipeCover()
            Spacer(modifier = Modifier.height(48.dp))
            PipePillar(Modifier.align(CenterHorizontally))
            Spacer(modifier = Modifier.height(48.dp))
            PipeCover()
        }

        Spacer(modifier = Modifier.width(120.dp))

        Column(
            modifier = Modifier.wrapContentWidth().fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Pipe(
                height = LowPipe,
                up = false
            )
            Spacer(modifier = Modifier.height(180.dp))
            Pipe(height = LowPipe)
        }

        Spacer(modifier = Modifier.width(60.dp))
    }
}

val PipeCoverWidth = 60.dp
val PipeCoverHeight = 30.dp