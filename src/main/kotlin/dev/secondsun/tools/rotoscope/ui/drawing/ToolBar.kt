package dev.secondsun.tools.rotoscope.ui.drawing

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.secondsun.tools.rotoscope_poc.generated.resources.Res
import dev.secondsun.tools.rotoscope_poc.generated.resources.ic_color_picker
import dev.secondsun.tools.rotoscope_poc.generated.resources.ic_polyline
import org.jetbrains.compose.resources.painterResource

@Composable
@Preview
fun Preview() {
    MaterialTheme {
        DrawingToolbar(Modifier.fillMaxHeight().wrapContentWidth(), RotoscopeAppModel())
    }
}

@Composable
fun DrawingToolbar(modifier: Modifier = Modifier, model: RotoscopeAppModel) {
    val currentTool by model.tool
    println("DrawingToolbar Composition")
    Box(modifier) {

        Row(modifier = Modifier.wrapContentWidth().fillMaxHeight()) {
            Column(modifier = Modifier.width(120.dp).fillMaxHeight()) {
                Row {
                    CreatePolygon(onClick = {model.setTool(Tools.PolygonStackTool)})
                }
                Row {
                     SelectColor(onClick = {model.setTool(Tools.ColorPickerTool)})
                }
            }

            Column(modifier = Modifier.wrapContentWidth().fillMaxHeight()) {
                when (currentTool) {
                    Tools.PolygonStackTool -> PolyStackTool(model = model)
                    Tools.ColorPickerTool -> ColorPicker(model = model)
                }

            }
        }
    }
}


@Composable
fun SelectColor(onClick: () -> Unit) {
    Button(onClick = onClick) {
        Icon(painterResource(Res.drawable.ic_color_picker), tint = MaterialTheme.colors.onPrimary, contentDescription = "")
    }
}

@Composable
fun CreatePolygon(onClick: () -> Unit) {
    Button(onClick = onClick) {
        Icon( painterResource(Res.drawable.ic_polyline), tint = MaterialTheme.colors.onPrimary, contentDescription = "")
    }
}
