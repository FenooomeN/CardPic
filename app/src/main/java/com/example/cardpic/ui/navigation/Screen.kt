package com.example.cardpic.ui.navigation

/**
 * Простая sealed navigation для переключения экранов.
 */
sealed class Screen {
    object Home : Screen()
    data class TopicScreen(val topicId: Int, val topicTitle: String) : Screen()
    object Training : Screen()
}