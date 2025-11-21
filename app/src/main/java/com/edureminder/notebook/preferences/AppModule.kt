package com.edureminder.notebook.preferences

import android.content.Context
import com.edureminder.notebook.preferences.notes.NotesPreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNotesPreferencesManager(@ApplicationContext context: Context): NotesPreferencesManager {
        return NotesPreferencesManager(context)
    }
}