package dev.secondsun.tools.rotoscope.ui.drawing

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.secondsun.tools.rotoscope_poc.generated.resources.Res
import dev.secondsun.tools.rotoscope_poc.generated.resources.ic_polyline
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource

@Composable
@Preview
fun DrawingToolbar(modifier:Modifier = Modifier) {
    Box(modifier) {
        Column() {
            Row() {
                CreatePolygon()
            }
            Row() {
               // SelectColor()
            }
        }
    }
}

@Composable
fun SelectColor() {
    TODO("Not yet implemented")
}

@Composable
fun CreatePolygon() {
    Button(onClick = {}) {
        Icon(painterResource(Res.drawable.ic_polyline), tint = MaterialTheme.colors.onPrimary, contentDescription = "")

    }
}
