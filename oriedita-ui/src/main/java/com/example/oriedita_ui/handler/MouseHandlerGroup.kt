package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик группировки
 * Адаптированная версия MouseHandlerGroup для Android
 */
class MouseHandlerGroup : BaseMouseHandler() {
    
    private var selectedElements = mutableListOf<Any>()
    private var groups = mutableListOf<Group>()
    private var currentGroup: Group? = null
    private var isGrouping = false
    
    data class Group(
        val id: String,
        val name: String,
        val elements: MutableList<Any>,
        val isVisible: Boolean = true,
        val isLocked: Boolean = false
    ) {
        fun addElement(element: Any) {
            if (!elements.contains(element)) {
                elements.add(element)
            }
        }
        
        fun removeElement(element: Any) {
            elements.remove(element)
        }
        
        fun getElementCount(): Int = elements.size
        
        fun isEmpty(): Boolean = elements.isEmpty()
    }
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        // Найти элемент под курсором
        val elementUnderCursor = findElementAt(point)
        
        if (elementUnderCursor != null) {
            // Добавить элемент к текущему выделению
            if (!selectedElements.contains(elementUnderCursor)) {
                selectedElements.add(elementUnderCursor)
                println("Элемент добавлен к выделению: $elementUnderCursor")
            }
        } else {
            // Начать новое выделение
            selectedElements.clear()
            println("Начато новое выделение")
        }
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        val currentPoint = offsetToPoint(offset)
        
        // Здесь можно добавить логику для выделения элементов при перетаскивании
        // Например, выделение всех элементов в прямоугольнике
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        val endPoint = offsetToPoint(offset)
        
        // Завершить выделение
        println("Завершено выделение. Выделено элементов: ${selectedElements.size}")
        
        return true
    }
    
    private fun findElementAt(point: Point): Any? {
        // Здесь должна быть логика поиска элемента в указанной точке
        // Пока возвращаем null
        return null
    }
    
    fun createGroup(name: String): Group {
        if (selectedElements.isEmpty()) {
            throw IllegalStateException("Нет выделенных элементов для группировки")
        }
        
        val groupId = generateGroupId()
        val group = Group(groupId, name, selectedElements.toMutableList())
        groups.add(group)
        currentGroup = group
        
        println("Создана группа '$name' с ${selectedElements.size} элементами")
        
        return group
    }
    
    fun deleteGroup(group: Group) {
        groups.remove(group)
        if (currentGroup == group) {
            currentGroup = null
        }
        println("Группа '${group.name}' удалена")
    }
    
    fun addToGroup(group: Group, element: Any) {
        group.addElement(element)
        println("Элемент добавлен в группу '${group.name}'")
    }
    
    fun removeFromGroup(group: Group, element: Any) {
        group.removeElement(element)
        println("Элемент удален из группы '${group.name}'")
    }
    
    fun getGroups(): List<Group> = groups.toList()
    
    fun getCurrentGroup(): Group? = currentGroup
    
    fun setCurrentGroup(group: Group?) {
        currentGroup = group
        println("Текущая группа: ${group?.name ?: "нет"}")
    }
    
    fun getSelectedElements(): List<Any> = selectedElements.toList()
    
    fun clearSelection() {
        selectedElements.clear()
        println("Выделение очищено")
    }
    
    fun getSelectionCount(): Int = selectedElements.size
    
    fun getGroupCount(): Int = groups.size
    
    fun findGroupByName(name: String): Group? {
        return groups.find { it.name == name }
    }
    
    fun findGroupById(id: String): Group? {
        return groups.find { it.id == id }
    }
    
    fun toggleGroupVisibility(group: Group) {
        val newVisibility = !group.isVisible
        val updatedGroup = group.copy(isVisible = newVisibility)
        val index = groups.indexOf(group)
        if (index != -1) {
            groups[index] = updatedGroup
        }
        println("Видимость группы '${group.name}' ${if (newVisibility) "включена" else "отключена"}")
    }
    
    fun toggleGroupLock(group: Group) {
        val newLocked = !group.isLocked
        val updatedGroup = group.copy(isLocked = newLocked)
        val index = groups.indexOf(group)
        if (index != -1) {
            groups[index] = updatedGroup
        }
        println("Блокировка группы '${group.name}' ${if (newLocked) "включена" else "отключена"}")
    }
    
    fun mergeGroups(group1: Group, group2: Group): Group {
        val mergedElements = (group1.elements + group2.elements).distinct().toMutableList()
        val mergedGroup = Group(
            generateGroupId(),
            "${group1.name} + ${group2.name}",
            mergedElements
        )
        
        groups.remove(group1)
        groups.remove(group2)
        groups.add(mergedGroup)
        
        println("Группы '${group1.name}' и '${group2.name}' объединены")
        
        return mergedGroup
    }
    
    fun splitGroup(group: Group, elements: List<Any>): Group {
        val remainingElements = group.elements.filter { it !in elements }.toMutableList()
        val splitGroup = Group(
            generateGroupId(),
            "${group.name}_split",
            elements.toMutableList()
        )
        
        val updatedGroup = group.copy(elements = remainingElements)
        val index = groups.indexOf(group)
        if (index != -1) {
            groups[index] = updatedGroup
        }
        groups.add(splitGroup)
        
        println("Группа '${group.name}' разделена")
        
        return splitGroup
    }
    
    private fun generateGroupId(): String {
        return "group_${System.currentTimeMillis()}_${groups.size}"
    }
    
    override fun getName(): String = "Группировка"
    
    override fun getDescription(): String = "Группируйте элементы"
} 