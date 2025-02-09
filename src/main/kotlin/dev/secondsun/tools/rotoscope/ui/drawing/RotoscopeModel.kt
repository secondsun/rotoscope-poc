package dev.secondsun.tools.rotoscope.ui.drawing

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.flow.Flow

class RotoscopeModel {

    //Frame No : Rotoscope Frame
    private val frameIndex = mutableMapOf(Pair(0, mutableStateListOf<Polygon>()))

    fun polys(frame:Int) : SnapshotStateList<Polygon> {
        return frameIndex.getOrPut(frame, {mutableStateListOf<Polygon>()})
    }

    fun addPolygon(frame:Int, polygon: Polygon) {
        frameIndex.getOrPut(frame, {mutableStateListOf<Polygon>()}).add(polygon)
    }

}

data class Polygon(val p1:PolygonPoint, val p2:PolygonPoint, val p3:PolygonPoint, val color :Int)

data class PolygonPoint(val x:Int, val y :Int ) {

}
