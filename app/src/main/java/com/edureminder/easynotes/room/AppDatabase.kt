package com.edureminder.easynotes.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.edureminder.easynotes.room.folder.Folder
import com.edureminder.easynotes.room.folder.FolderDao
import com.edureminder.easynotes.room.note.Note
import com.edureminder.easynotes.room.note.NoteDao
import com.edureminder.easynotes.room.utils.Converters

@Database(
    entities = [
        Folder::class,
        Note::class
    ],
    version = 2, // bump version
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun folderDao(): FolderDao
    abstract fun noteDao(): NoteDao

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