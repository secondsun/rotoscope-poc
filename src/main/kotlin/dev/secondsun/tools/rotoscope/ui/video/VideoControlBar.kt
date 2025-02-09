package dev.secondsun.tools.rotoscope.ui.video

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun VideoControlBar(modifier: Modifier = Modifier, videoUtil: VideoUtil) {

    val fpsPickerState by mutableStateOf(FPSPickerState.rememberFpsPickerState())

    Row(modifier = modifier.fillMaxWidth().wrapContentHeight()) {
        //Text("FPS:")
        //FPSPicker(fpsPickerState)
        FrameScrubber {
            videoUtil.seek(it)
        }
    }
}

@Composable
fun FrameScrubber(sliderPosition: MutableFloatState = remember { mutableFloatStateOf(0f) },onValueChange:(Float)->Unit) {

    val colors :SliderColors = object : SliderColors {
        @Composable
        override fun thumbColor(enabled: Boolean): State<Color> {
            return mutableStateOf(MaterialTheme.colors.onPrimary)
        }

        @Composable
        override fun tickColor(enabled: Boolean, active: Boolean): State<Color> {
            return mutableStateOf(MaterialTheme.colors.onPrimary)
        }

        @Composable
        override fun trackColor(enabled: Boolean, active: Boolean): State<Color> {
            return mutableStateOf(MaterialTheme.colors.onPrimary)
        }

    }
    Slider(
        value = sliderPosition.value,
        colors =  colors,
        onValueChange = { sliderPosition.value = it;onValueChange(it) }
    )
}

@Composable
fun FPSPicker(state: FPSPickerState = FPSPickerState.rememberFpsPickerState(), onSelected: (Int) -> Unit = {}) {

    var expanded by state.expanded
    val items = FPSPickerState.fpsItems
    var selectedIndex by state.selectedIndex

    Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
        Text(
            items[selectedIndex].toString(),
            modifier = Modifier.fillMaxWidth().clickable(onClick = { expanded = true }).background(
                MaterialTheme.colors.onPrimary
            )
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            items.forEachIndexed { index, s ->
                DropdownMenuItem(onClick = {
                    selectedIndex = index
                    expanded = false
                    onSelected(items[index])
                }) {

                    Text(text = s.toString())
                }

            }

        }
    }
}

@Composable
@Preview
fun previewVideoControlBar() {
    MaterialTheme {
        FPSPicker()
    }
}


class FPSPickerState(val expanded: MutableState<Boolean>, val selectedIndex: MutableIntState) {

    val currentFps: Int
        get() = fpsItems[selectedIndex.value]

    companion object {
        // default to 30fps
        val defaultFpsIndex = 5

        //vailable FPS. Magic numbers pulled from a hat of numbers that divide 60
        val fpsItems = listOf(5, 10, 12, 15, 20, 30)

        @Composable
        fun rememberFpsPickerState(): FPSPickerState {
            val expanded: MutableState<Boolean> = remember { mutableStateOf(false) }
            val selectedIndex: MutableIntState = remember { mutableIntStateOf(defaultFpsIndex) }

            return remember(expanded, selectedIndex) {
                FPSPickerState(expanded, selectedIndex)
            }

        }

    }


}