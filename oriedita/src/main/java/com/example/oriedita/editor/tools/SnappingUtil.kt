package com.example.oriedita.editor.tools

import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.OritaCalc
import com.example.oriedita_core.origami.Epsilon
import kotlin.math.roundToInt

/**
 * Утилита привязки для Android
 * Адаптированная версия SnappingUtil для Android
 */
object SnappingUtil {
    
    /**
     * Привязка к активной угловой системе
     */
    fun snapToActiveAngleSystem(
        start: Point,
        p: Point,
        angleSystemDivider: Int,
        angles: DoubleArray
    ): Point {
        var d_rad = 0.0
        val s = LineSegment(p, start)
        val d_angle_system: Double
        
        if (angleSystemDivider != 0) {
            d_angle_system = 180.0 / angleSystemDivider
            d_rad = (Math.PI / 180) * d_angle_system * 
                   (OritaCalc.angle(s) / d_angle_system).toInt()
        } else {
            val currentAngle = OritaCalc.angle(s)
            var d_kakudo_sa_min = 1000.0
            
            for (i in 0 until 6) {
                val angle = angles[i] - 180.0
                val angleDiff = minOf(
                    OritaCalc.angle_between_0_360(angle - currentAngle),
                    OritaCalc.angle_between_0_360(currentAngle - angle)
                )
                
                if (angleDiff < d_kakudo_sa_min) {
                    d_kakudo_sa_min = angleDiff
                    d_rad = (Math.PI / 180) * angle
                }
            }
        }
        
        val snapLine = LineSegment(
            s.b,
            Point(
                s.determineBX() + Math.cos(d_rad),
                s.determineBY() + Math.sin(d_rad)
            )
        )
        
        return OritaCalc.findProjection(snapLine, p)
    }
    
    /**
     * Привязка к ближайшей точке в активной угловой системе
     */
    fun snapToClosePointInActiveAngleSystem(
        start: Point,
        p: Point,
        angleSystemDivider: Int,
        angles: DoubleArray,
        getClosestPoint: (Point) -> Point,
        getSelectionDistance: () -> Double
    ): Point {
        val syuusei_point = snapToActiveAngleSystem(start, p, angleSystemDivider, angles)
        val closestPoint = getClosestPoint(syuusei_point)
        val zure_kakudo = OritaCalc.angle(start, syuusei_point, start, closestPoint)
        val zure_flg = (Epsilon.UNKNOWN_1EN5 < zure_kakudo) && (zure_kakudo <= 360.0 - Epsilon.UNKNOWN_1EN5)
        
        return if (zure_flg || (syuusei_point.distance(closestPoint) > getSelectionDistance())) {
            syuusei_point
        } else {
            closestPoint
        }
    }
    
    /**
     * Привязка к сетке
     */
    fun snapToGrid(point: Point, gridSize: Double): Point {
        val x = (point.x / gridSize).roundToInt() * gridSize
        val y = (point.y / gridSize).roundToInt() * gridSize
        return Point(x, y)
    }
    
    /**
     * Привязка к линиям
     */
    fun snapToLines(
        point: Point,
        lines: List<LineSegment>,
        selectionDistance: Double
    ): Point? {
        var closestPoint: Point? = null
        var minDistance = Double.MAX_VALUE
        
        for (line in lines) {
            val projection = OritaCalc.findProjection(line, point)
            val distance = point.distance(projection)
            
            if (distance < selectionDistance && distance < minDistance) {
                minDistance = distance
                closestPoint = projection
            }
        }
        
        return closestPoint
    }
    
    /**
     * Привязка к точкам
     */
    fun snapToPoints(
        point: Point,
        points: List<Point>,
        selectionDistance: Double
    ): Point? {
        var closestPoint: Point? = null
        var minDistance = Double.MAX_VALUE
        
        for (p in points) {
            val distance = point.distance(p)
            if (distance < selectionDistance && distance < minDistance) {
                minDistance = distance
                closestPoint = p
            }
        }
        
        return closestPoint
    }
} 