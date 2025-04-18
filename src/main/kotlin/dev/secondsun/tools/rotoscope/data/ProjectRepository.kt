package dev.secondsun.tools.rotoscope.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.secondsun.tools.rotoscope.ui.vo.PolyStackData
import dev.secondsun.tools.rotoscope.ui.vo.PolygonData
import dev.secondsun.tools.rotoscope.ui.vo.Project
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

/**
 * Repository for managing project data
 */
class ProjectRepository(private val dataStore: DataStore<Preferences>) {

    companion object {
        val RECENT_PROJECTS_KEY = stringPreferencesKey("ProjectRepository.RecentProjects")
        private val json = Json { 
            prettyPrint = true 
            ignoreUnknownKeys = true
        }
    }

    /**
     * Saves a project to a file
     */
    suspend fun saveProject(project: Project, file: File): Boolean = withContext(Dispatchers.IO) {
        try {

            val preparedProject = project.prepareToSave()

            // Create parent directories if they don't exist
            file.parentFile?.mkdirs()
            
            // Serialize the project to JSON and write to file
            val projectJson = json.encodeToString(preparedProject)
            file.writeText(projectJson)
            
            // Add to recent projects
            addRecentProject(file.absolutePath)
            
            return@withContext true
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }

    private fun Project.prepareToSave():Project {
        return Project(
            this.name,
            this.filePath,
            framePolystacks = this.framePolystacks.mapValues { PolyStackData(polygons = it.value.polygons.map{ PolygonData(it.color,it.key,it.points.toList())}) }.toMutableMap()
        )

    }

    /**
     * Loads a project from a file
     */
    suspend fun loadProject(file: File): Project? = withContext(Dispatchers.IO) {
        try {
            val projectJson = file.readText()
            val project = json.decodeFromString<Project>(projectJson)
            
            // Add to recent projects
            addRecentProject(file.absolutePath)
            
            return@withContext project
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }

    /**
     * Gets the list of recent projects
     */
    suspend fun getRecentProjects(): List<String> {
        return dataStore.data.map { preferences ->
            val recentProjects = preferences[RECENT_PROJECTS_KEY] ?: ""
            if (recentProjects.isEmpty()) emptyList() else recentProjects.split(File.pathSeparator)
        }.first()
    }

    /**
     * Adds a project to the recent projects list
     */
    private suspend fun addRecentProject(projectPath: String) {
        dataStore.edit { preferences ->
            val recentProjects = preferences[RECENT_PROJECTS_KEY] ?: ""
            val projectsList = if (recentProjects.isEmpty()) 
                mutableListOf() 
            else 
                recentProjects.split(File.pathSeparator).toMutableList()
            
            // Remove if already exists (to move to top)
            projectsList.remove(projectPath)
            
            // Add to the beginning
            projectsList.add(0, projectPath)
            
            // Limit to 10 recent projects
            val limitedList = projectsList.take(10)
            
            preferences[RECENT_PROJECTS_KEY] = limitedList.joinToString(File.pathSeparator)
        }
    }
}
