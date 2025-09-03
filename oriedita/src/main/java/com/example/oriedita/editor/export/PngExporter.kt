package com.example.oriedita.editor.export

import android.graphics.*
import android.graphics.drawable.Drawable
import java.io.File
import java.io.FileOutputStream

/**
 * Экспортер PNG файлов
 * Адаптированная версия PngExporter для Android
 */
class PngExporter : FileExporter {
    
    override fun supports(filename: File): Boolean {
        return filename.name.lowercase().endsWith(".png")
    }
    
    override fun doExport(data: ExportData, file: File): Boolean {
        return try {
            val bitmap = createBitmap(data)
            FileOutputStream(file).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    private fun createBitmap(data: ExportData): Bitmap {
        val width = 800
        val height = 600
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        // Установить белый фон
        canvas.drawColor(Color.WHITE)
        
        val paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        
        // Рисовать линии
        data.lines.forEach { line ->
            paint.color = getColor(line.color)
            paint.strokeWidth = getStrokeWidth(line.lineStyle).toFloat()
            canvas.drawLine(
                line.x1.toFloat(), line.y1.toFloat(),
                line.x2.toFloat(), line.y2.toFloat(),
                paint
            )
        }
        
        // Рисовать точки
        paint.style = Paint.Style.FILL
        data.points.forEach { point ->
            paint.color = Color.BLACK
            canvas.drawCircle(
                point.x.toFloat(), point.y.toFloat(),
                point.size.toFloat(), paint
            )
        }
        
        // Рисовать окружности
        paint.style = Paint.Style.STROKE
        paint.color = Color.BLACK
        data.circles.forEach { circle ->
            canvas.drawCircle(
                circle.centerX.toFloat(), circle.centerY.toFloat(),
                circle.radius.toFloat(), paint
            )
        }
        
        // Рисовать текст
        paint.style = Paint.Style.FILL
        paint.color = Color.BLACK
        data.texts.forEach { text ->
            paint.textSize = text.fontSize.toFloat()
            canvas.drawText(text.text, text.x.toFloat(), text.y.toFloat(), paint)
        }
        
        // Рисовать сложенные фигуры
        data.foldedFigures.forEach { figure ->
            drawFoldedFigure(canvas, figure, paint)
        }
        
        return bitmap
    }
    
    private fun drawFoldedFigure(canvas: Canvas, figure: FoldedFigureData, paint: Paint) {
        // Рисовать грани
        paint.style = Paint.Style.FILL
        paint.color = Color.LTGRAY
        
        figure.faces.forEach { face ->
            if (face.size >= 3) {
                val path = Path()
                val firstVertex = figure.vertices[face[0]]
                path.moveTo(firstVertex.x.toFloat(), firstVertex.y.toFloat())
                
                for (i in 1 until face.size) {
                    val vertex = figure.vertices[face[i]]
                    path.lineTo(vertex.x.toFloat(), vertex.y.toFloat())
                }
                path.close()
                canvas.drawPath(path, paint)
            }
        }
        
        // Рисовать границы граней
        paint.style = Paint.Style.STROKE
        paint.color = Color.BLACK
        paint.strokeWidth = 1f
        
        figure.faces.forEach { face ->
            if (face.size >= 3) {
                val path = Path()
                val firstVertex = figure.vertices[face[0]]
                path.moveTo(firstVertex.x.toFloat(), firstVertex.y.toFloat())
                
                for (i in 1 until face.size) {
                    val vertex = figure.vertices[face[i]]
                    path.lineTo(vertex.x.toFloat(), vertex.y.toFloat())
                }
                path.close()
                canvas.drawPath(path, paint)
            }
        }
    }
    
    private fun getColor(color: String): Int {
        return when (color.lowercase()) {
            "red" -> Color.RED
            "blue" -> Color.BLUE
            "green" -> Color.GREEN
            "black" -> Color.BLACK
            else -> Color.BLACK
        }
    }
    
    private fun getStrokeWidth(lineStyle: String): Int {
        return when (lineStyle.lowercase()) {
            "dashed" -> 3
            "dotted" -> 1
            else -> 2
        }
    }
    
    override fun getName(): String = "PNG"
    
    override fun getExtension(): String = "png"
} 