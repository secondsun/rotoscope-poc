package dev.secondsun.tools.rotoscope.ui.startscreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun StartScreen(
    modifier: Modifier = Modifier,
    viewModel: ProjectStartScreenViewModel,
    onFilePicked: (PlatformFile?) -> Unit
) {
    // File picker for video files
    val videoLauncher = rememberFilePickerLauncher(
        type = PickerType.Video,
        mode = PickerMode.Single,
        title = "Open Video File",
        initialDirectory = System.getProperty("user.home"),
        onResult = { platformFile ->
            onFilePicked(platformFile)
        }
    )
    
    // File picker for project files
    val projectLauncher = rememberFilePickerLauncher(
        type = PickerType.File(listOf("json")),
        mode = PickerMode.Single,
        title = "Open Project File",
        initialDirectory = System.getProperty("user.home"),
        onResult = { platformFile ->
            onFilePicked(platformFile)
        }
    )

    LaunchedEffect(false) {
        viewModel.refreshRecentProjects()
    }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            "Rotoscope Tool",
            fontSize = 28.sp,
            style = MaterialTheme.typography.h4
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Actions section
        Card(elevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Start a New Project",
                    fontSize = 20.sp,
                    style = MaterialTheme.typography.h6
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            ioScope.launch { videoLauncher.launch() }
                        },
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    ) {
                        Icon(Icons.Filled.VideoLibrary, contentDescription = "Open Video")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Open Video")
                    }
                    
                    Button(
                        onClick = {
                            ioScope.launch { projectLauncher.launch() }
                        },
                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                    ) {
                        Icon(Icons.Filled.FolderOpen, contentDescription = "Open Project")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Open Project")
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Recent projects section
        Text(
            "Recent Projects",
            fontSize = 20.sp,
            style = MaterialTheme.typography.h6
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (viewModel.recentFiles.isEmpty()) {
            Card(
                elevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No recent projects found")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                items(viewModel.recentFiles) { filePath ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { 
                                val file = File(filePath)
                                onFilePicked(PlatformFile(file)) 
                            },
                        elevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val isProject = filePath.endsWith(".json")
                            Icon(
                                imageVector = if (isProject) Icons.Default.FolderOpen else Icons.Default.VideoLibrary,
                                contentDescription = null,
                                tint = MaterialTheme.colors.primary
                            )
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Column {
                                Text(
                                    text = File(filePath).name,
                                    style = MaterialTheme.typography.subtitle1
                                )
                                Text(
                                    text = filePath,
                                    style = MaterialTheme.typography.caption
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}