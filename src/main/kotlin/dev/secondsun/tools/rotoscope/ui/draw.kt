import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import dev.secondsun.tools.rotoscope.ui.vo.Polygon

fun DrawScope.drawPoly(poly: Polygon, pathFilter: Path.() -> Path = { this }, palette: IntArray? = null) {
    drawPath(
        pathFilter(Path().apply {

            if (poly.points.isNotEmpty()) {
                val firstPoint = poly.points.first()

                moveTo(firstPoint.x.toFloat(), firstPoint.y.toFloat())

                poly.points.drop(1).forEach {
                    lineTo(it.x.toFloat(), it.y.toFloat())
                }

                close()

            }
        }),
        color = if (palette != null && poly.colorIndex >= 0 && poly.colorIndex < palette.size) {
                   Color(palette[poly.colorIndex])
                } else {
                   // Fallback color if palette is not available or index is out of bounds
                   Color.Gray
                },
        style = Fill
    )
}
