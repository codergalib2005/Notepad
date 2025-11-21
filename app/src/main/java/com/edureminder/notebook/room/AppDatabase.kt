package com.edureminder.notebook.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.edureminder.notebook.room.diary.Diary
import com.edureminder.notebook.room.diary.DiaryDao
import com.edureminder.notebook.room.folder.Folder
import com.edureminder.notebook.room.folder.FolderDao
import com.edureminder.notebook.room.note.Note
import com.edureminder.notebook.room.note.NoteDao
import com.edureminder.notebook.room.todo.Todo
import com.edureminder.notebook.room.todo.TodoDao
import com.edureminder.notebook.room.utils.Converters

@Database(
    entities = [
        Folder::class,
        Note::class,
        Diary::class,
        Todo::class
    ],
    version = 1, // bump version
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun folderDao(): FolderDao
    abstract fun noteDao(): NoteDao
    abstract fun diaryDao(): DiaryDao
    abstract fun todoDao(): TodoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "draftly_database"
                    )
                        //.fallbackToDestructiveMigration() // use this only if you want to reset data
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}