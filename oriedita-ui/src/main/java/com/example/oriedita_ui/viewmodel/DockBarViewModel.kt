package com.example.oriedita_ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import com.example.oriedita_ui.ui.CanvasTool

open class DockBarViewModel : ViewModel() {
    open var position by mutableStateOf(Offset(100f, 100f))
    open var isVertical by mutableStateOf(false)
    open var pinnedTools by mutableStateOf(List(5) { CanvasTool.DrawCreaseFree as CanvasTool? })

    fun setToolAt(index: Int, tool: CanvasTool) {
        pinnedTools = pinnedTools.toMutableList().also { it[index] = tool }
    }

    fun moveTo(newPos: Offset, screenWidth: Float, screenHeight: Float) {
        position = newPos
        val margin = 32f
        isVertical = (newPos.x < margin) || (newPos.x > screenWidth - margin)
        position = Offset(
            x = when {
                newPos.x < margin -> 0f
                newPos.x > screenWidth - margin -> screenWidth
                else -> newPos.x
            },
            y = when {
                newPos.y < margin -> 0f
                newPos.y > screenHeight - margin -> screenHeight
                else -> newPos.y
            }
        )
    }
} 