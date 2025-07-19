package com.dausnotes.app.data.database

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dausnotes.app.data.model.Note
import com.dausnotes.app.data.model.PomodoroSession

@Database(
    entities = [Note::class, PomodoroSession::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun notesDao(): NotesDao
    abstract fun pomodoroDao(): PomodoroDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dausnotes_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
        
        // Example migration for future use
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add migration logic here when needed
                // Example: database.execSQL("ALTER TABLE notes ADD COLUMN new_column TEXT")
            }
        }
    }
}

class Converters {
    @TypeConverter
    fun fromSessionType(sessionType: PomodoroSession.SessionType): String {
        return sessionType.name
    }
    
    @TypeConverter
    fun toSessionType(sessionType: String): PomodoroSession.SessionType {
        return PomodoroSession.SessionType.valueOf(sessionType)
    }
}