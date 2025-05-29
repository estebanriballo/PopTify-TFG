import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchHistoryPreferences(private val context: Context) {
    companion object {
        private val Context.dataStore by preferencesDataStore(name = "search_history")
        private val SEARCH_HISTORY = stringSetPreferencesKey("search_history_set")
        private const val MAX_HISTORY_ITEMS = 15
    }

    val searchHistory: Flow<List<String>> = context.dataStore.data
        .map { preferences ->
            preferences[SEARCH_HISTORY]?.toList()?.reversed() ?: emptyList()
        }

    suspend fun addSearchQuery(query: String) {
        context.dataStore.edit { preferences ->
            val currentSet = preferences[SEARCH_HISTORY]?.toMutableSet() ?: mutableSetOf()
            // Eliminar si ya existe para evitar duplicados
            currentSet.remove(query)
            // Agregar al inicio
            currentSet.add(query)
            // Mantener solo los últimos 15 elementos
            if (currentSet.size > MAX_HISTORY_ITEMS) {
                val oldestItems = currentSet.toList().take(currentSet.size - MAX_HISTORY_ITEMS)
                currentSet.removeAll(oldestItems)
            }
            preferences[SEARCH_HISTORY] = currentSet
        }
    }

    suspend fun clearSearchHistory() {
        context.dataStore.edit { preferences ->
            preferences.remove(SEARCH_HISTORY)
        }
    }
}