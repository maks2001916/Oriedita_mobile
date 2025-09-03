package com.example.oriedita.editor.export

import java.io.File

/**
 * Базовый интерфейс для экспортеров файлов
 * Адаптированная версия для Android
 */
interface FileExporter {
    
    /**
     * Проверить, поддерживается ли файл
     */
    fun supports(filename: File): Boolean
    
    /**
     * Выполнить экспорт
     */
    fun doExport(data: ExportData, file: File): Boolean
    
    /**
     * Получить название экспортера
     */
    fun getName(): String
    
    /**
     * Получить расширение файла
     */
    fun getExtension(): String
}

/**
 * Данные для экспорта
 */
data class ExportData(
    val lines: List<LineData>,
    val points: List<PointData>,
    val circles: List<CircleData>,
    val texts: List<TextData>,
    val foldedFigures: List<FoldedFigureData>
)

data class LineData(
    val x1: Double,
    val y1: Double,
    val x2: Double,
    val y2: Double,
    val color: String,
    val lineStyle: String
)

data class PointData(
    val x: Double,
    val y: Double,
    val size: Int
)

data class CircleData(
    val centerX: Double,
    val centerY: Double,
    val radius: Double
)

data class TextData(
    val x: Double,
    val y: Double,
    val text: String,
    val fontSize: Int
)

data class FoldedFigureData(
    val vertices: List<PointData>,
    val faces: List<List<Int>>
) 