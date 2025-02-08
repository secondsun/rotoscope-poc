import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.secondsun.tools.rotoscope.ui.FPSPicker
import dev.secondsun.tools.rotoscope.ui.FrameScrubber
import dev.secondsun.tools.rotoscope.ui.VideoControlBar
import dev.secondsun.tools.rotoscope.ui.VideoFrame
import kotlinx.coroutines.*
import video.VideoUtil

private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

@Composable
@Preview
fun App() {
    var util = VideoUtil("C:\\Users\\secon\\OneDrive\\Pictures\\Camera Roll\\WIN_20250205_16_30_05_Pro.mp4")
    val status by util.status.collectAsState()


    MaterialTheme {
            when (status) {
                VideoUtil.Status.NOT_READY -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Button(onClick = {
                            ioScope.launch {

                            }
                        })
                        { Text("Load") }
                        FrameScrubber {  }
                    }
                }
                VideoUtil.Status.LOADING -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(imageVector = Icons.Default.Refresh, contentDescription = "")
                    }
                }
                VideoUtil.Status.READY -> {
                    VideoFrame(videoUtil = util)
                }
            }

    }
}




fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
