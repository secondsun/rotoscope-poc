package dev.secondsun.tools.rotoscope.ui.vo

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.UUID

/**
 * Represents a complete rotoscope project.
 */
@Serializable
data class Project(
    val name: String = "Untitled Project",
    val filePath: String = "",
    val framePolystacks: MutableMap<Int, PolyStackData> = mutableMapOf()
) {

    // Non-serializable UI state
    @Transient
    val currentFrame = mutableStateOf(0)
    @Transient
    val currentPolyIndex = mutableStateOf(0)
    @Transient
    val modified = mutableStateOf(false)
    
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
        color = this.color.value,
        key = this.key.toString(),
        points = this.points.map { PointData(it.x, it.y) }
    )
}

fun PolygonData.toPolygon(): Polygon {
    return Polygon(Color(color), UUID.fromString(key)).apply {
        points.addAll(this@toPolygon.points.map { PolyPoint(it.x, it.y) })
    }
}
