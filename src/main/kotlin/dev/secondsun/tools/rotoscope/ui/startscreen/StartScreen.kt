package dev.secondsun.tools.rotoscope.ui.startscreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.runtime.Composable
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
    startScreenVM: StartScreenViewModel,
    onFilePicked: (PlatformFile?) -> Unit
) {
    val launcher = rememberFilePickerLauncher(
        type = PickerType.Video,
        mode = PickerMode.Single,
        title = "Pick a media file",
        initialDirectory = System.getProperty("user.home"), // More user-friendly default
        onResult = { platformFile ->
            platformFile?.let {
                ioScope.launch {
                    startScreenVM.addRecentFile(it); // Store path, not PlatformFile
                    onFilePicked(it)
                }
            }
        }
    )

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            "Recent Projects",
            fontSize = 24.sp,
            style = MaterialTheme.typography.h6
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (startScreenVM.recentFiles.isEmpty()) {
            Text("No recent projects found.")
            Spacer(modifier = Modifier.height(8.dp))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f) // Fill available vertical space
            ) {
                items(startScreenVM.recentFiles) { filePath ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                            .clickable { onFilePicked(PlatformFile(File(filePath))) },
                        elevation = 2.dp
                    ) {
                        PaddingValues(all = 8.dp)
                        Text(text = filePath)

                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = {
                ioScope.launch {
                    launcher.launch()
                }
            },
            modifier = Modifier.fillMaxWidth(0.6f), // Control button width
            elevation = ButtonDefaults.elevation()
        ) {
            Icon(Icons.Filled.FolderOpen, contentDescription = "Open File")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Open File")
        }
    }
}