package dev.secondsun.tools.rotoscope.ui.startscreen

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.io.File

class StartScreenViewModel(val prefs: DataStore<Preferences>) : ViewModel() {

    private val _recentFiles = mutableStateListOf<String>()
    val recentFiles : SnapshotStateList<String> = _recentFiles

    init {
        runBlocking {  _recentFiles.addAll(getRecentFiles()) }
    }

    companion object {
        val RECENT_FILES_KEY = stringPreferencesKey("StartScreenViewModel.RecentFiles")
    }

    suspend fun getRecentFiles():Set<String> {
        return prefs.data.map { preferences ->
            val recentFiles :String = preferences[RECENT_FILES_KEY].toString()
            recentFiles.split(File.pathSeparator).toMutableSet()
        }.first()
    }

    suspend fun addRecentFile(file: PlatformFile?) {
        if (file != null) {
            prefs.updateData { preferences ->
                val mutPrefs = preferences.toMutablePreferences()
                val recentFiles :String = preferences[RECENT_FILES_KEY].toString()

                val set = recentFiles.split(File.pathSeparator).toMutableSet()
                set.add(file.path?:file.name)

                mutPrefs[RECENT_FILES_KEY] = set.joinToString(File.pathSeparator)
                _recentFiles.clear()
                _recentFiles.addAll(set)
                mutPrefs.toPreferences()
             }
        }
    }





}
