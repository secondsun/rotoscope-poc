package dev.secondsun.tools.rotoscope.ui.startscreen

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import dev.secondsun.tools.rotoscope.data.ProjectRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File

/**
 * ViewModel for the start screen that uses ProjectRepository
 */
class ProjectStartScreenViewModel(private val projectRepository: ProjectRepository) {

    private val _recentFiles = mutableStateListOf<String>()
    val recentFiles: SnapshotStateList<String> = _recentFiles

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        // Load recent projects on initialization
        scope.launch {
            val projects = withContext(Dispatchers.IO) {
                projectRepository.getRecentProjects()
            }
            _recentFiles.clear()
            _recentFiles.addAll(projects)


        }
    }

    /**
     * Loads the recent projects from the repository
     */
    fun refreshRecentProjects() {
        scope.launch {
            val projects = withContext(Dispatchers.IO) {
                projectRepository.getRecentProjects()
            }
            _recentFiles.clear()
            _recentFiles.addAll(projects)
        }
    }
}
