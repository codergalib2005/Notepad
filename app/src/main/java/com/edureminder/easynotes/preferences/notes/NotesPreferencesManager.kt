package com.edureminder.easynotes.preferences.notes

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.edureminder.easynotes.room.note.Type
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("app_preferences")

@Singleton
class NotesPreferencesManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        private val VIEW_TYPE_KEY = stringPreferencesKey("view_type")
        private val SORT_ORDER_KEY = stringPreferencesKey("sort_order")
        private val NOTE_TYPE_KEY = stringPreferencesKey("note_type")
    }

    val viewTypeFlow: Flow<ViewType> = dataStore.data.map { preferences ->
        ViewType.fromString(preferences[VIEW_TYPE_KEY] ?: ViewType.LIST.name)
    }

    val sortOrderFlow: Flow<SortOrder> = dataStore.data.map { preferences ->
        SortOrder.fromString(preferences[SORT_ORDER_KEY] ?: SortOrder.DATE_NEWEST_FIRST.name)
    }

    val noteTypeFlow: Flow<Type?> = dataStore.data.map { preferences ->
        preferences[NOTE_TYPE_KEY]?.let { Type.fromString(it) }
    }

    suspend fun setViewType(viewType: ViewType) {
        dataStore.edit { preferences ->
            preferences[VIEW_TYPE_KEY] = viewType.name
        }
    }

    suspend fun setSortOrder(sortOrder: SortOrder) {
        dataStore.edit { preferences ->
            preferences[SORT_ORDER_KEY] = sortOrder.name
        }
    }

    suspend fun setNoteType(type: Type?) {
        dataStore.edit { preferences ->
            if (type != null) {
                preferences[NOTE_TYPE_KEY] = type.name
            } else {
                preferences.remove(NOTE_TYPE_KEY)
            }
        }
    }

    suspend fun clearNoteType() {
        dataStore.edit { preferences ->
            preferences.remove(NOTE_TYPE_KEY)
        }
    }
}