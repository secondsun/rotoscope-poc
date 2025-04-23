import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import dev.secondsun.tools.rotoscope.ui.vo.Project
import java.io.File

@Composable
fun AppTitle(project: Project) {
    val modified by project.modified
    val filename = if (project.projectFilePathname.isNotEmpty()) {
        File(project.projectFilePathname).name
    } else {
        project.name
    }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            filename + (if (modified) "*" else ""),
            style = MaterialTheme.typography.h6
        )
    }
}
