package com.edureminder.easynotes.di

import android.content.Context
import com.edureminder.easynotes.room.AppDatabase
import com.edureminder.easynotes.room.folder.FolderDao
import com.edureminder.easynotes.room.folder.FolderRepository
import com.edureminder.easynotes.room.note.NoteDao
import com.edureminder.easynotes.room.note.NoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideFolderDao(db: AppDatabase): FolderDao = db.folderDao()

    @Provides
    fun provideFolderRepository(folderDao: FolderDao): FolderRepository =
        FolderRepository(folderDao)

    @Provides
    fun provideNoteDao(db: AppDatabase): NoteDao = db.noteDao()

    @Provides
    fun provideNoteRepository(noteDao: NoteDao): NoteRepository =
        NoteRepository(noteDao)


}