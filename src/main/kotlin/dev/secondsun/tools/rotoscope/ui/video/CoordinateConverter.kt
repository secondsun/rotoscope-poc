package dev.secondsun.tools.rotoscope.ui.video

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import dev.secondsun.tools.rotoscope.ui.vo.PolyPoint

/**
 * Utility class to handle conversions between screen space coordinates and polygon space coordinates.
 */
class CoordinateConverter(
    private val viewportSize: IntSize,
    polygonSpaceSize: IntSize,
    private val offset: Offset
) {

    private val viewportScaleX = this.viewportSize.width / polygonSpaceSize.width.toFloat()
    private val viewportScaleY = this.viewportSize.height / polygonSpaceSize.height.toFloat()

    /**
     * Converts a point from screen space to polygon space
     */
    fun screenToPolygon(screenPoint: Offset): PolyPoint {
        return PolyPoint(
            ((screenPoint.x / viewportScaleX) - offset.x).toInt(),
            ((screenPoint.y / viewportScaleY) - offset.y).toInt()
        )
    }

    /**
     * Converts a point from polygon space to screen space
     */
    fun polygonToScreen(polygonPoint: PolyPoint): Offset {
        return Offset(
            (polygonPoint.x + offset.x) * viewportScaleX,
            (polygonPoint.y + offset.y) * viewportScaleY
        )
    }

    /**
     * Converts a delta movement in screen space to polygon space
     */
    fun screenDeltaToPolygon(screenDelta: Offset): Offset {
        return Offset(
            screenDelta.x / viewportScaleX,
            screenDelta.y / viewportScaleY
        )
    }
}
