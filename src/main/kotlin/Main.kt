import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.darkColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.lightColors
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dev.secondsun.tools.rotoscope.data.DATA_STORE_FILE_NAME
import dev.secondsun.tools.rotoscope.data.createDataStore
import dev.secondsun.tools.rotoscope.ui.drawing.DrawingToolbar
import dev.secondsun.tools.rotoscope.ui.drawing.RotoscopeAppModel
import dev.secondsun.tools.rotoscope.ui.startscreen.StartScreen
import dev.secondsun.tools.rotoscope.ui.startscreen.StartScreenViewModel
import dev.secondsun.tools.rotoscope.ui.video.VideoFrame
import dev.secondsun.tools.rotoscope.ui.video.VideoUtil
import dev.secondsun.tools.rotoscope.ui.video.VideoUtilBuilder
import com.jthemedetecor.OsThemeDetector;

@Composable
@Preview
fun App(prefs: DataStore<Preferences>) {

    var util by remember { mutableStateOf(VideoUtil("C:\\Users\\secon\\OneDrive\\Pictures\\Camera Roll\\WIN_20250205_16_30_05_Pro.mp4")) }
    val status by util.status.collectAsState()


    val model = RotoscopeAppModel()

    val detector: OsThemeDetector = OsThemeDetector.getDetector()
    var isDarkMode by remember {mutableStateOf(detector.isDark)}
    detector.registerListener { isDark ->
        if (isDark) {
            isDarkMode = true
        } else {
            isDarkMode = false
        }
    }

    MaterialTheme(colors = if (isDarkMode) darkColors() else lightColors()) {
        Scaffold {
            when (status) {
                VideoUtil.Status.NOT_READY -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        StartScreen(
                            Modifier.fillMaxSize(),
                            StartScreenViewModel(prefs)
                        ) { file ->
                            if (file != null) {
                                util = VideoUtilBuilder.open(file)
                            }
                        }

                    }
                }

                VideoUtil.Status.LOADING -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(imageVector = Icons.Default.Refresh, contentDescription = "")
                    }
                }

                VideoUtil.Status.READY -> {
                    Row {
                        DrawingToolbar(
                            modifier = Modifier.fillMaxHeight().wrapContentWidth()
                                .background(MaterialTheme.colors.primary), model = model
                        )
                        VideoFrame(modifier = Modifier.fillMaxSize().weight(1f), videoUtil = util, model = model)
                    }

                }
            }
        }
    }
}


fun main(): Unit {
    val prefs = createDataStore {
        DATA_STORE_FILE_NAME
    }

    application {

        Window(onCloseRequest = ::exitApplication) {
            App(prefs)
        }
    }
}
