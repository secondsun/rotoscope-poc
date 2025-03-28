import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.secondsun.tools.rotoscope.ui.drawing.DrawingToolbar
import dev.secondsun.tools.rotoscope.ui.drawing.RotoscopeAppModel
import dev.secondsun.tools.rotoscope.ui.video.VideoFrame
import kotlinx.coroutines.*
import dev.secondsun.tools.rotoscope.ui.video.VideoUtil
import dev.secondsun.tools.rotoscope.ui.video.VideoUtilBuilder
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import java.io.File

private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

@Composable
@Preview
fun App() {
    var util by remember {mutableStateOf(VideoUtil("C:\\Users\\secon\\OneDrive\\Pictures\\Camera Roll\\WIN_20250205_16_30_05_Pro.mp4"))}
    val status by util.status.collectAsState()

    val launcher = rememberFilePickerLauncher(
        type = PickerType.Video,
        mode = PickerMode.Single,
        title = "Pick a media",
        initialDirectory = "D:\\"
    ) { file ->
        util = VideoUtilBuilder.open(file!!)
    }
    val model = RotoscopeAppModel()

    MaterialTheme {
            when (status) {
                VideoUtil.Status.NOT_READY -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Button(onClick = {
                            ioScope.launch {
                                launcher.launch()

                            }
                        })
                        { Text("Load") }
                    }
                }
                VideoUtil.Status.LOADING -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(imageVector = Icons.Default.Refresh, contentDescription = "")
                    }
                }
                VideoUtil.Status.READY -> {
                    Row {
                        DrawingToolbar(modifier = Modifier.fillMaxHeight().wrapContentWidth().background(MaterialTheme.colors.primary), model=model)
                        VideoFrame(modifier = Modifier.fillMaxSize().weight(1f),videoUtil = util, model=model)
                    }

                }
            }

    }
}




fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
