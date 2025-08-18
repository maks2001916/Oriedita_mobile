package com.example.oriedita.editor.export

import java.io.File

/**
 * Менеджер экспортеров файлов
 * Управляет всеми экспортерами в приложении
 */
class ExportManager {
    
    private val exporters = mutableListOf<FileExporter>()
    
    init {
        registerExporters()
    }
    
    private fun registerExporters() {
        exporters.add(SvgExporter())
        exporters.add(PngExporter())
        exporters.add(JpgExporter())
    }
    
    /**
     * Получить список всех экспортеров
     */
    fun getAllExporters(): List<FileExporter> = exporters.toList()
    
    /**
     * Найти подходящий экспортер для файла
     */
    fun findExporter(filename: File): FileExporter? {
        return exporters.find { it.supports(filename) }
    }
    
    /**
     * Экспортировать данные в файл
     */
    fun export(data: ExportData, file: File): Boolean {
        val exporter = findExporter(file)
        return exporter?.doExport(data, file) ?: false
    }
    
    /**
     * Получить экспортер по расширению
     */
    fun getExporterByExtension(extension: String): FileExporter? {
        val ext = extension.lowercase().removePrefix(".")
        return exporters.find { it.getExtension().lowercase() == ext }
    }
    
    /**
     * Получить список поддерживаемых форматов
     */
    fun getSupportedFormats(): List<String> {
        return exporters.map { it.getExtension() }
    }
} 