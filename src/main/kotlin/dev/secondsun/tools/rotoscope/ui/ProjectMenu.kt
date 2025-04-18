package dev.secondsun.tools.rotoscope.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.secondsun.tools.rotoscope.ui.drawing.RotoscopeAppModel
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File

private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

@Composable
fun ProjectMenu(model: RotoscopeAppModel) {
    var showMenu by remember { mutableStateOf(false) }
    val projectState by model.project.collectAsState()
    val hasUnsavedChanges = projectState.modified.value
    
    // File picker launcher for opening projects
    val openProjectLauncher = rememberFilePickerLauncher(
        type = PickerType.File(listOf("json")),
        mode = PickerMode.Single,
        title = "Open Project",
        initialDirectory = System.getProperty("user.home"),
        onResult = { platformFile ->
            platformFile?.let {
                ioScope.launch {
                    model.loadProject(File(it.path ?: it.name))
                }
            }
        }
    )
    
    // File picker launcher for saving projects
    val saveProjectLauncher = rememberFilePickerLauncher(
        type = PickerType.File(listOf("json")),
        mode = PickerMode.Single,
        title = "Save Project",
        initialDirectory = System.getProperty("user.home"),
        onResult = { platformFile ->
            platformFile?.let {
                ioScope.launch {
                    val file = File(it.path ?: it.name)
                    // Ensure the file has a .json extension
                    val finalFile = if (!file.name.endsWith(".json")) {
                        File(file.parentFile, "${file.name}.json")
                    } else {
                        file
                    }
                    model.saveProject(finalFile)
                }
            }
        }
    )
    
    // File picker launcher for opening videos
    val openVideoLauncher = rememberFilePickerLauncher(
        type = PickerType.Video,
        mode = PickerMode.Single,
        title = "Open Video",
        initialDirectory = System.getProperty("user.home"),
        onResult = { platformFile ->
            platformFile?.let {
                val path = it.path ?: it.name
                model.filePath(path)
            }
        }
    )

    IconButton(onClick = { showMenu = true }) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Project Menu",
            tint = MaterialTheme.colors.onPrimary
        )
    }
    
    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = { showMenu = false },
        modifier = Modifier.width(200.dp)
    ) {
        Column {
            DropdownMenuItem(onClick = {
                model.newProject()
                showMenu = false
            }) {
                Icon(Icons.Default.Add, contentDescription = "New Project")
                Text("New Project")
            }
            
            DropdownMenuItem(onClick = {
                ioScope.launch { openProjectLauncher.launch() }
                showMenu = false
            }) {
                Icon(Icons.Default.FolderOpen, contentDescription = "Open Project")
                Text("Open Project")
            }
            
            DropdownMenuItem(onClick = {
                ioScope.launch { openVideoLauncher.launch() }
                showMenu = false
            }) {
                Icon(Icons.Default.VideoLibrary, contentDescription = "Open Video")
                Text("Open Video")
            }
            
            DropdownMenuItem(onClick = {
                ioScope.launch { saveProjectLauncher.launch() }
                showMenu = false
            }) {
                Icon(
                    if (hasUnsavedChanges) Icons.Default.Save else Icons.Default.SaveAlt,
                    contentDescription = "Save Project"
                )
                Text("Save Project")
            }
        }
    }
}
