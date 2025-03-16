package dev.secondsun.tools.rotoscope.ui.drawing

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import dev.secondsun.tools.rotoscope.ui.vo.PolyPoint
import dev.secondsun.tools.rotoscope.ui.vo.Polygon
import dev.secondsun.tools.rotoscope.ui.vo.PolygonStack

class RotoscopeAppModel(val dataSource : TempDataSource = TempDataSource()) {


    private var _frame = mutableIntStateOf(0)
    val frame : IntState = _frame

    private var _polyIndex = mutableIntStateOf(0)
    val polyIndex : IntState = _polyIndex


    private var _filePath = mutableStateOf("")
    val filePath : State<String> = _filePath

    val polygonList = mutableStateOf(listOf(Polygon()))

    private var polygonStack = dataSource.getPolyStack(frame.value, filePath.value)


    fun polyIndex(index : Int) {
        _polyIndex.value = index
    }

    fun addPointToCurrentPoly(point : PolyPoint) {


        if (polygonStack.polys.isEmpty()) {
            polygonStack.polys.add(Polygon())
        }

        polygonStack.polys[_polyIndex.value].addPoint(point)
        polygonList.value = polygonStack.polys.toList()

    }

    fun removePointToCurrentPoly(point : PolyPoint) {
        polygonStack.polys[_polyIndex.value].removePoint(point)
        polygonList.value = polygonStack.polys.toList()
    }

    fun frame(frameNumber : Int) {
        _frame.value = frameNumber
        polyIndex(0)
        polygonStack = dataSource.getPolyStack(frame.value, filePath.value)
        polygonList.value = polygonStack.polys.toList()
    }

    fun filePath(path:String) {
        _filePath.value = path
        polyIndex(0)
        polygonStack = dataSource.getPolyStack(frame.value, filePath.value)
        polygonList.value = polygonStack.polys.toList()
    }


    fun addPolygon(index: Int = polyIndex.value, polygon: Polygon = Polygon()) {
        polygonStack.polys.add(index+1, polygon)
        polygonList.value = polygonStack.polys.toList()
        polyIndex(index + 1)
    }
    fun removePolygon(index: Int = polyIndex.value) {
        if (!polygonStack.polys.isEmpty()
            && index >=0 && index <polygonStack.polys.size) {
            polygonStack.polys.removeAt(index)
            if (polyIndex.value >= polygonStack.polys.size) {
                polyIndex(index - 1)
            }
            polygonList.value = polygonStack.polys.toList()
        }
    }

    fun swapPolys(to: Int, from: Int) {
        polygonStack.polys.apply {add(to, removeAt(from))}
        polygonList.value = polygonStack.polys.toList()
    }


}

class TempDataSource() {

    fun getPolyStack(frame:Int, path:String) :PolygonStack {
        return PolygonStack()
    }

    fun updatePolygonStack(frame:Int, path:String) {
        //Stub
    }

}
