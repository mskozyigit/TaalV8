package org.salih.project

import androidx.compose.ui.graphics.Color

enum class ExerciseType {
    FILL_IN_THE_BLANK,
    HUSSELAAR
}

data class Category(
    val id: String,
    val title: String,
    val color: Color,
    val shadowColor: Color
)

data class Lesson(
    val id: String,
    val title: String
)

data class Exercise(
    val id: String,
    val type: ExerciseType,
    val context: String = "",
    val correctAnswer: String = "",
    val options: List<String> = emptyList(),
    val correctSentence: String = "",
    val shuffledWords: List<String> = emptyList()
)

data class AnswerRecord(
    val id: String,
    val type: ExerciseType,
    val userAnswer: String,
    val isCorrect: Boolean
)

data class SentenceData(
    val id: String,
    val text: String,
    val blankWord: String,
    val distractors: List<String>
)
