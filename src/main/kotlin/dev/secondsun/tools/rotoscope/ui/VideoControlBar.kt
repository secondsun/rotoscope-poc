package dev.secondsun.tools.rotoscope.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import video.VideoUtil

@Composable

fun VideoControlBar(modifier:Modifier = Modifier, videoUtil: VideoUtil = VideoUtil.Stub) {
    Row(modifier = modifier.fillMaxWidth().wrapContentHeight()) {
        Box(contentAlignment = Alignment.TopCenter) {
            IconButton(onClick = {TODO()},
                content = { Icon(imageVector = Icons.Outlined.PlayArrow, contentDescription = "Play") })
        }
    }
}

@Composable
@Preview
fun previewVideoControlBar() {
    MaterialTheme {
        VideoControlBar()
    }
}