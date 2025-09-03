package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик поиска
 * Адаптированная версия MouseHandlerSearch для Android
 */
class MouseHandlerSearch : BaseMouseHandler() {
    
    private var searchQuery: String = ""
    private var searchResults = mutableListOf<SearchResult>()
    private var allElements = mutableListOf<Any>()
    private var currentResultIndex: Int = -1
    private var isSearching = false
    private var searchOptions = SearchOptions()
    
    data class SearchResult(
        val element: Any,
        val matchType: MatchType,
        val relevance: Double,
        val description: String
    ) {
        fun getDisplayName(): String {
            return element.toString()
        }
    }
    
    data class SearchOptions(
        val caseSensitive: Boolean = false,
        val useRegex: Boolean = false,
        val searchInNames: Boolean = true,
        val searchInProperties: Boolean = true,
        val searchInContent: Boolean = true,
        val maxResults: Int = 100
    )
    
    enum class MatchType {
        EXACT,      // Точное совпадение
        PARTIAL,    // Частичное совпадение
        FUZZY,      // Нечеткое совпадение
        REGEX       // Регулярное выражение
    }
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        // Найти элемент под курсором
        val elementUnderCursor = findElementAt(point)
        
        if (elementUnderCursor != null) {
            // Выполнить поиск по найденному элементу
            val elementName = getElementName(elementUnderCursor)
            searchQuery = elementName
            performSearch()
            println("Поиск по элементу: $elementName")
        }
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        val currentPoint = offsetToPoint(offset)
        
        // Здесь можно добавить логику для динамического поиска при перетаскивании
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        val endPoint = offsetToPoint(offset)
        
        // Завершить поиск
        if (searchResults.isNotEmpty()) {
            println("Поиск завершен. Найдено результатов: ${searchResults.size}")
        }
        
        return true
    }
    
    private fun findElementAt(point: Point): Any? {
        // Здесь должна быть логика поиска элемента в указанной точке
        // Пока возвращаем null
        return null
    }
    
    private fun getElementName(element: Any): String {
        // Здесь должна быть логика получения имени элемента
        return element.toString()
    }
    
    fun search(query: String) {
        searchQuery = query
        performSearch()
    }
    
    private fun performSearch() {
        if (searchQuery.isEmpty()) {
            searchResults.clear()
            currentResultIndex = -1
            return
        }
        
        searchResults.clear()
        currentResultIndex = -1
        
        for (element in allElements) {
            val result = searchInElement(element, searchQuery)
            if (result != null) {
                searchResults.add(result)
            }
        }
        
        // Сортировать результаты по релевантности
        searchResults.sortByDescending { it.relevance }
        
        // Ограничить количество результатов
        if (searchResults.size > searchOptions.maxResults) {
            searchResults = searchResults.take(searchOptions.maxResults).toMutableList()
        }
        
        println("Поиск выполнен. Найдено: ${searchResults.size} результатов")
    }
    
    private fun searchInElement(element: Any, query: String): SearchResult? {
        val elementName = getElementName(element)
        val elementProperties = getElementProperties(element)
        val elementContent = getElementContent(element)
        
        var bestMatch: SearchResult? = null
        var bestRelevance = 0.0
        
        // Поиск в имени
        if (searchOptions.searchInNames) {
            val nameMatch = findMatch(elementName, query)
            if (nameMatch != null && nameMatch.relevance > bestRelevance) {
                bestMatch = SearchResult(element, nameMatch.matchType, nameMatch.relevance, "Имя: $elementName")
                bestRelevance = nameMatch.relevance
            }
        }
        
        // Поиск в свойствах
        if (searchOptions.searchInProperties) {
            for (property in elementProperties) {
                val propertyMatch = findMatch(property, query)
                if (propertyMatch != null && propertyMatch.relevance > bestRelevance) {
                    bestMatch = SearchResult(element, propertyMatch.matchType, propertyMatch.relevance, "Свойство: $property")
                    bestRelevance = propertyMatch.relevance
                }
            }
        }
        
        // Поиск в содержимом
        if (searchOptions.searchInContent) {
            val contentMatch = findMatch(elementContent, query)
            if (contentMatch != null && contentMatch.relevance > bestRelevance) {
                bestMatch = SearchResult(element, contentMatch.matchType, contentMatch.relevance, "Содержимое: $elementContent")
                bestRelevance = contentMatch.relevance
            }
        }
        
        return bestMatch
    }
    
    private fun findMatch(text: String, query: String): MatchResult? {
        if (text.isEmpty() || query.isEmpty()) return null
        
        val searchText = if (searchOptions.caseSensitive) text else text.lowercase()
        val searchQuery = if (searchOptions.caseSensitive) query else query.lowercase()
        
        return when {
            searchOptions.useRegex -> findRegexMatch(searchText, searchQuery)
            searchText == searchQuery -> MatchResult(MatchType.EXACT, 1.0)
            searchText.contains(searchQuery) -> MatchResult(MatchType.PARTIAL, 0.8)
            else -> findFuzzyMatch(searchText, searchQuery)
        }
    }
    
    private fun findRegexMatch(text: String, pattern: String): MatchResult? {
        return try {
            val regex = pattern.toRegex()
            if (regex.containsMatchIn(text)) {
                MatchResult(MatchType.REGEX, 0.9)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    private fun findFuzzyMatch(text: String, query: String): MatchResult? {
        // Простая реализация нечеткого поиска
        val similarity = calculateSimilarity(text, query)
        return if (similarity > 0.5) {
            MatchResult(MatchType.FUZZY, similarity)
        } else {
            null
        }
    }
    
    private fun calculateSimilarity(text: String, query: String): Double {
        // Простая реализация расчета схожести
        val commonChars = text.toSet().intersect(query.toSet())
        return commonChars.size.toDouble() / maxOf(text.length, query.length)
    }
    
    private fun getElementProperties(element: Any): List<String> {
        // Здесь должна быть логика получения свойств элемента
        return listOf()
    }
    
    private fun getElementContent(element: Any): String {
        // Здесь должна быть логика получения содержимого элемента
        return element.toString()
    }
    
    fun nextResult(): SearchResult? {
        if (searchResults.isEmpty()) return null
        
        currentResultIndex = (currentResultIndex + 1) % searchResults.size
        return searchResults[currentResultIndex]
    }
    
    fun previousResult(): SearchResult? {
        if (searchResults.isEmpty()) return null
        
        currentResultIndex = if (currentResultIndex <= 0) {
            searchResults.size - 1
        } else {
            currentResultIndex - 1
        }
        return searchResults[currentResultIndex]
    }
    
    fun getCurrentResult(): SearchResult? {
        return if (currentResultIndex >= 0 && currentResultIndex < searchResults.size) {
            searchResults[currentResultIndex]
        } else {
            null
        }
    }
    
    fun getSearchResults(): List<SearchResult> = searchResults.toList()
    
    fun getSearchQuery(): String = searchQuery
    
    fun setSearchQuery(query: String) {
        searchQuery = query
        performSearch()
    }
    
    fun setAllElements(elements: List<Any>) {
        allElements.clear()
        allElements.addAll(elements)
        performSearch()
    }
    
    fun getSearchOptions(): SearchOptions = searchOptions
    
    fun setSearchOptions(options: SearchOptions) {
        searchOptions = options
        performSearch()
    }
    
    fun clearSearch() {
        searchQuery = ""
        searchResults.clear()
        currentResultIndex = -1
        println("Поиск очищен")
    }
    
    fun getResultCount(): Int = searchResults.size
    
    fun getCurrentResultIndex(): Int = currentResultIndex
    
    fun hasResults(): Boolean = searchResults.isNotEmpty()
    
    fun getSearchStatistics(): String {
        return "Запрос: '$searchQuery', Найдено: ${getResultCount()}, Текущий: ${getCurrentResultIndex() + 1}"
    }
    
    fun exportSearchResults(): String {
        val sb = StringBuilder()
        sb.appendLine("Результаты поиска для '$searchQuery' (${searchResults.size}):")
        
        for ((index, result) in searchResults.withIndex()) {
            sb.appendLine("${index + 1}. ${result.getDisplayName()} (${result.matchType}, ${String.format("%.2f", result.relevance)})")
            sb.appendLine("   ${result.description}")
        }
        
        return sb.toString()
    }
    
    private data class MatchResult(
        val matchType: MatchType,
        val relevance: Double
    )
    
    override fun getName(): String = "Поиск"
    
    override fun getDescription(): String = "Найдите элементы по различным критериям"
} 