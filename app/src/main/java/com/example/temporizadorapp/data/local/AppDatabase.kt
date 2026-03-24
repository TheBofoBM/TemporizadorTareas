package com.example.temporizadorapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.temporizadorapp.data.local.dao.TaskDao
import com.example.temporizadorapp.data.local.dao.TemplateDao
import com.example.temporizadorapp.data.local.entities.TaskEntity
import com.example.temporizadorapp.data.local.entities.TemplateEntity

// Aquí le decimos a Room cuáles son nuestras tablas (entities)
@Database(
    entities = [TaskEntity::class, TemplateEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Declaramos nuestros DAOs
    abstract fun taskDao(): TaskDao
    abstract fun templateDao(): TemplateDao

    // Creamos un Singleton para asegurarnos de que solo exista una instancia de la base de datos
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "temporizador_db" // Este será el nombre del archivo de la base de datos en el celular
                )
                    .fallbackToDestructiveMigration() // Si cambiamos las tablas en el futuro, borra y recrea
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}