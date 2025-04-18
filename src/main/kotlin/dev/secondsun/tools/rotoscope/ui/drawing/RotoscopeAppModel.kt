package dev.secondsun.tools.rotoscope.ui.drawing

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import dev.secondsun.tools.rotoscope.ui.vo.PolyPoint
import dev.secondsun.tools.rotoscope.ui.vo.Polygon
import dev.secondsun.tools.rotoscope.ui.vo.PolygonStack


import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import dev.secondsun.tools.rotoscope.data.ProjectRepository
import dev.secondsun.tools.rotoscope.data.TempDataSource
import dev.secondsun.tools.rotoscope.ui.vo.Project
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class RotoscopeAppModel(
    val dataSource: TempDataSource = TempDataSource(),
    val projectRepository: ProjectRepository? = null
) {
    // Generate random colors for the color palette
    val palette: IntArray
        get() {
            return IntArray(15).map {
                Color(
                    (Math.random() * 255).toInt(),
                    (Math.random() * 255).toInt(),
                    (Math.random() * 255).toInt()
                ).toArgb()
            }.toIntArray()
        }

    // Selected tool state
    private var _tool = mutableStateOf(Tools.PolygonStackTool)
    val tool: State<Tools> = _tool

    // Project state
    private val _project = MutableStateFlow(Project())
    val project: StateFlow<Project> = _project.asStateFlow()
    
    // Polygon list for UI
    val polygonList = mutableStateOf(listOf(Polygon()))
    
    // Current PolygonStack based on current frame
    private var currentPolygonStack = PolygonStack()

    init {
        // Initialize with empty project
        updatePolygonList()
    }

    // Current frame accessor and setter
    val frame get() = project.value.currentFrame
    fun frame(frameNumber: Int) {
        // Save current polystack before changing frames
        saveCurrentPolystack()
        
        // Update frame and reload polystack
        _project.value.currentFrame.value = frameNumber
        updatePolygonList()
    }

    // Current polygon index accessor and setter
    val polyIndex get() = project.value.currentPolyIndex
    fun polyIndex(index: Int) {
        _project.value.currentPolyIndex.value = index
    }

    // File path accessor and setter
    val filePath get() = _project.value.filePath
    fun filePath(path: String) {
        // Create a new project with the given file path
        val newProject = Project(filePath = path)
        _project.value = newProject
        updatePolygonList()
    }

    // Set the current tool
    fun setTool(tool: Tools) {
        _tool.value = tool
    }

    // Add a point to the current polygon
    fun addPointToCurrentPoly(point: PolyPoint) {
        if (currentPolygonStack.polys.isEmpty()) {
            currentPolygonStack.polys.add(Polygon())
            if (polyIndex.value < 0) {
                polyIndex(0)
            }
        }

        currentPolygonStack.polys[polyIndex.value].addPoint(point)
        updatePolygonList()
        _project.value.markModified()
    }

    // Remove a point from the current polygon
    fun removePointToCurrentPoly(point: PolyPoint) {
        if (currentPolygonStack.polys.isNotEmpty() && polyIndex.value >= 0 && polyIndex.value < currentPolygonStack.polys.size) {
            currentPolygonStack.polys[polyIndex.value].removePoint(point)
            updatePolygonList()
            _project.value.markModified()
        }
    }

    // Add a new polygon
    fun addPolygon(index: Int = polyIndex.value, polygon: Polygon = Polygon()) {
        if (index >= 0 && index < currentPolygonStack.polys.size) {
            currentPolygonStack.polys.add(index + 1, polygon)
        } else {
            currentPolygonStack.polys.add(polygon)
        }
        updatePolygonList()
        polyIndex(index + 1)
        _project.value.markModified()
    }

    // Remove a polygon
    fun removePolygon(index: Int = polyIndex.value) {
        if (currentPolygonStack.polys.isNotEmpty() && index >= 0 && index < currentPolygonStack.polys.size) {
            currentPolygonStack.polys.removeAt(index)
            if (polyIndex.value >= currentPolygonStack.polys.size) {
                polyIndex(index - 1)
            }
            updatePolygonList()
            _project.value.markModified()
        }
    }

    // Swap two polygons
    fun swapPolys(to: Int, from: Int) {
        if (currentPolygonStack.polys.isNotEmpty() && 
            to >= 0 && to < currentPolygonStack.polys.size &&
            from >= 0 && from < currentPolygonStack.polys.size) {
            currentPolygonStack.polys.apply { add(to, removeAt(from)) }
            updatePolygonList()
            _project.value.markModified()
        }
    }

    // Load a project from a file
    suspend fun loadProject(file: File): Boolean {
        projectRepository?.loadProject(file)?.let { loadedProject ->
            // Save current project before loading
            //saveCurrentPolystack()
            
            // Set new project
            _project.value = loadedProject
            currentPolygonStack = _project.value.getCurrentPolystack()
            // Update UI
            updatePolygonList()
            return true
        }
        return false
    }

    // Save current project to a file
    suspend fun saveProject(file: File): Boolean {
        // Save current polystack before saving project
        saveCurrentPolystack()
        
        // Save project
        return projectRepository?.saveProject(_project.value, file) ?: false
    }

    // Create a new project
    fun newProject(name: String = "Untitled Project") {
        _project.value = Project(name = name)
        currentPolygonStack = _project.value.getCurrentPolystack()
        addPolygon()
        updatePolygonList()

    }

    // Update the polygon list for UI
    private fun updatePolygonList() {
        //currentPolygonStack = _project.value.getCurrentPolystack()
        polygonList.value = currentPolygonStack.polys.toList()
    }

    // Save the current polystack to the project
    private fun saveCurrentPolystack() {
        _project.value.updatePolystack(_project.value.currentFrame.value, currentPolygonStack)
    }
}

