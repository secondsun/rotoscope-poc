package dev.secondsun.tools.rotoscope.ui.vo

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.*

/**
 * Represents a complete rotoscope project.
 */
@Serializable
data class Project(
    val name: String = "Untitled Project",
    val filePath: String = "",
    val framePolystacks: MutableMap<Int, PolyStackData> = mutableMapOf(),
    val palette: IntArray = generateDefault15BitPalette()
) {

    // Non-serializable UI state
    @Transient
    val currentFrame = mutableStateOf(0)

    @Transient
    val currentPolyIndex = mutableStateOf(0)

    @Transient
    val modified = mutableStateOf(false)

    @Transient
    val currentPaletteIndex = mutableStateOf(0)

    fun markModified() {
        modified.value = true
    }

    fun getCurrentPolystack(): PolygonStack {
        val frame = currentFrame.value
        val polystackData = framePolystacks.getOrPut(frame) { PolyStackData() }
        return PolygonStack(polystackData.polygons.map { it.toPolygon() }.toMutableList())
    }

    fun updatePolystack(frame: Int, polygonStack: PolygonStack) {
        framePolystacks[frame] = PolyStackData(polygonStack.polys.map { it.toPolygonData() })
        markModified()
    }

    companion object {
        /**
         * Generates a default palette with 15 colors from a 15-bit RGB color space
         * 15-bit RGB uses 5 bits per color channel (32 values per channel)
         */
        fun generateDefault15BitPalette(): IntArray {
            val colors = IntArray(15)

            // Common colors from 15-bit color space (5 bits per channel)
            colors[0] = Color(0, 0, 0).toArgb() // Black
            colors[1] = Color(31 * 8, 0, 0).toArgb() // Red
            colors[2] = Color(0, 31 * 8, 0).toArgb() // Green
            colors[3] = Color(0, 0, 31 * 8).toArgb() // Blue
            colors[4] = Color(31 * 8, 31 * 8, 0).toArgb() // Yellow
            colors[5] = Color(31 * 8, 0, 31 * 8).toArgb() // Magenta
            colors[6] = Color(0, 31 * 8, 31 * 8).toArgb() // Cyan
            colors[7] = Color(31 * 8, 31 * 8, 31 * 8).toArgb() // White
            colors[8] = Color(24 * 8, 24 * 8, 24 * 8).toArgb() // Light Gray
            colors[9] = Color(16 * 8, 16 * 8, 16 * 8).toArgb() // Gray
            colors[10] = Color(8 * 8, 8 * 8, 8 * 8).toArgb() // Dark Gray
            colors[11] = Color(31 * 8, 16 * 8, 8 * 8).toArgb() // Orange
            colors[12] = Color(16 * 8, 8 * 8, 24 * 8).toArgb() // Purple
            colors[13] = Color(8 * 8, 24 * 8, 8 * 8).toArgb() // Light Green
            colors[14] = Color(24 * 8, 16 * 8, 8 * 8).toArgb() // Brown

            return colors
        }

        /**
         * Converts a RGB color to 15-bit color space (5 bits per channel)
         */
        fun convertTo15BitColor(color: Color): Color {
            // Get original components
            val r = color.red
            val g = color.green
            val b = color.blue

            // Convert to 5 bits per channel (0-31) then back to 8 bits (0-255)
            val r5bit = ((r * 31) / 255).coerceIn(0f, 31f)
            val g5bit = ((g * 31) / 255).coerceIn(0f, 31f)
            val b5bit = ((b * 31) / 255).coerceIn(0f, 31f)

            // Convert back to 8-bit color space
            return Color(r5bit * 8, g5bit * 8, b5bit * 8, color.alpha)
        }
    }
}

/**
 * Serializable representation of a PolygonStack
 */
@Serializable
data class PolyStackData(
    val polygons: List<PolygonData> = emptyList()
)

/**
 * Serializable representation of a Polygon
 */
@Serializable
data class PolygonData(
    val color: ULong = 0.toULong(),
    val key: String = "",
    val points: List<PointData> = emptyList()
)

/**
 * Serializable representation of a PolyPoint
 */
@Serializable
data class PointData(
    val x: Int,
    val y: Int
)

/**
 * Extension functions to convert between UI and serializable models
 */
fun Polygon.toPolygonData(): PolygonData {
    return PolygonData(
        color = this.colorIndex.toULong(),
        key = this.key.toString(),
        points = this.points.map { PointData(it.x, it.y) }
    )
}

fun PolygonData.toPolygon(): Polygon {
    return Polygon(color.toInt(), UUID.fromString(key)).apply {
        points.addAll(this@toPolygon.points.map { PolyPoint(it.x, it.y) })
    }
}
