package com.example.temporizadorapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.temporizadorapp.data.local.entities.TemplateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TemplateDao {
    // Obtener todas las plantillas guardadas
    @Query("SELECT * FROM templates")
    fun getAllTemplates(): Flow<List<TemplateEntity>>

    // Obtener una plantilla específica por su ID
    @Query("SELECT * FROM templates WHERE id = :templateId")
    suspend fun getTemplateById(templateId: Int): TemplateEntity?

    // Guardar una nueva plantilla
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: TemplateEntity)

    // Borrar una plantilla
    @Delete
    suspend fun deleteTemplate(template: TemplateEntity)
}