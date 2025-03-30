package dev.secondsun.tools.rotoscope.ui.drawing

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun ColorPicker(modifier: Modifier = Modifier, model: RotoscopeAppModel) {


    val palette = Palette(model.palette)


    Column(modifier
        .fillMaxHeight()
        .wrapContentWidth()) {

        Row(modifier = Modifier.wrapContentHeight()) {
            for (i in 0..7) {
                ColorBox(palette.colors[i])
            }
        }
        Row(modifier = Modifier.wrapContentHeight()) {
            for (i in 8..14) {
                ColorBox(palette.colors[i])
            }
        }


    }
}

@Composable
fun ColorBox(color: Int) {
    Box(modifier = Modifier.size(16.dp).background(Color(color)).border(1.dp, Color.White))
}