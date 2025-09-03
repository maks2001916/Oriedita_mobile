package com.example.oriedita.editor.export

import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.text.DecimalFormat
import java.util.*

/**
 * Экспортер SVG файлов
 * Адаптированная версия SvgExporter для Android
 */
class SvgExporter : FileExporter {
    
    override fun supports(filename: File): Boolean {
        return filename.name.lowercase().endsWith(".svg")
    }
    
    override fun doExport(data: ExportData, file: File): Boolean {
        return try {
            FileWriter(file).use { fw ->
                PrintWriter(fw).use { pw ->
                    exportSvg(data, pw)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    private fun exportSvg(data: ExportData, pw: PrintWriter) {
        Locale.setDefault(Locale.ENGLISH)
        
        // Начало SVG
        pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
        pw.println("<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" width=\"800\" height=\"600\">")
        
        // Экспорт линий
        if (data.lines.isNotEmpty()) {
            pw.println("  <g id=\"lines\">")
            data.lines.forEach { line ->
                exportLine(pw, line)
            }
            pw.println("  </g>")
        }
        
        // Экспорт точек
        if (data.points.isNotEmpty()) {
            pw.println("  <g id=\"points\">")
            data.points.forEach { point ->
                exportPoint(pw, point)
            }
            pw.println("  </g>")
        }
        
        // Экспорт окружностей
        if (data.circles.isNotEmpty()) {
            pw.println("  <g id=\"circles\">")
            data.circles.forEach { circle ->
                exportCircle(pw, circle)
            }
            pw.println("  </g>")
        }
        
        // Экспорт текста
        if (data.texts.isNotEmpty()) {
            pw.println("  <g id=\"texts\">")
            data.texts.forEach { text ->
                exportText(pw, text)
            }
            pw.println("  </g>")
        }
        
        // Экспорт сложенных фигур
        if (data.foldedFigures.isNotEmpty()) {
            data.foldedFigures.forEachIndexed { index, figure ->
                pw.println("  <g id=\"folded-figure-$index\">")
                exportFoldedFigure(pw, figure)
                pw.println("  </g>")
            }
        }
        
        // Конец SVG
        pw.println("</svg>")
    }
    
    private fun exportLine(pw: PrintWriter, line: LineData) {
        val format = DecimalFormat("#.#")
        val x1 = format.format(line.x1)
        val y1 = format.format(line.y1)
        val x2 = format.format(line.x2)
        val y2 = format.format(line.y2)
        
        val strokeColor = getStrokeColor(line.color)
        val strokeWidth = getStrokeWidth(line.lineStyle)
        
        pw.println("    <line x1=\"$x1\" y1=\"$y1\" x2=\"$x2\" y2=\"$y2\" stroke=\"$strokeColor\" stroke-width=\"$strokeWidth\"/>")
    }
    
    private fun exportPoint(pw: PrintWriter, point: PointData) {
        val format = DecimalFormat("#.#")
        val x = format.format(point.x)
        val y = format.format(point.y)
        val size = point.size
        
        pw.println("    <circle cx=\"$x\" cy=\"$y\" r=\"$size\" fill=\"black\" stroke=\"black\"/>")
    }
    
    private fun exportCircle(pw: PrintWriter, circle: CircleData) {
        val format = DecimalFormat("#.#")
        val cx = format.format(circle.centerX)
        val cy = format.format(circle.centerY)
        val r = format.format(circle.radius)
        
        pw.println("    <circle cx=\"$cx\" cy=\"$cy\" r=\"$r\" fill=\"none\" stroke=\"black\" stroke-width=\"1\"/>")
    }
    
    private fun exportText(pw: PrintWriter, text: TextData) {
        val format = DecimalFormat("#.#")
        val x = format.format(text.x)
        val y = format.format(text.y)
        val fontSize = text.fontSize
        
        pw.println("    <text x=\"$x\" y=\"$y\" font-family=\"sans-serif\" font-size=\"${fontSize}px\" fill=\"black\">${text.text}</text>")
    }
    
    private fun exportFoldedFigure(pw: PrintWriter, figure: FoldedFigureData) {
        // Экспорт вершин
        figure.vertices.forEach { vertex ->
            exportPoint(pw, vertex)
        }
        
        // Экспорт граней (полигонов)
        figure.faces.forEach { face ->
            if (face.size >= 3) {
                val points = face.joinToString(" ") { vertexIndex ->
                    val vertex = figure.vertices[vertexIndex]
                    "${vertex.x},${vertex.y}"
                }
                pw.println("    <polygon points=\"$points\" fill=\"lightgray\" stroke=\"black\" stroke-width=\"1\"/>")
            }
        }
    }
    
    private fun getStrokeColor(color: String): String {
        return when (color.lowercase()) {
            "red" -> "red"
            "blue" -> "blue"
            "green" -> "green"
            "black" -> "black"
            else -> "black"
        }
    }
    
    private fun getStrokeWidth(lineStyle: String): String {
        return when (lineStyle.lowercase()) {
            "dashed" -> "2"
            "dotted" -> "1"
            else -> "1"
        }
    }
    
    override fun getName(): String = "SVG"
    
    override fun getExtension(): String = "svg"
} 