package dev.secondsun.tools.rotoscope

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import java.awt.image.BufferedImage

class Scene {
    var _palette = SnesPalette()

    var palette: SnesPalette
            get() =  _palette
            set(value) {_palette = value}

    val _images = mutableStateListOf<BufferedImage>()
    val images :List<BufferedImage> get() = _images



}