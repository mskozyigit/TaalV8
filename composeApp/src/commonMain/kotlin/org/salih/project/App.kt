package org.salih.project

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Canvas
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class Screen {
    HOME, CATEGORY_DETAIL, LESSON_PAGE
}

enum class ExerciseType {
    FILL_IN_THE_BLANK,
    HUSSELAAR
}

data class Exercise(
    val id: String,
    val type: ExerciseType,
    val context: String = "",
    val correctAnswer: String = "",
    val options: List<String> = emptyList(),
    val correctSentence: String = "",
    val shuffledWords: List<String> = emptyList()
)

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

fun generateExercises(sentences: List<SentenceData>): List<Exercise> {
    return sentences.mapIndexed { index, s ->
        // Alternate between types for variety
        val isBlankType = index % 2 == 0
        if (isBlankType) {
            Exercise(
                id = s.id,
                type = ExerciseType.FILL_IN_THE_BLANK,
                context = s.text.replaceFirst(s.blankWord, "___"),
                correctAnswer = s.blankWord,
                options = (s.distractors + s.blankWord).shuffled()
            )
        } else {
            // Split sentence into words, maintaining punctuation
            val words = s.text.split(" ").filter { it.isNotEmpty() }
            Exercise(
                id = s.id,
                type = ExerciseType.HUSSELAAR,
                correctSentence = s.text,
                shuffledWords = words.shuffled()
            )
        }
    }
}

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedLesson by remember { mutableStateOf<Lesson?>(null) }

    val categories = remember {
        listOf(
            Category("a2", "Niveau A2", Color(0xFF58CC02), Color(0xFF46A302)),
            Category("b1", "Niveau B1", Color(0xFF1CB0F6), Color(0xFF1899D6)),
            Category("b2", "Niveau B2", Color(0xFFCE82FF), Color(0xFFA568CC))
        )
    }

    // Hide the HTML loading bar when Compose is ready
    LaunchedEffect(Unit) {
        hideLoadingBar()
    }

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                }
            ) { screen ->
                when (screen) {
                    Screen.HOME -> HomeScreen(
                        categories = categories,
                        onCategoryClick = { 
                            selectedCategory = it
                            currentScreen = Screen.CATEGORY_DETAIL
                        }
                    )
                    Screen.CATEGORY_DETAIL -> {
                        selectedCategory?.let { category ->
                    CategoryDetailScreen(
                        category = category,
                        onHome = { currentScreen = Screen.HOME },
                        onBack = { currentScreen = Screen.HOME },
                        onLessonClick = { lesson ->
                            selectedLesson = lesson
                            currentScreen = Screen.LESSON_PAGE
                        }
                    )
                        }
                    }
                    Screen.LESSON_PAGE -> {
                        selectedLesson?.let { lesson ->
                            LessonScreen(
                                lesson = lesson,
                                category = selectedCategory ?: categories[0],
                                onHome = { currentScreen = Screen.HOME },
                                onBack = { currentScreen = Screen.CATEGORY_DETAIL }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(categories: List<Category>, onCategoryClick: (Category) -> Unit) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        
        Text(
            text = "Kies je niveau",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF4B4B4B),
            modifier = Modifier.padding(bottom = 48.dp)
        )

        categories.forEach { category ->
            DuolingoButton(
                text = category.title,
                baseColor = category.color,
                shadowColor = category.shadowColor,
                onClick = { onCategoryClick(category) },
                modifier = Modifier.widthIn(min = 280.dp, max = 400.dp).fillMaxWidth(0.9f)
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(
    category: Category,
    onHome: () -> Unit,
    onBack: () -> Unit,
    onLessonClick: (Lesson) -> Unit
) {
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(category.title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        NavIconButton(
                            isHome = true,
                            onClick = onHome,
                            color = category.color
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        NavIconButton(
                            isHome = false,
                            onClick = onBack,
                            color = category.color
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            val lessons = remember(category) {
                (1..5).map { 
                    val title = if (category.id == "a2" && it == 1) "Eerste Group" else "Oefening $it"
                    Lesson("${category.id}_$it", title)
                }
            }

            lessons.forEach { lesson ->
                DuolingoButton(
                    text = lesson.title,
                    baseColor = category.color,
                    shadowColor = category.shadowColor,
                    onClick = { onLessonClick(lesson) },
                    modifier = Modifier.widthIn(min = 280.dp, max = 400.dp).fillMaxWidth(0.9f)
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(lesson: Lesson, category: Category, onHome: () -> Unit, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(lesson.title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        NavIconButton(
                            isHome = true,
                            onClick = onHome,
                            color = category.color
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        NavIconButton(
                            isHome = false,
                            onClick = onBack,
                            color = category.color
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            // --- DATA MODULE START ---
            // In the future, this data can be loaded from a JSON file based on lesson.id
            val rawSentences = remember(lesson.id) {
                when (lesson.id) {
                    "a2_1" -> listOf(
                        SentenceData("1", "Ik eet een appel.", "eet", listOf("drink", "ben", "heb")),
                        SentenceData("2", "Ik wil naar huis gaan.", "wil", listOf("kan", "zal", "moet")),
                        SentenceData("3", "Jij bent erg aardig.", "bent", listOf("is", "zijn", "heb")),
                        SentenceData("4", "De auto is erg snel.", "is", listOf("was", "wordt", "lijkt")),
                        SentenceData("5", "Wij wonen in Amsterdam.", "wonen", listOf("werkt", "slaapt", "loopt")),
                        SentenceData("6", "Het regent vandaag veel.", "vandaag", listOf("gisteren", "morgen", "nooit")),
                        SentenceData("7", "Hoe gaat het met jou?", "gaat", listOf("is", "ben", "hebt")),
                        SentenceData("8", "Ik spreek een beetje Nederlands.", "beetje", listOf("veel", "alles", "niets")),
                        SentenceData("9", "Zij luistert naar de muziek.", "luistert", listOf("kijkt", "leest", "eet")),
                        SentenceData("10", "Morgen gaan we naar zee.", "gaan", listOf("komen", "staan", "zitten"))
                    )
                    // Add more lessons here: "a2_2", "b1_1", etc.
                    else -> emptyList()
                }
            }
            // --- DATA MODULE END ---

            if (rawSentences.isNotEmpty()) {
                QuizModule(
                    sentences = rawSentences,
                    category = category,
                    onBack = onBack
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Je bent nu bij ${lesson.title}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4B4B4B)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Laten we beginnen met leren!",
                        fontSize = 18.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    DuolingoButton(
                        text = "Terug",
                        baseColor = Color(0xFF58CC02),
                        shadowColor = Color(0xFF46A302),
                        onClick = onBack,
                        modifier = Modifier.width(280.dp)
                    )
                }
            }
        }
    }
}

/**
 * REPLICABLE TEST MODULE
 * This component can be used anywhere to start a quiz with a list of sentences.
 */
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

    // Auto-advance for correct answers and record results
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
            kotlinx.coroutines.delay(1200) // Give time to see "GOED BEZIG"
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
        // Progress bar
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
                            // Check if correct for auto-advance
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

        // Feedback section or Check Button
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
            // Manual check for wrong answers
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
            // Show check when all words are placed
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FillInTheBlankUI(
    exercise: Exercise,
    selectedAnswer: String?,
    isAnswered: Boolean,
    categoryColor: Color,
    onWordClick: (String) -> Unit,
    onRemoveAnswer: () -> Unit
) {
    val lightColors = remember(categoryColor) { getLightColors(categoryColor) }
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Vul de lege plek in",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4B4B4B)
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        // Sentence with blank
        val parts = exercise.context.split("___")
        val isBlankAtStart = parts[0].trim().isEmpty()
        
        FlowRow(
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
        ) {
            if (parts[0].isNotEmpty()) {
                Text(
                    parts[0], 
                    fontSize = 24.sp, 
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
            
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .widthIn(min = 80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(enabled = selectedAnswer != null && !isAnswered) { onRemoveAnswer() }
                    .drawBehind {
                        val strokeWidth = 2.dp.toPx()
                        val y = size.height - 4.dp.toPx()
                        drawLine(
                            color = if (selectedAnswer != null) Color.Transparent else Color.LightGray,
                            start = androidx.compose.ui.geometry.Offset(0f, y),
                            end = androidx.compose.ui.geometry.Offset(size.width, y),
                            strokeWidth = strokeWidth
                        )
                    }
                    .align(Alignment.CenterVertically),
                contentAlignment = Alignment.Center
            ) {
                if (selectedAnswer != null) {
                    QuizWordButton(
                        text = selectedAnswer.adjustCase(isBlankAtStart),
                        baseColor = if (isAnswered) (if (selectedAnswer == exercise.correctAnswer) Color(0xFF58CC02) else Color(0xFFFF4B4B)) else lightColors.first,
                        shadowColor = if (isAnswered) (if (selectedAnswer == exercise.correctAnswer) Color(0xFF46A302) else Color(0xFFD13B3B)) else lightColors.second,
                        textColor = if (isAnswered) Color.White else categoryColor,
                        onClick = { if (!isAnswered) onRemoveAnswer() },
                        modifier = Modifier.padding(horizontal = 4.dp),
                        height = 40.dp,
                        fontSize = 16.sp
                    )
                } else {
                    Text(" ", fontSize = 24.sp, modifier = Modifier.padding(vertical = 8.dp))
                }
            }
            
            if (parts.size > 1 && parts[1].isNotEmpty()) {
                Text(
                    parts[1], 
                    fontSize = 24.sp, 
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Options
        FlowRow(
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            exercise.options.forEach { option ->
                val isSelected = selectedAnswer == option
                
                Box(modifier = Modifier.padding(6.dp)) {
                    if (!isSelected || isAnswered) {
                        QuizWordButton(
                            text = option.adjustCase(isBlankAtStart),
                            baseColor = lightColors.first,
                            shadowColor = lightColors.second,
                            textColor = categoryColor,
                            onClick = { onWordClick(option) },
                            height = 45.dp,
                            fontSize = 16.sp
                        )
                    } else {
                        // Placeholder with estimated size of the word
                        Box(
                            modifier = Modifier
                                .height(45.dp)
                                .widthIn(min = 60.dp)
                                .background(Color(0xFFE5E5E5), RoundedCornerShape(12.dp))
                                .padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HusselaarUI(
    exercise: Exercise,
    userWords: List<String>,
    isAnswered: Boolean,
    categoryColor: Color,
    onWordClick: (String, Boolean) -> Unit
) {
    val lightColors = remember(categoryColor) { getLightColors(categoryColor) }
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Maak de zin compleet",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4B4B4B)
        )
        Spacer(modifier = Modifier.height(48.dp))

        // Selected words area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp)
                .padding(8.dp)
                .drawBehind {
                    val strokeWidth = 2.dp.toPx()
                    val y = size.height - strokeWidth
                    drawLine(
                        color = Color.LightGray,
                        start = androidx.compose.ui.geometry.Offset(0f, y),
                        end = androidx.compose.ui.geometry.Offset(size.width, y),
                        strokeWidth = strokeWidth
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            FlowRow(horizontalArrangement = Arrangement.Center) {
                userWords.forEachIndexed { index, word ->
                    QuizWordButton(
                        text = word.adjustCase(index == 0),
                        baseColor = if (isAnswered) Color(0xFF58CC02) else lightColors.first,
                        shadowColor = if (isAnswered) Color(0xFF46A302) else lightColors.second,
                        textColor = if (isAnswered) Color.White else categoryColor,
                        onClick = { onWordClick(word, false) },
                        modifier = Modifier.padding(4.dp),
                        height = 38.dp,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(64.dp))

        // Available words (Options)
        FlowRow(horizontalArrangement = Arrangement.Center) {
            exercise.shuffledWords.forEachIndexed { index, word ->
                val countInUser = userWords.count { it == word }
                val countBeforeInShuffled = exercise.shuffledWords.take(index).count { it == word }
                val isUsed = countInUser > countBeforeInShuffled
                
                Box(modifier = Modifier.padding(4.dp)) {
                    if (!isUsed) {
                        QuizWordButton(
                            text = word.lowercase(), // Options usually lowercase for natural feel
                            baseColor = lightColors.first,
                            shadowColor = lightColors.second,
                            textColor = categoryColor,
                            onClick = { onWordClick(word, true) },
                            height = 40.dp,
                            fontSize = 14.sp
                        )
                    } else {
                        // Placeholder
                        Box(
                            modifier = Modifier
                                .height(40.dp)
                                .widthIn(min = 40.dp)
                                .background(Color(0xFFE5E5E5), RoundedCornerShape(12.dp))
                        )
                    }
                }
            }
        }
    }
}

fun getLightColors(base: Color): Pair<Color, Color> {
    return when (base.value.toLong()) {
        Color(0xFF58CC02).value.toLong() -> Color(0xFFD7FFB8) to Color(0xFFB8E695) // Green
        Color(0xFF1CB0F6).value.toLong() -> Color(0xFFD1F1FF) to Color(0xFFAADCF5) // Blue
        Color(0xFFCE82FF).value.toLong() -> Color(0xFFF0D9FF) to Color(0xFFD4AFFF) // Purple
        else -> Color(0xFFF7F7F7) to Color(0xFFE5E5E5)
    }
}

fun String.adjustCase(isFirst: Boolean): String {
    if (this.isEmpty()) return this
    return if (isFirst) {
        this.replaceFirstChar { it.uppercase() }
    } else {
        this.lowercase()
    }
}

@Composable
fun DuolingoButton(
    text: String,
    baseColor: Color,
    shadowColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = Color.White,
    height: androidx.compose.ui.unit.Dp = 60.dp,
    fontSize: androidx.compose.ui.unit.TextUnit = 18.sp,
    isUppercase: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val translationY by animateDpAsState(targetValue = if (isPressed) 4.dp else 0.dp)
    
    Box(
        modifier = modifier
            .height(height)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // Shadow layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(top = 4.dp)
                .background(shadowColor, RoundedCornerShape(16.dp))
        )
        
        // Top layer (Button surface)
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(bottom = 4.dp)
                .offset(y = translationY)
                .background(baseColor, RoundedCornerShape(16.dp))
        )

        // Content
        Text(
            text = if (isUppercase) text.uppercase() else text,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            color = textColor,
            softWrap = false,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 4.dp)
                .offset(y = translationY)
        )
    }
}

@Composable
fun QuizWordButton(
    text: String,
    baseColor: Color,
    shadowColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = Color.White,
    height: androidx.compose.ui.unit.Dp = 45.dp,
    fontSize: androidx.compose.ui.unit.TextUnit = 16.sp,
    isUppercase: Boolean = false,
    horizontalPadding: androidx.compose.ui.unit.Dp = 12.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val translationY by animateDpAsState(targetValue = if (isPressed) 3.dp else 0.dp)
    
    Box(
        modifier = modifier
            .height(height)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // Shadow layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(top = 3.dp)
                .background(shadowColor, RoundedCornerShape(12.dp))
        )
        
        // Top layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(bottom = 3.dp)
                .offset(y = translationY)
                .background(baseColor, RoundedCornerShape(12.dp))
        )

        Text(
            text = if (isUppercase) text.uppercase() else text,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            color = textColor,
            softWrap = false,
            modifier = Modifier
                .padding(horizontal = horizontalPadding)
                .padding(bottom = 3.dp)
                .offset(y = translationY)
        )
    }
}

@Composable
fun NavIconButton(
    isHome: Boolean,
    onClick: () -> Unit,
    color: Color,
    modifier: Modifier = Modifier
) {
    FilledIconButton(
        onClick = onClick,
        modifier = modifier.size(44.dp),
        shape = CircleShape,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = color,
            contentColor = Color.White
        )
    ) {
        Canvas(modifier = Modifier.size(20.dp)) {
            if (isHome) {
                // Simple House Icon
                val path = Path().apply {
                    moveTo(size.width * 0.5f, size.height * 0.1f)
                    lineTo(size.width * 0.1f, size.height * 0.5f)
                    lineTo(size.width * 0.1f, size.height * 0.9f)
                    lineTo(size.width * 0.9f, size.height * 0.9f)
                    lineTo(size.width * 0.9f, size.height * 0.5f)
                    close()
                }
                drawPath(path, color = Color.White)
                // Door
                drawRect(
                    color = color,
                    topLeft = androidx.compose.ui.geometry.Offset(size.width * 0.4f, size.height * 0.6f),
                    size = androidx.compose.ui.geometry.Size(size.width * 0.2f, size.height * 0.3f)
                )
            } else {
                // Simple Back Arrow Icon
                val path = Path().apply {
                    moveTo(size.width * 0.85f, size.height * 0.5f)
                    lineTo(size.width * 0.15f, size.height * 0.5f)
                    moveTo(size.width * 0.15f, size.height * 0.5f)
                    lineTo(size.width * 0.45f, size.height * 0.2f)
                    moveTo(size.width * 0.15f, size.height * 0.5f)
                    lineTo(size.width * 0.45f, size.height * 0.8f)
                }
                drawPath(
                    path = path,
                    color = Color.White,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }
    }
}

@Composable
fun QuizSummaryView(
    exercises: List<Exercise>,
    results: List<AnswerRecord>,
    category: Category,
    onDone: () -> Unit
) {
    val total = exercises.size
    val correctCount = results.count { it.isCorrect }
    val wrongCount = total - correctCount

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Resultaten",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF4B4B4B)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Goed: $correctCount  |  Fout: $wrongCount",
            fontSize = 18.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Basit tablo başlıkları
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("#", fontWeight = FontWeight.Bold, modifier = Modifier.width(30.dp))
            Text("Zin", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f).padding(horizontal = 8.dp))
            Text("Resultaat", fontWeight = FontWeight.Bold, modifier = Modifier.width(80.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Sonuç satırları
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            exercises.forEachIndexed { index, ex ->
                val rec = results.getOrNull(index)
                val ok = rec?.isCorrect == true
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp, horizontal = 8.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (ok) Color(0xFFD7FFB8) else Color(0xFFFFDFE0))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${index + 1}", modifier = Modifier.width(30.dp))
                    Text(
                        text = if (ex.type == ExerciseType.FILL_IN_THE_BLANK) ex.context else ex.correctSentence,
                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                        fontSize = 14.sp
                    )
                    Text(
                        text = if (ok) "Goed" else "Fout",
                        fontWeight = FontWeight.SemiBold,
                        color = if (ok) Color(0xFF58A700) else Color(0xFFEA2B2B),
                        modifier = Modifier.width(80.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        DuolingoButton(
            text = "Terug",
            baseColor = category.color,
            shadowColor = category.shadowColor,
            onClick = onDone,
            modifier = Modifier.fillMaxWidth(),
            isUppercase = false
        )
    }
}
