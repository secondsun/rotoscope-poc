import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.google.dynamiccolor.DynamicScheme
import com.google.hct.Hct
import com.google.scheme.SchemeTonalSpot
import jthemedetecor.OsThemeDetector
import jthemedetecor.consumers.DarkModeConsumer
import jthemedetecor.consumers.PrimaryColorConsumer
import dev.secondsun.tools.rotoscope.data.DATA_STORE_FILE_NAME
import dev.secondsun.tools.rotoscope.data.ProjectRepository
import dev.secondsun.tools.rotoscope.data.TempDataSource
import dev.secondsun.tools.rotoscope.data.createDataStore
import dev.secondsun.tools.rotoscope.ui.ProjectMenu
import dev.secondsun.tools.rotoscope.ui.drawing.DrawingToolbar
import dev.secondsun.tools.rotoscope.ui.drawing.RotoscopeAppModel
import dev.secondsun.tools.rotoscope.ui.startscreen.StartScreen
import dev.secondsun.tools.rotoscope.ui.startscreen.ProjectStartScreenViewModel
import dev.secondsun.tools.rotoscope.ui.video.VideoFrame
import dev.secondsun.tools.rotoscope.ui.video.VideoUtil
import dev.secondsun.tools.rotoscope.ui.video.VideoUtilBuilder
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.extension
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.path
import kotlinx.coroutines.launch
import java.io.File


@Composable
@Preview
fun App(prefs: DataStore<Preferences>) {
    println("App composition")
    // Create repositories and models
    val projectRepository = remember { ProjectRepository(prefs) }
    val scope = rememberCoroutineScope()
    val model by remember { mutableStateOf(RotoscopeAppModel(
        dataSource = TempDataSource(),
        projectRepository = projectRepository
    )) }
    
    var util by remember { mutableStateOf(VideoUtil("")) }
    val status by util.status.collectAsState()
    
    // UI theme detection
    val detector: OsThemeDetector by remember { mutableStateOf(OsThemeDetector.detector) }
    var isDarkMode by remember { mutableStateOf(detector.isDark) }
    var primaryColor by remember { mutableStateOf(detector.primaryColor) }
    var scheme: DynamicScheme by remember(key1 = { (if (isDarkMode) 0 else 1) * 3 + primaryColor.rgb }) { 
        mutableStateOf(SchemeTonalSpot(Hct.fromInt(primaryColor.rgb), isDarkMode, 0.0))
    }
    
    // Monitor theme changes
    detector.registerListener(scope, DarkModeConsumer({ isDark ->
        if (isDark != isDarkMode) {
            isDarkMode = isDark
            scheme = SchemeTonalSpot(Hct.fromInt(primaryColor.rgb), isDarkMode, 0.0)
        }
    }))

    detector.registerListener(scope, PrimaryColorConsumer({ color ->
        if (primaryColor != color) {
            primaryColor = color
            scheme = SchemeTonalSpot(Hct.fromInt(primaryColor.rgb), isDarkMode, 0.0)
        }
    }))

    // Project state
    val projectState by model.project.collectAsState()
    
    // Update video when project changes
    LaunchedEffect(key1 = projectState.filePath) {
        if (projectState.filePath.isNotEmpty()) {
            util = VideoUtilBuilder.open(PlatformFile(File(projectState.filePath)))
        }
    }

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
        onSurface = Color(scheme.onSurface),
        onError = Color(scheme.onError),
        isLight = !isDarkMode
    )) {
        Scaffold(
            topBar = {
                if (status == VideoUtil.Status.READY) {
                    TopAppBar(
                        title = { Text(projectState.name) },
                        actions = {
                            ProjectMenu(model = model)
                        },
                        backgroundColor = MaterialTheme.colors.primary
                    )
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                when (status) {
                    VideoUtil.Status.NOT_READY -> {
                        // Replace StartScreenViewModel with a new implementation that uses ProjectRepository
                        val startViewModel = remember {
                            ProjectStartScreenViewModel(projectRepository)
                        }

                        StartScreen(
                            Modifier.fillMaxSize(),
                            startViewModel
                        ) { file ->
                            file?.let {
                                when(it.extension) {
                                    "mp4", "mkv", "avi" -> {
                                        // Update model with the file path
                                        model.filePath(it.path ?: it.name)

                                        // Open the video
                                        util = VideoUtilBuilder.open(it)
                                    }
                                    "json" -> {
                                        scope.launch {
                                            model.loadProject(it.file) }

                                    }
                                }

                            }
                        }
                    }

                    VideoUtil.Status.LOADING -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                            Image(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Loading",
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    VideoUtil.Status.READY -> {
                        Row {
                            DrawingToolbar(
                                modifier = Modifier.fillMaxHeight().wrapContentWidth()
                                    .background(MaterialTheme.colors.primary),
                                model = model
                            )
                            VideoFrame(
                                modifier = Modifier.fillMaxSize().weight(1f),
                                videoUtil = util,
                                model = model
                            )
                        }
                    }
                }
            }
        }
    }
}

fun main() {
    val prefs = createDataStore {
        DATA_STORE_FILE_NAME
    }

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Rotoscope Tool"
        ) {
            App(prefs)
        }
    }
}
