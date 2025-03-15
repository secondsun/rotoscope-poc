package dev.secondsun.tools.rotoscope.ui.drawing

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntSize
import dev.secondsun.tools.rotoscope.ui.drawPoly
import dev.secondsun.tools.rotoscope.ui.vo.PolyPoint

@Composable
fun RotoscopeCanvas(modifier: Modifier = Modifier, model :RotoscopeAppModel = RotoscopeAppModel(), offset:Offset = Offset.Zero, boxSize:IntSize = IntSize.Zero){

    val polygons by model.polygonList

    Canvas(
        modifier = modifier.pointerInput(Unit) {

                detectTapGestures {
                    println(" click canvas${it.x-offset.x}, ${it.y-offset.y}")
                    model.addPointToCurrentPoly(PolyPoint((it.x-offset.x).toInt(), (it.y-offset.y).toInt()))
                }
            }
    ) {
        polygons.forEach {
            drawPoly(it)
        }
    }
}