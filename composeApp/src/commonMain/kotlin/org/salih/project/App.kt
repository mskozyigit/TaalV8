package org.salih.project

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

