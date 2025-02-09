package dev.secondsun.tools.rotoscope.ui.video

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
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
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import dev.secondsun.tools.rotoscope.ui.drawing.Polygon
import dev.secondsun.tools.rotoscope.ui.drawing.PolygonPoint
import dev.secondsun.tools.rotoscope.ui.drawing.RotoscopeCanvas
import dev.secondsun.tools.rotoscope.ui.drawing.RotoscopeModel

@Composable
fun VideoFrame(modifier: Modifier = Modifier, videoUtil: VideoUtil) {

    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var boxSize by remember { mutableStateOf(IntSize.Zero) } // Store the Box size
    var model by remember{ mutableStateOf( RotoscopeModel() ) }

    var  p1 : PolygonPoint? = remember{null}
    var  p2 : PolygonPoint? = remember{null}
    var  p3 : PolygonPoint? = remember{null}

    var polygons = model.polys(0)

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
                                println("p1 $p1 p2 $p2 p3 $p3")
                                if (p1 == null) {
                                    p1 = PolygonPoint((it.x-offset.x).toInt(), (it.y-offset.y).toInt())
                                } else if (p2 == null) {
                                    p2 = PolygonPoint((it.x-offset.x).toInt(), (it.y-offset.y).toInt())
                                } else if (p3 == null) {
                                    p3 = PolygonPoint((it.x-offset.x).toInt(), (it.y-offset.y).toInt())
                                    model.addPolygon(0, Polygon(p1!!,p2!!,p3!!,1))
                                    p1 = null
                                    p3 = null
                                    p2 = null
                                }
                            }
                        }
                ) {

                    drawImage(
                        image = videoUtil.image.value.toComposeImageBitmap(),
                        srcOffset = IntOffset(-offset.x.toInt(), -offset.y.toInt())
                    )
                    polygons.forEach {
                        val wallpaint: Paint = Paint()
                        wallpaint.color = Color.Gray
                        wallpaint.style = PaintingStyle.Fill

                        val wallpath: Path = Path()
                        wallpath.reset() // only needed when reusing this path for a new build
                        wallpath.moveTo(it.p1.x.toFloat(), it.p1.y.toFloat()) // used for first point
                        wallpath.lineTo(it.p2.x.toFloat(), it.p2.y.toFloat())
                        wallpath.lineTo(it.p3.x.toFloat(), it.p3.y.toFloat())
                        wallpath.lineTo(it.p1.x.toFloat(), it.p1.y.toFloat())
                        this.drawPath(path = wallpath, color = Color.Gray, style = Fill )
                    }
                }
            }
            VideoControlBar(modifier = Modifier.fillMaxWidth().wrapContentHeight().background(MaterialTheme.colors.primary), videoUtil)

        }


    }
}