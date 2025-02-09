package dev.secondsun.tools.rotoscope.ui.drawing

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntSize

@Composable
fun RotoscopeCanvas(modifier: Modifier = Modifier, model :RotoscopeModel = RotoscopeModel(), offset:Offset = Offset.Zero, boxSize:IntSize = IntSize.Zero){

    var  p1 : PolygonPoint? = remember{null}
    var  p2 : PolygonPoint? = remember{null}
    var  p3 : PolygonPoint? = remember{null}

    var polygons = model.polys(0)

    Canvas(
        modifier = modifier.pointerInput(Unit) {

                detectTapGestures {
                    println(" click canvas${it.x-offset.x}, ${it.y-offset.y}")
                    println("p1 = ${p1?:"null"}")
                    println("p2 = ${p2}")
                    println("p3 = ${p3}")
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