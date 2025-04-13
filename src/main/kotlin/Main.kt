import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.google.dynamiccolor.DynamicColor
import com.google.dynamiccolor.DynamicScheme
import com.google.dynamiccolor.MaterialDynamicColors
import com.google.hct.Hct
import com.google.scheme.SchemeTonalSpot
import jthemedetecor.OsThemeDetector
import jthemedetecor.consumers.DarkModeConsumer
import jthemedetecor.consumers.PrimaryColorConsumer
import dev.secondsun.tools.rotoscope.data.DATA_STORE_FILE_NAME
import dev.secondsun.tools.rotoscope.data.createDataStore
import dev.secondsun.tools.rotoscope.ui.drawing.DrawingToolbar
import dev.secondsun.tools.rotoscope.ui.drawing.RotoscopeAppModel
import dev.secondsun.tools.rotoscope.ui.startscreen.StartScreen
import dev.secondsun.tools.rotoscope.ui.startscreen.StartScreenViewModel
import dev.secondsun.tools.rotoscope.ui.video.VideoFrame
import dev.secondsun.tools.rotoscope.ui.video.VideoUtil
import dev.secondsun.tools.rotoscope.ui.video.VideoUtilBuilder


@Composable
@Preview
fun App(prefs: DataStore<Preferences>) {

    var util by remember { mutableStateOf(VideoUtil("")) }
    val status by util.status.collectAsState()


    val model = RotoscopeAppModel()

    val detector: OsThemeDetector = OsThemeDetector.detector
    var isDarkMode by remember {mutableStateOf(detector.isDark)}
    var primaryColor by remember {mutableStateOf(detector.primaryColor)}
    var scheme: DynamicScheme by remember { mutableStateOf( SchemeTonalSpot(Hct.fromInt(primaryColor.rgb), isDarkMode, 0.0))}

    detector.registerListener (DarkModeConsumer( {
        isDark ->
        if (isDark) {
            isDarkMode = true
        } else {
            isDarkMode = false
        }
        scheme = SchemeTonalSpot(Hct.fromInt(primaryColor.rgb), isDarkMode, 0.0)

    }))

    detector.registerListener (PrimaryColorConsumer( {
            color ->
        primaryColor = color
        scheme = SchemeTonalSpot(Hct.fromInt(primaryColor.rgb), isDarkMode, 0.0)
    }))

    MaterialTheme(colors = Colors(
        primary = Color(scheme.primary),
        primaryVariant = Color(scheme.onPrimary),
        secondary = Color(scheme.secondary),
        secondaryVariant = Color(scheme.onSecondary),
        background = Color(scheme.background),
        surface = Color(scheme.surface),
        error = Color(scheme.error),
        onPrimary = Color(scheme.onPrimary),
        onSecondary = Color(scheme.onSecondary),
        onBackground = Color(scheme.onBackground),
        onSurface =  Color(scheme.onSurface),
        onError= Color(scheme.onError),
        isLight= !isDarkMode
    )) {
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
