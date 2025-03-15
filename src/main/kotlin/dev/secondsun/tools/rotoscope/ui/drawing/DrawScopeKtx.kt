package dev.secondsun.tools.rotoscope.ui

import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import dev.secondsun.tools.rotoscope.ui.vo.Polygon

fun DrawScope.drawPoly(poly : Polygon) {
    val wallpaint: Paint = Paint()
    wallpaint.color = poly.color
    wallpaint.style = PaintingStyle.Fill

    val wallpath: Path = Path()
    wallpath.reset() // only needed when reusing this path for a new build
    wallpath.moveTo(
        poly.points[0].x.toFloat(),
        poly.points[0].y.toFloat()
    ) // used for first point
    poly.points.reversed().forEach {
        wallpath.lineTo(it.x.toFloat(), it.y.toFloat())
        this.drawPath(path = wallpath, color = poly.color, style = Fill)
    }
}