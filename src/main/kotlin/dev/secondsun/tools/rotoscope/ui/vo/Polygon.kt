package dev.secondsun.tools.rotoscope.ui.vo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import java.util.*

class Polygon(val color:Color = Color.Gray, val key: UUID = UUID.randomUUID()) {
    val normalizePoints: List<PolyPoint>
        get() {
            return this.points.map { PolyPoint(it.x - bounds.minX, it.y - bounds.minY) }.toList()
        }

    private val _bounds = mutableStateOf (Bounds ())
    val bounds  by _bounds

    private val _points = mutableStateListOf<PolyPoint>()
    val points : SnapshotStateList<PolyPoint> = _points

    fun addPoint(point : PolyPoint): Polygon {
        _points.add(point)
        updateBounds(point)
        return this
    }
    fun removePoint(point : PolyPoint): Polygon {
        _points.remove(point)
        updateBounds(point)
        return this
    }

    private fun updateBounds(point: PolyPoint) {
        var minX = Int.MAX_VALUE
        var minY = Int.MAX_VALUE

        var maxX = Int.MIN_VALUE
        var maxY = Int.MIN_VALUE

        points.forEach {
            if(it.x < minX) {
                minX = it.x
            }
            if(it.y < minY) {
                minY = it.y
            }
            if(it.x > maxX) {
                maxX = it.x
            }
            if(it.y > maxY) {
                maxY = it.y
            }
        }

        _bounds.value = Bounds(maxX- minX, maxY-minY, minX, minY)

    }

    fun recalculateBounds() {
        points.forEach {updateBounds(it)}
    }


}

data class Bounds(val width: Int = 0, val height: Int = 0, val minX: Int = 0, val minY: Int = 0)

data class PolyPoint(val x :Int,val y :Int)

data class PolygonStack( val polys:MutableList<Polygon> = mutableListOf<Polygon>())