package dev.secondsun.tools.rotoscope.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.coroutines.CoroutineScope
import video.VideoUtil
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

@Composable
fun VideoFrame(videoUtil: VideoUtil, ioScope: CoroutineScope) {
    Column {

        var scale by remember { mutableStateOf(1f) }
        var offset by remember { mutableStateOf(Offset.Zero) }
        var boxSize by remember { mutableStateOf(IntSize.Zero) } // Store the Box size

        val bitmap = videoUtil.image
        val imageBitmap = bitmap.toComposeImageBitmap()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .onGloballyPositioned { coordinates ->  // Get the size here
                    boxSize = coordinates.size
                }.pointerInput(Unit) {
                    detectTransformGestures { centroid, pan, zoom, rotation ->
                        scale *= zoom
                        offset += pan
                    }
                }

        ) {

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                val canvasWidth = size.width.toInt()
                val canvasHeight = size.height.toInt()

                val imageHeight = bitmap.height
                val imageWidth = bitmap.width

                // magic calculations

                drawImage(
                    image = imageBitmap,
                    srcOffset = IntOffset(-offset.x.toInt(),-offset.y.toInt())


                )
            }
            }



//    Canvas(Modifier.fillMaxSize().weight(1f)) {
//        drawImage(
//            image = videoUtil.image.toComposeImageBitmap(),
//        )
//    }
        VideoControlBar(modifier = Modifier.fillMaxWidth())
    }
}