package com.edureminder.notebook.di

import android.content.Context
import com.edureminder.notebook.room.AppDatabase
import com.edureminder.notebook.room.diary.DiaryDao
import com.edureminder.notebook.room.diary.DiaryRepository
import com.edureminder.notebook.room.folder.FolderDao
import com.edureminder.notebook.room.folder.FolderRepository
import com.edureminder.notebook.room.note.NoteDao
import com.edureminder.notebook.room.note.NoteRepository
import com.edureminder.notebook.room.todo.TodoDao
import com.edureminder.notebook.room.todo.TodoRepository
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

    @Provides
    fun provideDiaryDao(db: AppDatabase): DiaryDao = db.diaryDao()

    @Provides
    fun provideDiaryRepository(diaryDao: DiaryDao): DiaryRepository =
        DiaryRepository(diaryDao)

    @Provides
    fun provideTodoDao(db: AppDatabase): TodoDao = db.todoDao()

    @Provides
    fun provideTodoRepository(todoDao: TodoDao): TodoRepository =
        TodoRepository(todoDao)

}