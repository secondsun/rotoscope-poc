package dev.secondsun.tools.rotoscope.ui.startscreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
        title = "Pick a media",
        initialDirectory = "D:\\",
        onResult = {
            ioScope.launch {
                startScreenVM.addRecentFile(it);
                onFilePicked(it)
            }
        }
    )

    LazyColumn {
        items(startScreenVM.recentFiles) { file->
            Text(modifier = Modifier.clickable { onFilePicked(PlatformFile(File(file))) }, text = file)
        }
    }

    Button(onClick = {
        ioScope.launch {
            launcher.launch()
        }
    })
    { Text("Load") }


}