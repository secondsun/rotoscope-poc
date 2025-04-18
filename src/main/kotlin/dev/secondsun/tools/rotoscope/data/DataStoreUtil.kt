package dev.secondsun.tools.rotoscope.data

import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okio.Path.Companion.toOkioPath
import java.io.File

// File name for the data store
const val DATA_STORE_FILE_NAME = "rotoscope_preferences.preferences_pb"

/**
 * Create a preferences DataStore
 */
fun createDataStore(
    produceFileName: () -> String = { DATA_STORE_FILE_NAME },
    corruptionHandler: ReplaceFileCorruptionHandler<Preferences>? = null,
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(
        corruptionHandler = corruptionHandler,
        scope = scope,
        produceFile = { File(System.getProperty("user.home"), produceFileName()).toOkioPath() }
    )
}
