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
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFileSaverLauncher
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.path

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
        type = FileKitType.File(listOf("json")),
        title = "Open Project",
        directory = PlatformFile (System.getProperty("user.home")),
        onResult = { platformFile ->
            platformFile?.let {
                ioScope.launch {
                    model.loadProject(File(it.path ?: it.name))
                }
            }
        }
    )
    
    // File picker launcher for saving projects
    val saveProjectLauncher = rememberFileSaverLauncher (
        //directory = PlatformFile(System.getProperty("user.home")),
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
        type = FileKitType.Video,
        mode = FileKitMode.Single,
        title = "Open Video",
        directory = PlatformFile(System.getProperty("user.home")),
        onResult = { platformFile ->
            platformFile?.let {
                val path = it.path ?: it.name
                model.projectFilePathname(path)
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
                TODO("Create new project dialog")
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
                val file = File(model.project.value.projectFilePathname)

                ioScope.launch { saveProjectLauncher.launch(file.name,file.extension) }
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
