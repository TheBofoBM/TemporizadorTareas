package com.example.temporizadorapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.temporizadorapp.data.local.dao.TaskDao
import com.example.temporizadorapp.data.local.dao.TemplateDao
import com.example.temporizadorapp.data.local.entities.TaskEntity
import com.example.temporizadorapp.data.local.entities.TemplateEntity

@Database(
    entities = [TaskEntity::class, TemplateEntity::class],
    version = 2, // Incrementar versión por el cambio en TaskEntity
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun templateDao(): TemplateDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "temporizador_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}