package com.example.oriedita_ui.handler

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.Circle
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик выбора концентрических кругов
 * 
 * Этот класс реализует функциональность для создания новых кругов на основе
 * трех выбранных концентрических кругов. Алгоритм работает следующим образом:
 * 
 * 1. Пользователь выбирает первый круг (зеленый цвет)
 * 2. Пользователь выбирает второй круг (фиолетовый цвет)
 * 3. Пользователь выбирает третий круг (фиолетовый цвет)
 * 4. Система вычисляет разность радиусов между вторым и третьим кругом
 * 5. Создается новый круг с радиусом = радиус_первого_круга + разность_радиусов
 * 
 * Адаптированная версия для Android с использованием Jetpack Compose
 * 
 * @see BaseMouseHandler
 * @see CreasePattern_Worker
 */
class MouseHandlerCircleDrawConcentricSelect : BaseMouseHandler() {
    
    private var closestCircumference = Circle(100000.0, 100000.0, 10.0, LineColor.PURPLE_8)
    private var selectedCircles = mutableListOf<Circle>()
    private var isProcessing = false
    private var lastError: String? = null
    
    companion object {
        private const val TAG = "ConcentricSelectHandler"
        private const val DEFAULT_SELECTION_DISTANCE = 10.0
    }
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        try {
            val point = offsetToPoint(offset)
            
            // Обновить ближайшую окружность
            val closestCircleMidpoint = getClosestCircleMidpoint(point)
            closestCircumference = Circle(closestCircleMidpoint.getX(), closestCircleMidpoint.getY(), 10.0, LineColor.PURPLE_8)
            
            // Проверить количество уже выбранных кругов
            when {
                getCircleStepList().size == 0 && getLineStepList().size == 0 -> {
                    if (OritaCalc.distance_circumference(point, closestCircumference) <= getSelectionDistance()) {
                        val circle = Circle(closestCircumference.getX(), closestCircumference.getY(), closestCircumference.getR(), LineColor.GREEN_6)
                        addCircleToStep(circle)
                        selectedCircles.add(circle)
                        lastError = null
                        Log.d(TAG, "Выбран первый круг с радиусом: ${circle.getR()}")
                    } else {
                        lastError = "Круг слишком далеко от точки касания"
                        Log.w(TAG, "Ошибка: круг слишком далеко от точки касания")
                    }
                }
                getCircleStepList().size == 1 && getLineStepList().size == 0 -> {
                    if (OritaCalc.distance_circumference(point, closestCircumference) <= getSelectionDistance()) {
                        val circle = Circle(closestCircumference.getX(), closestCircumference.getY(), closestCircumference.getR(), LineColor.PURPLE_8)
                        addCircleToStep(circle)
                        selectedCircles.add(circle)
                        lastError = null
                        Log.d(TAG, "Выбран второй круг с радиусом: ${circle.getR()}")
                    } else {
                        lastError = "Круг слишком далеко от точки касания"
                        Log.w(TAG, "Ошибка: круг слишком далеко от точки касания")
                    }
                }
                getCircleStepList().size == 2 && getLineStepList().size == 0 -> {
                    if (OritaCalc.distance_circumference(point, closestCircumference) <= getSelectionDistance()) {
                        val circle = Circle(closestCircumference.getX(), closestCircumference.getY(), closestCircumference.getR(), LineColor.PURPLE_8)
                        addCircleToStep(circle)
                        selectedCircles.add(circle)
                        lastError = null
                        Log.d(TAG, "Выбран третий круг с радиусом: ${circle.getR()}")
                    } else {
                        lastError = "Круг слишком далеко от точки касания"
                        Log.w(TAG, "Ошибка: круг слишком далеко от точки касания")
                    }
                }
                else -> {
                    lastError = "Неожиданное состояние: ${getCircleStepList().size} кругов, ${getLineStepList().size} линий"
                    Log.w(TAG, "Неожиданное состояние: ${getCircleStepList().size} кругов, ${getLineStepList().size} линий")
                }
            }
            
            return true
        } catch (e: Exception) {
            lastError = "Ошибка при обработке нажатия: ${e.message}"
            Log.e(TAG, "Ошибка в onPress: ${e.message}", e)
            return false
        }
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        // Выбор концентрических кругов не требует перетаскивания
        return false
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        try {
            if (getCircleStepList().size == 3 && getLineStepList().size == 0) {
                // Обработать три выбранных круга
                processSelectedCircles()
            }
            
            return true
        } catch (e: Exception) {
            lastError = "Ошибка при обработке отпускания: ${e.message}"
            Log.e(TAG, "Ошибка в onRelease: ${e.message}", e)
            return false
        }
    }
    
    /**
     * Обрабатывает три выбранных кругов и создает новый круг
     * 
     * Алгоритм:
     * 1. Проверяет, что выбрано ровно 3 круга
     * 2. Проверяет, что круги концентрические (имеют общий центр)
     * 3. Вычисляет разность радиусов между вторым и третьим кругом
     * 4. Создает новый круг с радиусом = радиус_первого_круга + разность_радиусов
     * 5. Добавляет новый круг в модель и записывает состояние
     */
    private fun processSelectedCircles() {
        try {
            if (selectedCircles.size != 3) {
                lastError = "Ожидается 3 круга, но выбрано ${selectedCircles.size}"
                Log.w(TAG, "Ошибка: ожидается 3 круга, но выбрано ${selectedCircles.size}")
                return
            }
            
            val circle1 = selectedCircles[0]
            val circle2 = selectedCircles[1]
            val circle3 = selectedCircles[2]
            
            Log.d(TAG, "Обработка кругов:")
            Log.d(TAG, "  Круг 1: радиус = ${circle1.getR()}, центр = (${circle1.determineCenter().x}, ${circle1.determineCenter().y})")
            Log.d(TAG, "  Круг 2: радиус = ${circle2.getR()}, центр = (${circle2.determineCenter().x}, ${circle2.determineCenter().y})")
            Log.d(TAG, "  Круг 3: радиус = ${circle3.getR()}, центр = (${circle3.determineCenter().x}, ${circle3.determineCenter().y})")
            
            // Проверяем, что круги концентрические (имеют общий центр)
            val center1 = circle1.determineCenter()
            val center2 = circle2.determineCenter()
            val center3 = circle3.determineCenter()
            
            if (!center1.equals(center2) || !center1.equals(center3)) {
                lastError = "Круги должны быть концентрическими (иметь общий центр)"
                Log.w(TAG, "Ошибка: круги должны быть концентрическими")
                Log.w(TAG, "  Центр 1: (${center1.x}, ${center1.y})")
                Log.w(TAG, "  Центр 2: (${center2.x}, ${center2.y})")
                Log.w(TAG, "  Центр 3: (${center3.x}, ${center3.y})")
                return
            }
            
            // Вычислить разность радиусов
            val addRadius = circle3.getR() - circle2.getR()
            Log.d(TAG, "Разность радиусов (r3 - r2): $addRadius")
            
            if (addRadius == 0.0) {
                lastError = "Разность радиусов между вторым и третьим кругом равна нулю"
                Log.w(TAG, "Ошибка: разность радиусов равна нулю")
                return
            }
            
            val newRadius = addRadius + circle1.getR()
            Log.d(TAG, "Новый радиус (r1 + (r3 - r2)): $newRadius")
            
            if (newRadius <= 0.0) {
                lastError = "Новый радиус должен быть положительным (получено: $newRadius)"
                Log.w(TAG, "Ошибка: новый радиус должен быть положительным")
                return
            }
            
            // Обновить первый круг
            circle1.setR(newRadius)
            circle1.setColor(LineColor.CYAN_3)
            
            addCircle(circle1)
            record()
            lastError = null
            Log.i(TAG, "Добавлен обновленный круг с радиусом: ${circle1.getR()}")
            
            // Очистить шаги
            resetCircleStep()
            selectedCircles.clear()
            
        } catch (e: Exception) {
            lastError = "Ошибка при обработке выбранных кругов: ${e.message}"
            Log.e(TAG, "Ошибка в processSelectedCircles: ${e.message}", e)
        }
    }
    
    override fun getClosestCircleMidpoint(point: Point): Circle {
        try {
            // Используем CreasePattern_Worker для получения ближайшего круга
            val closestCircle = creasePatternWorker?.getClosestCircleMidpoint(point)
            return if (closestCircle != null) {
                Circle(closestCircle.determineCenter(), 0.0, LineColor.GREEN_6)
            } else {
                Circle(point, 0.0, LineColor.GREEN_6)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при получении ближайшего круга: ${e.message}", e)
            return Circle(point, 0.0, LineColor.GREEN_6)
        }
    }
    
    override fun getClosestCircle(point: Point): Circle? {
        try {
            // Получаем ближайший круг через CreasePattern_Worker
            val closestCircle = creasePatternWorker?.getClosestCircleMidpoint(point)
            return if (closestCircle != null) {
                Circle(closestCircle.determineCenter(), closestCircle.getR(), closestCircle.getColor())
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при получении ближайшего круга: ${e.message}", e)
            return null
        }
    }
    
    private fun addCircleToStep(circle: Circle) {
        try {
            // Добавляем круг в шаги через CreasePattern_Worker
            creasePatternWorker?.let { worker ->
                // Получаем текущий список кругов и добавляем новый
                val currentList = worker.getCircleStep().toMutableList()
                currentList.add(circle)
                
                // Очищаем текущие шаги и добавляем все круги заново
                worker.resetCircleStep()
                currentList.forEach { worker.addCircle(it) }
                
                Log.d(TAG, "Круг добавлен в шаги: радиус = ${circle.getR()}, центр = (${circle.determineCenter().x}, ${circle.determineCenter().y})")
            } ?: run {
                lastError = "CreasePattern_Worker не инициализирован"
                Log.e(TAG, "Ошибка: CreasePattern_Worker не инициализирован")
            }
        } catch (e: Exception) {
            lastError = "Ошибка при добавлении круга в шаги: ${e.message}"
            Log.e(TAG, "Ошибка в addCircleToStep: ${e.message}", e)
        }
    }
    
    /**
     * Проверяет, готов ли обработчик к работе
     */
    fun isReady(): Boolean {
        return creasePatternWorker != null
    }
    
    /**
     * Проверяет, можно ли выбрать круг в данной точке
     */
    fun canSelectCircleAt(point: Point): Boolean {
        val closestCircle = getClosestCircle(point)
        return closestCircle != null && 
               OritaCalc.distance_circumference(point, closestCircle) <= getSelectionDistance()
    }
    
    /**
     * Получает состояние обработчика
     */
    fun getState(): HandlerState {
        return HandlerState(
            selectedCirclesCount = selectedCircles.size,
            circleStepSize = getCircleStepList().size,
            lineStepSize = getLineStepList().size,
            isProcessing = isProcessing,
            lastError = lastError,
            isReady = isReady()
        )
    }
    
    /**
     * Валидирует выбранные круги
     */
    fun validateSelectedCircles(): ValidationResult {
        if (selectedCircles.size != 3) {
            return ValidationResult(false, "Ожидается 3 круга, но выбрано ${selectedCircles.size}")
        }
        
        val circle1 = selectedCircles[0]
        val circle2 = selectedCircles[1]
        val circle3 = selectedCircles[2]
        
        // Проверяем концентричность
        val center1 = circle1.determineCenter()
        val center2 = circle2.determineCenter()
        val center3 = circle3.determineCenter()
        
        if (!center1.equals(center2) || !center1.equals(center3)) {
            return ValidationResult(false, "Круги должны быть концентрическими")
        }
        
        // Проверяем радиусы
        val addRadius = circle3.getR() - circle2.getR()
        if (addRadius == 0.0) {
            return ValidationResult(false, "Разность радиусов равна нулю")
        }
        
        val newRadius = addRadius + circle1.getR()
        if (newRadius <= 0.0) {
            return ValidationResult(false, "Новый радиус должен быть положительным")
        }
        
        return ValidationResult(true, "Валидация прошла успешно")
    }
    
    fun getClosestCircumference(): Circle = closestCircumference
    
    fun getSelectedCircles(): List<Circle> = selectedCircles.toList()
    
    fun getSelectedCirclesCount(): Int = selectedCircles.size
    
    fun isProcessing(): Boolean = isProcessing
    
    fun getLastError(): String? = lastError
    
    fun clearSelectedCircles() {
        selectedCircles.clear()
        resetCircleStep()
        lastError = null
        Log.d(TAG, "Выбранные круги очищены")
    }
    
    override fun reset() {
        selectedCircles.clear()
        resetCircleStep()
        isProcessing = false
        lastError = null
        closestCircumference = Circle(100000.0, 100000.0, 10.0, LineColor.PURPLE_8)
        
        Log.d(TAG, "Обработчик выбора концентрических кругов сброшен")
    }
    
    fun getConcentricSelectDescription(): String {
        return when (selectedCircles.size) {
            0 -> "Выберите первый круг"
            1 -> "Выберите второй круг"
            2 -> "Выберите третий круг"
            3 -> "Обработка выбранных кругов"
            else -> "Неизвестное состояние"
        }
    }
    
    fun getSelectedCirclesInfo(): String {
        return buildString {
            append("Выбранные круги:\n")
            selectedCircles.forEachIndexed { index, circle ->
                append("${index + 1}. Центр: (${circle.determineCenter().x}, ${circle.determineCenter().y}), ")
                append("Радиус: ${circle.getR()}, ")
                append("Цвет: ${getColorName(circle.getColor())}\n")
            }
            if (lastError != null) {
                append("\nПоследняя ошибка: $lastError")
            }
        }
    }
    
    private fun getColorName(color: LineColor): String {
        return when (color) {
            LineColor.BLACK_0 -> "Черный"
            LineColor.RED_1 -> "Красный"
            LineColor.BLUE_2 -> "Синий"
            LineColor.CYAN_3 -> "Голубой"
            LineColor.ORANGE_4 -> "Оранжевый"
            LineColor.GREEN_6 -> "Зеленый"
            LineColor.PURPLE_8 -> "Фиолетовый"
            else -> "Другой"
        }
    }
    
    override fun getName(): String = "Выбор концентрических кругов"
    
    override fun getDescription(): String = "Выбирайте концентрические круги для создания новых кругов"
    
    /**
     * Состояние обработчика
     */
    data class HandlerState(
        val selectedCirclesCount: Int,
        val circleStepSize: Int,
        val lineStepSize: Int,
        val isProcessing: Boolean,
        val lastError: String?,
        val isReady: Boolean
    )
    
    /**
     * Результат валидации
     */
    data class ValidationResult(
        val isValid: Boolean,
        val message: String
    )
} 