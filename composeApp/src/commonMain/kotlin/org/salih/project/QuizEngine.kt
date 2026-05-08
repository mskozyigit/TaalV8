package org.salih.project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

fun generateExercises(sentences: List<SentenceData>): List<Exercise> {
    return sentences.mapIndexed { index, s ->
        val isBlankType = index % 2 == 0
        if (isBlankType) {
            val context = s.text.replaceFirst(s.blankWord, "___").replaceFirstChar { it.uppercase() }
            Exercise(
                id = s.id,
                type = ExerciseType.FILL_IN_THE_BLANK,
                context = context,
                correctAnswer = s.blankWord,
                options = (s.distractors + s.blankWord).shuffled()
            )
        } else {
            val text = s.text.replaceFirstChar { it.uppercase() }
            val words = text.split(" ").filter { it.isNotEmpty() }
            Exercise(
                id = s.id,
                type = ExerciseType.HUSSELAAR,
                correctSentence = text,
                shuffledWords = words.shuffled()
            )
        }
    }
}

@Composable
fun QuizModule(
    sentences: List<SentenceData>,
    category: Category,
    onBack: () -> Unit
) {
    val exercises = remember(sentences) { generateExercises(sentences) }
    QuizContent(
        exercises = exercises,
        category = category,
        onBack = onBack
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuizContent(exercises: List<Exercise>, category: Category, onBack: () -> Unit) {
    var currentIndex by remember { mutableStateOf(0) }
    var showSummary by remember { mutableStateOf(false) }
    val results = remember { mutableStateListOf<AnswerRecord>() }
    val currentExercise = exercises[currentIndex]
    
    var selectedAnswer by remember(currentIndex) { mutableStateOf<String?>(null) }
    var husselaarWords by remember(currentIndex) { mutableStateOf(listOf<String>()) }
    var isAnswered by remember(currentIndex) { mutableStateOf(false) }
    var isCorrect by remember(currentIndex) { mutableStateOf(false) }
    var resultRecorded by remember(currentIndex) { mutableStateOf(false) }

    LaunchedEffect(isAnswered, isCorrect) {
        if (isAnswered && !resultRecorded) {
            val userAnswerStr = when (currentExercise.type) {
                ExerciseType.FILL_IN_THE_BLANK -> selectedAnswer ?: ""
                ExerciseType.HUSSELAAR -> husselaarWords.joinToString(" ")
            }
            results.add(
                AnswerRecord(
                    id = currentExercise.id,
                    type = currentExercise.type,
                    userAnswer = userAnswerStr,
                    isCorrect = isCorrect
                )
            )
            resultRecorded = true
        }
        if (isAnswered && isCorrect) {
            delay(1200)
            if (currentIndex < exercises.size - 1) {
                currentIndex++
            } else {
                showSummary = true
            }
        }
    }

    if (showSummary) {
        QuizSummaryView(
            exercises = exercises,
            results = results,
            category = category,
            onDone = onBack
        )
        return
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LinearProgressIndicator(
            progress = { (currentIndex + 1).toFloat() / exercises.size },
            modifier = Modifier.fillMaxWidth().height(12.dp).clip(CircleShape),
            color = category.color,
            trackColor = Color(0xFFE5E5E5)
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        when (currentExercise.type) {
            ExerciseType.FILL_IN_THE_BLANK -> {
                FillInTheBlankUI(
                    exercise = currentExercise,
                    selectedAnswer = selectedAnswer,
                    isAnswered = isAnswered,
                    categoryColor = category.color,
                    onWordClick = { word ->
                        if (!isAnswered) {
                            selectedAnswer = word
                            if (word == currentExercise.correctAnswer) {
                                isCorrect = true
                                isAnswered = true
                            }
                        }
                    },
                    onRemoveAnswer = {
                        if (!isAnswered) {
                            selectedAnswer = null
                        }
                    }
                )
            }
            ExerciseType.HUSSELAAR -> {
                HusselaarUI(
                    exercise = currentExercise,
                    userWords = husselaarWords,
                    isAnswered = isAnswered,
                    categoryColor = category.color,
                    onWordClick = { word, fromOptions ->
                        if (!isAnswered) {
                            if (fromOptions) {
                                husselaarWords = husselaarWords + word
                            } else {
                                husselaarWords = husselaarWords - word
                            }
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        if (isAnswered) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isCorrect) Color(0xFFD7FFB8) else Color(0xFFFFDFE0))
                    .padding(24.dp)
            ) {
                Column {
                    Text(
                        text = if (isCorrect) "GOED BEZIG!" else "FOUT!",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        color = if (isCorrect) Color(0xFF58A700) else Color(0xFFEA2B2B)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    DuolingoButton(
                        text = if (currentIndex < exercises.size - 1) "Volgende" else "Klaar",
                        baseColor = if (isCorrect) Color(0xFF58CC02) else Color(0xFFFF4B4B),
                        shadowColor = if (isCorrect) Color(0xFF46A302) else Color(0xFFD13B3B),
                        onClick = {
                            if (!resultRecorded) {
                                val userAnswerStr = when (currentExercise.type) {
                                    ExerciseType.FILL_IN_THE_BLANK -> selectedAnswer ?: ""
                                    ExerciseType.HUSSELAAR -> husselaarWords.joinToString(" ")
                                }
                                results.add(
                                    AnswerRecord(
                                        id = currentExercise.id,
                                        type = currentExercise.type,
                                        userAnswer = userAnswerStr,
                                        isCorrect = isCorrect
                                    )
                                )
                                resultRecorded = true
                            }
                            if (currentIndex < exercises.size - 1) {
                                currentIndex++
                            } else {
                                showSummary = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        isUppercase = false
                    )
                }
            }
        } else if (currentExercise.type == ExerciseType.FILL_IN_THE_BLANK && selectedAnswer != null) {
            DuolingoButton(
                text = "Controleer",
                baseColor = category.color,
                shadowColor = category.shadowColor,
                onClick = {
                    isAnswered = true
                    isCorrect = selectedAnswer == currentExercise.correctAnswer
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                isUppercase = false
            )
        } else if (currentExercise.type == ExerciseType.HUSSELAAR && husselaarWords.size == currentExercise.shuffledWords.size) {
            DuolingoButton(
                text = "Controleer",
                baseColor = category.color,
                shadowColor = category.shadowColor,
                onClick = {
                    isAnswered = true
                    isCorrect = husselaarWords.joinToString(" ") == currentExercise.correctSentence
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                isUppercase = false
            )
        }
    }
}
