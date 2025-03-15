package dev.secondsun.tools.rotoscope.ui.vo

data class Scene(val background: PolygonStack = PolygonStack(), val keyframes : List<PolygonStack> = mutableListOf())