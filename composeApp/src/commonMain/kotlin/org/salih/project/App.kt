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
            Category("c1", "Basis Zinnen", Color(0xFF58CC02), Color(0xFF46A302)),
            Category("c2", "Vragen Stellen", Color(0xFF1CB0F6), Color(0xFF1899D6)),
            Category("c3", "Werkwoorden & Tijden", Color(0xFFCE82FF), Color(0xFFA568CC)),
            Category("c4", "Verbindingswoorden", Color(0xFFF96060), Color(0xFFD64A4A)),
            Category("c5", "Zinsstructuur", Color(0xFFFFC107), Color(0xFFE5AD06))
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
                when (category.id) {
                    "c1" -> listOf(
                        Lesson("c1_1", "De Basis Zin", "N1_C1_S1_De_Basis_Zin.json"),
                        Lesson("c1_2", "Inversie", "N1_C1_S2_Inversie.json"),
                        Lesson("c1_3", "Herhaling", "N1_C1_S3_Herhaling.json")
                    )
                    "c2" -> listOf(
                        Lesson("c2_1", "Ja / Nee Vragen", "N1_C2_S1_Ja_Nee_Vragen.json"),
                        Lesson("c2_2", "Vraagwoord Vragen", "N1_C2_S2_Vraagwoord_Vragen.json"),
                        Lesson("c2_3", "Herhaling", "N1_C2_S3_Herhaling.json")
                    )
                    "c3" -> listOf(
                        Lesson("c3_1", "Modale Werkwoorden", "N1_C3_S1_Modale_Werkwoorden.json"),
                        Lesson("c3_2", "Voltooid Tegenwoordig", "N1_C3_S2_Voltooid_Tegenwoordige_Tijd.json"),
                        Lesson("c3_3", "Herhaling", "N1_C3_S3_Herhaling.json")
                    )
                    "c4" -> listOf(
                        Lesson("c4_1", "Nevenschikkend", "N1_C4_S1_Nevenschikkende_Voegwoorden.json"),
                        Lesson("c4_2", "Aan Het + Infinitief", "N1_C4_S2_Aan_Het_Infinitief.json"),
                        Lesson("c4_3", "Om Te + Infinitief", "N1_C4_S3_Om_Te_Infinitief.json"),
                        Lesson("c4_4", "Herhaling", "N1_C4_S4_Herhaling.json")
                    )
                    "c5" -> listOf(
                        Lesson("c5_1", "Inversie (Extra)", "N1_5_S1_Inversie.json"),
                        Lesson("c5_2", "Bijzin", "N1_5_S2_Bijzin.json")
                    )
                    else -> emptyList()
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
    var sentences by remember(lesson.id) { mutableStateOf<List<SentenceData>?>(null) }
    var isLoading by remember(lesson.id) { mutableStateOf(true) }

    LaunchedEffect(lesson.id) {
        isLoading = true
        val fileName = lesson.fileName
        if (fileName != null) {
            val all = loadExercisesFromJson(fileName)
            // Her girişte farklı soru havuzu için karıştır ve 20 tane al
            sentences = all.shuffled().take(20)
        }
        isLoading = false
    }

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
            if (isLoading) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = category.color)
                    Spacer(Modifier.height(16.dp))
                    Text("Laden...", color = Color.Gray)
                }
            } else {
                val currentSentences = sentences
                if (currentSentences != null && currentSentences.isNotEmpty()) {
                    QuizModule(
                        sentences = currentSentences,
                        category = category,
                        onBack = onBack
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Geen oefeningen gevonden",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4B4B4B)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Probeer het later opnieuw.",
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

