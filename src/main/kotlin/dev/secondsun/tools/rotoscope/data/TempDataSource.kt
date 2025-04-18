package dev.secondsun.tools.rotoscope.data

import dev.secondsun.tools.rotoscope.ui.vo.PolygonStack

/**
 * Temporary data source for backwards compatibility
 * This class will be removed once migration to ProjectRepository is complete
 */
class TempDataSource {
    private val cache = mutableMapOf<String, PolygonStack>()

    fun getPolyStack(frame: Int, filePath: String): PolygonStack {
        val key = "$filePath:$frame"
        return cache.getOrPut(key) { PolygonStack() }
    }

    fun setPolyStack(frame: Int, filePath: String, stack: PolygonStack) {
        val key = "$filePath:$frame"
        cache[key] = stack
    }
}
