package org.salih.project

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
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
    val title: String,
    val fileName: String? = null
)

@Serializable
data class Exercise(
    val id: String,
    val type: ExerciseType,
    val context: String = "",
    val correctAnswer: String = "",
    val options: List<String> = emptyList(),
    val correctSentence: String = "",
    val shuffledWords: List<String> = emptyList()
)

@Serializable
data class AnswerRecord(
    val id: String,
    val type: ExerciseType,
    val userAnswer: String,
    val isCorrect: Boolean
)

@Serializable
data class SentenceData(
    val id: String,
    val text: String,
    val blankWord: String,
    val distractors: List<String>
)

@Serializable
data class QuizMetadata(
    val level: String,
    val category: String,
    val subject: String,
    val title: String,
    val language: String = "nl"
)

@Serializable
data class QuizFile(
    val metadata: QuizMetadata,
    val exercises: List<RawExercise>
)

@Serializable
data class RawExercise(
    val id: Int? = null,
    @SerialName("question_id") val questionId: Int? = null,
    val sentence: String,
    val options: List<String>,
    @SerialName("correct_answer") val correctAnswer: String
)
