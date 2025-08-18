package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.Point

/**
 * Базовый класс для обработчиков мыши с пошаговыми действиями
 * Позволяет создавать обработчики с конечным автоматом состояний
 * Адаптированная версия для Android
 */
abstract class StepMouseHandler<T : Enum<T>>(
    initialStep: T
) : BaseMouseHandler() {
    
    private var mousePos = Point(0.0, 0.0)
    protected var steps: StepGraph<T> = StepGraph(initialStep)
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        mousePos = offsetToPoint(offset)
        steps?.runCurrentPressAction(mousePos)
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        mousePos = offsetToPoint(offset)
        steps?.runCurrentDragAction(mousePos)
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        mousePos = offsetToPoint(offset)
        steps?.runCurrentReleaseAction(mousePos)
        return true
    }
    
    /**
     * Обработка движения мыши (для предварительного просмотра)
     */
    fun handleMouseMoved(offset: Offset) {
        mousePos = offsetToPoint(offset)
        steps?.runCurrentMoveAction(mousePos)
    }
    
    fun getCurrentStep(): T? = steps?.getCurrentStep()
    
    fun getMousePosition(): Point = mousePos
    
    fun setStep(step: T) {
        steps?.setCurrentStep(step)
        println("Установлен шаг: $step")
    }
    
    fun resetSteps() {
        steps?.reset()
        println("Шаги сброшены")
    }
    
    fun getStepDescription(): String {
        return steps?.getCurrentStep()?.name ?: "Неизвестный шаг"
    }
    
    fun getStepInfo(): String {
        return buildString {
            append("Текущий шаг: ${getStepDescription()}\n")
            append("Позиция мыши: (${String.format("%.2f", mousePos.x)}, ${String.format("%.2f", mousePos.y)})")
        }
    }
}

/**
 * Граф состояний для пошаговых обработчиков
 */
class StepGraph<T : Enum<T>>(initialStep: T) {
    
    private var currentStep: T = initialStep
    private val nodes = mutableMapOf<T, StepNode<T>>()
    
    init {
        addNode(StepNode(currentStep))
    }
    
    fun addNode(node: StepNode<T>) {
        nodes[node.step] = node
    }
    
    fun setCurrentStep(step: T) {
        currentStep = step
    }
    
    fun getCurrentStep(): T = currentStep
    
    fun reset() {
        // Сбросить к начальному состоянию
        if (nodes.isNotEmpty()) {
            currentStep = nodes.keys.first()
        }
    }
    
    fun runCurrentPressAction(point: Point) {
        nodes[currentStep]?.pressAction?.invoke(point)
    }
    
    fun runCurrentMoveAction(point: Point) {
        nodes[currentStep]?.moveAction?.invoke(point)
    }
    
    fun runCurrentDragAction(point: Point) {
        nodes[currentStep]?.dragAction?.invoke(point)
    }
    
    fun runCurrentReleaseAction(point: Point): T? {
        val nextStep = nodes[currentStep]?.releaseAction?.invoke(point)
        if (nextStep != null) {
            currentStep = nextStep
        }
        return nextStep
    }
}

/**
 * Узел состояния в графе шагов
 */
class StepNode<T : Enum<T>>(
    val step: T,
    val pressAction: ((Point) -> Unit)? = null,
    val moveAction: ((Point) -> Unit)? = null,
    val dragAction: ((Point) -> Unit)? = null,
    val releaseAction: ((Point) -> T?)? = null
) {
    companion object {
        fun <T : Enum<T>> createNode(
            step: T,
            pressAction: ((Point) -> Unit)? = null,
            moveAction: ((Point) -> Unit)? = null,
            dragAction: ((Point) -> Unit)? = null,
            releaseAction: ((Point) -> T?)? = null
        ): StepNode<T> {
            return StepNode(step, pressAction, moveAction, dragAction, releaseAction)
        }
        
        fun <T : Enum<T>> createNode_MD_R(
            step: T,
            moveAction: ((Point) -> Unit)? = null,
            dragAction: ((Point) -> Unit)? = null,
            releaseAction: ((Point) -> T?)? = null
        ): StepNode<T> {
            return StepNode(step, null, moveAction, dragAction, releaseAction)
        }
    }
} 