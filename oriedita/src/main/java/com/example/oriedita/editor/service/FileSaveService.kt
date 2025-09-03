package com.example.oriedita.editor.service

import android.content.Context
import com.example.oriedita.editor.export.ExportData
import com.example.oriedita.editor.export.ExportManager
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import java.io.Serializable

/**
 * Сервис сохранения файлов для Android
 */
class FileSaveService(private val context: Context) {
    
    private val exportManager = ExportManager()
    
    /**
     * Сохранить проект в формате .ori
     */
    fun saveProject(projectData: ProjectData, filename: String): Boolean {
        return try {
            val file = File(context.getExternalFilesDir(null), filename)
            FileOutputStream(file).use { fos ->
                ObjectOutputStream(fos).use { oos ->
                    oos.writeObject(projectData)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Загрузить проект из формата .ori
     */
    fun loadProject(filename: String): ProjectData? {
        return try {
            val file = File(context.getExternalFilesDir(null), filename)
            if (file.exists()) {
                file.inputStream().use { fis ->
                    java.io.ObjectInputStream(fis).use { ois ->
                        ois.readObject() as ProjectData
                    }
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Экспортировать в различные форматы
     */
    fun exportToFormat(data: ExportData, filename: String): Boolean {
        val file = File(context.getExternalFilesDir(null), filename)
        return exportManager.export(data, file)
    }
    
    /**
     * Получить список поддерживаемых форматов экспорта
     */
    fun getSupportedExportFormats(): List<String> {
        return exportManager.getSupportedFormats()
    }
    
    /**
     * Получить список всех файлов проектов
     */
    fun getProjectFiles(): List<File> {
        val projectDir = context.getExternalFilesDir(null)
        return projectDir?.listFiles { file ->
            file.name.endsWith(".ori")
        }?.toList() ?: emptyList()
    }
    
    /**
     * Удалить файл проекта
     */
    fun deleteProject(filename: String): Boolean {
        return try {
            val file = File(context.getExternalFilesDir(null), filename)
            file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

/**
 * Данные проекта
 */
data class ProjectData(
    val name: String,
    val lines: List<LineData>,
    val points: List<PointData>,
    val circles: List<CircleData>,
    val texts: List<TextData>,
    val settings: Map<String, Any>,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable

data class LineData(
    val x1: Double,
    val y1: Double,
    val x2: Double,
    val y2: Double,
    val color: String,
    val lineStyle: String
) : Serializable

data class PointData(
    val x: Double,
    val y: Double,
    val size: Int
) : Serializable

data class CircleData(
    val centerX: Double,
    val centerY: Double,
    val radius: Double
) : Serializable

data class TextData(
    val x: Double,
    val y: Double,
    val text: String,
    val fontSize: Int
) : Serializable