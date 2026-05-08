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
                                categoryColor = selectedCategory?.color ?: Color(0xFF58CC02),
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
fun LessonScreen(lesson: Lesson, categoryColor: Color, onHome: () -> Unit, onBack: () -> Unit) {
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
                            color = categoryColor
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        NavIconButton(
                            isHome = false,
                            onClick = onBack,
                            color = categoryColor
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
            // Quiz mock data
            val mockExercises = remember {
                listOf(
                    Exercise("1", ExerciseType.FILL_IN_THE_BLANK, "Ik ___ een appel.", "eet", listOf("eet", "drink", "ben", "heb")),
                    Exercise("2", ExerciseType.HUSSELAAR, "", "", emptyList(), "Ik wil naar huis gaan.", listOf("huis", "Ik", "gaan.", "wil", "naar")),
                    Exercise("3", ExerciseType.FILL_IN_THE_BLANK, "Jij ___ erg aardig.", "bent", listOf("bent", "is", "zijn", "heb")),
                    Exercise("4", ExerciseType.HUSSELAAR, "", "", emptyList(), "De auto is erg snel.", listOf("is", "De", "snel.", "auto", "erg")),
                    Exercise("5", ExerciseType.FILL_IN_THE_BLANK, "Wij ___ in Amsterdam.", "wonen", listOf("wonen", "werkt", "slaapt", "loopt")),
                    Exercise("6", ExerciseType.HUSSELAAR, "", "", emptyList(), "Het regent vandaag veel.", listOf("veel.", "Het", "vandaag", "regent")),
                    Exercise("7", ExerciseType.FILL_IN_THE_BLANK, "Hoe ___ het met jou?", "gaat", listOf("gaat", "is", "ben", "hebt")),
                    Exercise("8", ExerciseType.HUSSELAAR, "", "", emptyList(), "Ik spreek bir beetje Nederlands.", listOf("bir", "Ik", "Nederlands.", "beetje", "spreek")),
                    Exercise("9", ExerciseType.FILL_IN_THE_BLANK, "Zij ___ naar de muziek.", "luistert", listOf("luistert", "kijkt", "leest", "eet")),
                    Exercise("10", ExerciseType.HUSSELAAR, "", "", emptyList(), "Morgen gaan we naar zee.", listOf("we", "Morgen", "zee.", "gaan", "naar"))
                )
            }

            if (lesson.id == "a2_1") {
                QuizContent(
                    exercises = mockExercises,
                    categoryColor = categoryColor,
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuizContent(exercises: List<Exercise>, categoryColor: Color, onBack: () -> Unit) {
    var currentIndex by remember { mutableStateOf(0) }
    val currentExercise = exercises[currentIndex]
    
    var selectedAnswer by remember(currentIndex) { mutableStateOf<String?>(null) }
    var husselaarWords by remember(currentIndex) { mutableStateOf(listOf<String>()) }
    var isAnswered by remember(currentIndex) { mutableStateOf(false) }
    var isCorrect by remember(currentIndex) { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress bar
        LinearProgressIndicator(
            progress = { (currentIndex + 1).toFloat() / exercises.size },
            modifier = Modifier.fillMaxWidth().height(12.dp).clip(CircleShape),
            color = categoryColor,
            trackColor = Color(0xFFE5E5E5)
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        when (currentExercise.type) {
            ExerciseType.FILL_IN_THE_BLANK -> {
                FillInTheBlankUI(
                    exercise = currentExercise,
                    selectedAnswer = selectedAnswer,
                    isAnswered = isAnswered,
                    onWordClick = { word ->
                        if (!isAnswered) {
                            selectedAnswer = word
                            isCorrect = word == currentExercise.correctAnswer
                            isAnswered = true
                        }
                    }
                )
            }
            ExerciseType.HUSSELAAR -> {
                HusselaarUI(
                    exercise = currentExercise,
                    userWords = husselaarWords,
                    isAnswered = isAnswered,
                    onWordClick = { word, fromOptions ->
                        if (!isAnswered) {
                            if (fromOptions) {
                                husselaarWords = husselaarWords + word
                            } else {
                                husselaarWords = husselaarWords - word
                            }
                            
                            val currentSentence = husselaarWords.joinToString(" ")
                            if (currentSentence == currentExercise.correctSentence) {
                                isCorrect = true
                                isAnswered = true
                            }
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Feedback section
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
                        text = if (currentIndex < exercises.size - 1) "VOLGENDE" else "KLAAR",
                        baseColor = if (isCorrect) Color(0xFF58CC02) else Color(0xFFFF4B4B),
                        shadowColor = if (isCorrect) Color(0xFF46A302) else Color(0xFFD13B3B),
                        onClick = {
                            if (currentIndex < exercises.size - 1) {
                                currentIndex++
                            } else {
                                onBack()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FillInTheBlankUI(
    exercise: Exercise,
    selectedAnswer: String?,
    isAnswered: Boolean,
    onWordClick: (String) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Vul de lege plek in",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4B4B4B)
        )
        Spacer(modifier = Modifier.height(48.dp))
        
        // Sentence with blank
        val parts = exercise.context.split("___")
        FlowRow(
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.Center
        ) {
            Text(parts[0], fontSize = 24.sp, fontWeight = FontWeight.Medium)
            
            Box(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .widthIn(min = 80.dp)
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
                if (selectedAnswer != null) {
                    Text(
                        selectedAnswer,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isAnswered) (if (selectedAnswer == exercise.correctAnswer) Color(0xFF58CC02) else Color.Red) else Color.Black
                    )
                } else {
                    Text(" ", fontSize = 24.sp)
                }
            }
            
            if (parts.size > 1) {
                Text(parts[1], fontSize = 24.sp, fontWeight = FontWeight.Medium)
            }
        }

        Spacer(modifier = Modifier.height(64.dp))

        // Options
        FlowRow(
            horizontalArrangement = Arrangement.Center,
            maxItemsInEachRow = 2
        ) {
            exercise.options.forEach { option ->
                DuolingoButton(
                    text = option,
                    baseColor = Color.White,
                    shadowColor = Color(0xFFE5E5E5),
                    textColor = Color(0xFF4B4B4B),
                    onClick = { onWordClick(option) },
                    modifier = Modifier.padding(8.dp).width(140.dp),
                    height = 50.dp,
                    fontSize = 16.sp
                )
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
    onWordClick: (String, Boolean) -> Unit
) {
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
                userWords.forEach { word ->
                    DuolingoButton(
                        text = word,
                        baseColor = Color.White,
                        shadowColor = Color(0xFFE5E5E5),
                        textColor = Color(0xFF4B4B4B),
                        onClick = { onWordClick(word, false) },
                        modifier = Modifier.padding(4.dp).widthIn(min = 60.dp),
                        height = 45.dp,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(64.dp))

        // Available words (Options)
        FlowRow(horizontalArrangement = Arrangement.Center) {
            val availableWords = exercise.shuffledWords.filter { word ->
                val countInOptions = exercise.shuffledWords.count { it == word }
                val countInUser = userWords.count { it == word }
                countInUser < countInOptions
            }
            
            exercise.shuffledWords.forEach { word ->
                val isUsed = userWords.contains(word) && 
                    userWords.count { it == word } >= exercise.shuffledWords.count { it == word }
                
                // Keep the button there but make it "empty" or disabled if used
                Box(modifier = Modifier.padding(4.dp).widthIn(min = 60.dp).height(45.dp)) {
                    if (!isUsed) {
                        DuolingoButton(
                            text = word,
                            baseColor = Color.White,
                            shadowColor = Color(0xFFE5E5E5),
                            textColor = Color(0xFF4B4B4B),
                            onClick = { onWordClick(word, true) },
                            modifier = Modifier.fillMaxSize(),
                            height = 45.dp,
                            fontSize = 14.sp
                        )
                    } else {
                        // Placeholder
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFFE5E5E5), RoundedCornerShape(12.dp))
                        )
                    }
                }
            }
        }
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
    fontSize: androidx.compose.ui.unit.TextUnit = 18.sp
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
        contentAlignment = Alignment.BottomCenter
    ) {
        // Shadow layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 4.dp)
                .background(shadowColor, RoundedCornerShape(16.dp))
        )
        
        // Top layer (Button surface)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 4.dp)
                .offset(y = translationY)
                .background(baseColor, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text.uppercase(),
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
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