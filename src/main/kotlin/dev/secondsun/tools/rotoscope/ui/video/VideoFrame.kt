package dev.secondsun.tools.rotoscope.ui.video

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import dev.secondsun.tools.rotoscope.ui.drawing.drawPoly
import dev.secondsun.tools.rotoscope.ui.drawing.RotoscopeAppModel
import dev.secondsun.tools.rotoscope.ui.vo.PolyPoint
import dev.secondsun.tools.rotoscope.ui.vo.Polygon

@Composable
fun VideoFrame(modifier: Modifier = Modifier, videoUtil: VideoUtil, model : RotoscopeAppModel) {
    println("VideoFrame Composition")
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var boxSize by remember { mutableStateOf(IntSize.Zero) } // Store the Box size

    val polygons = model.polygonList.value

    Box(modifier = modifier) {

        Column(
            modifier = Modifier
                .fillMaxSize()


        ) {
            Box(Modifier
                .fillMaxSize().weight(1f)) {

                Canvas(
                    modifier = Modifier
                        .fillMaxSize().onGloballyPositioned { coordinates ->  // Get the size here
                            boxSize = coordinates.size
                        }.pointerInput(Unit) {
                            detectTransformGestures { centroid, pan, zoom, rotation ->
                                scale *= zoom
                                offset += pan
                            }
                        }.pointerInput(Unit){
                            detectTapGestures {
                                model.addPointToCurrentPoly(PolyPoint((it.x-offset.x).toInt(), (it.y-offset.y).toInt()))

                            }
                        }
                ) {

                    drawImage(
                        image = videoUtil.image.value.toComposeImageBitmap(),
                        srcOffset = IntOffset(-offset.x.toInt(), -offset.y.toInt())
                    )
                    polygons.forEach {poly->
                        drawPoly(poly = Polygon(poly.colorIndex,poly.key).apply { points.addAll(poly.points.map {
                                PolyPoint(it.x+offset.x.toInt(), it.y+offset.y.toInt())
                            })
                        }, palette =model.palette)
                    }
                }
            }
            VideoControlBar(modifier = Modifier.fillMaxWidth().wrapContentHeight().background(MaterialTheme.colors.primary), videoUtil)

        }


    }
}