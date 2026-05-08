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
    LEVEL_SELECTION, CATEGORY_SELECTION, LESSON_SELECTION, QUIZ
}

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf(Screen.LEVEL_SELECTION) }
    var selectedLevel by remember { mutableStateOf<Level?>(null) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedLesson by remember { mutableStateOf<Lesson?>(null) }

    val levels = remember {
        listOf(
            Level("n1", "Niveau 1 (A2)", Color(0xFF58CC02), Color(0xFF46A302)),
            Level("n2", "Niveau 2 (B1)", Color(0xFF1CB0F6), Color(0xFF1899D6)),
            Level("n3", "Niveau 3 (B2)", Color(0xFFCE82FF), Color(0xFFA568CC))
        )
    }

    val categories = remember {
        listOf(
            Category("c1", "Werkwoord Eerste", Color(0xFF58CC02), Color(0xFF46A302)),
            Category("c2", "Werkwoord Vragen", Color(0xFF1CB0F6), Color(0xFF1899D6)),
            Category("c3", "Werkwoord Extra", Color(0xFFCE82FF), Color(0xFFA568CC)),
            Category("c4", "Hoger Niveau", Color(0xFFF96060), Color(0xFFD64A4A)),
            Category("c5", "Eindoefeningen", Color(0xFFFFC107), Color(0xFFE5AD06))
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
                    Screen.LEVEL_SELECTION -> LevelSelectionScreen(
                        levels = levels,
                        onLevelClick = {
                            selectedLevel = it
                            currentScreen = Screen.CATEGORY_SELECTION
                        }
                    )
                    Screen.CATEGORY_SELECTION -> {
                        selectedLevel?.let { level ->
                            CategorySelectionScreen(
                                level = level,
                                categories = categories,
                                onHome = { currentScreen = Screen.LEVEL_SELECTION },
                                onBack = { currentScreen = Screen.LEVEL_SELECTION },
                                onCategoryClick = {
                                    selectedCategory = it
                                    currentScreen = Screen.LESSON_SELECTION
                                }
                            )
                        }
                    }
                    Screen.LESSON_SELECTION -> {
                        val level = selectedLevel
                        val category = selectedCategory
                        if (level != null && category != null) {
                            LessonSelectionScreen(
                                level = level,
                                category = category,
                                onHome = { currentScreen = Screen.LEVEL_SELECTION },
                                onBack = { currentScreen = Screen.CATEGORY_SELECTION },
                                onLessonClick = {
                                    selectedLesson = it
                                    currentScreen = Screen.QUIZ
                                }
                            )
                        }
                    }
                    Screen.QUIZ -> {
                        selectedLesson?.let { lesson ->
                            LessonScreen(
                                lesson = lesson,
                                category = selectedCategory ?: categories[0],
                                onHome = { currentScreen = Screen.LEVEL_SELECTION },
                                onBack = { currentScreen = Screen.LESSON_SELECTION }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LevelSelectionScreen(levels: List<Level>, onLevelClick: (Level) -> Unit) {
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

        levels.forEach { level ->
            DuolingoButton(
                text = level.title,
                baseColor = level.color,
                shadowColor = level.shadowColor,
                onClick = { onLevelClick(level) },
                modifier = Modifier.widthIn(min = 280.dp, max = 400.dp).fillMaxWidth(0.9f)
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelectionScreen(
    level: Level,
    categories: List<Category>,
    onHome: () -> Unit,
    onBack: () -> Unit,
    onCategoryClick: (Category) -> Unit
) {
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(level.title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        NavIconButton(isHome = true, onClick = onHome, color = level.color)
                        Spacer(modifier = Modifier.width(8.dp))
                        NavIconButton(isHome = false, onClick = onBack, color = level.color)
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
            categories.forEach { category ->
                DuolingoButton(
                    text = category.title,
                    baseColor = level.color,
                    shadowColor = level.shadowColor,
                    onClick = { onCategoryClick(category) },
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
fun LessonSelectionScreen(
    level: Level,
    category: Category,
    onHome: () -> Unit,
    onBack: () -> Unit,
    onLessonClick: (Lesson) -> Unit
) {
    val lessons = remember(level, category) { getLessonsFor(level, category) }
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
                        NavIconButton(isHome = true, onClick = onHome, color = level.color)
                        Spacer(modifier = Modifier.width(8.dp))
                        NavIconButton(isHome = false, onClick = onBack, color = level.color)
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
            lessons.forEach { lesson ->
                DuolingoButton(
                    text = lesson.title,
                    baseColor = level.color,
                    shadowColor = level.shadowColor,
                    onClick = { onLessonClick(lesson) },
                    modifier = Modifier.widthIn(min = 280.dp, max = 400.dp).fillMaxWidth(0.9f)
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

fun getLessonsFor(level: Level, category: Category): List<Lesson> {
    val l = level.id.uppercase()
    val c = category.id.uppercase()
    
    return when ("${l}_${c}") {
        "N1_C1" -> listOf(
            Lesson("N1_C1_S1", "De Basis Zin", "N1_C1_S1_De_Basis_Zin.json"),
            Lesson("N1_C1_S2", "Inversie", "N1_C1_S2_Inversie.json"),
            Lesson("N1_C1_S3", "Herhaling", "N1_C1_S3_Herhaling.json")
        )
        "N1_C2" -> listOf(
            Lesson("N1_C2_S1", "Ja / Nee Vragen", "N1_C2_S1_Ja_Nee_Vragen.json"),
            Lesson("N1_C2_S2", "Vraagwoord Vragen", "N1_C2_S2_Vraagwoord_Vragen.json"),
            Lesson("N1_C2_S3", "Herhaling", "N1_C2_S3_Herhaling.json")
        )
        "N1_C3" -> listOf(
            Lesson("N1_C3_S1", "Modale Werkwoorden", "N1_C3_S1_Modale_Werkwoorden.json"),
            Lesson("N1_C3_S2", "Voltooid Tegenwoordige Tijd", "N1_C3_S2_Voltooid_Tegenwoordige_Tijd.json"),
            Lesson("N1_C3_S3", "Herhaling", "N1_C3_S3_Herhaling.json")
        )
        "N1_C4" -> listOf(
            Lesson("N1_C4_S1", "Nevenschikkende Voegwoorden", "N1_C4_S1_Nevenschikkende_Voegwoorden.json"),
            Lesson("N1_C4_S2", "Aan Het Infinitief", "N1_C4_S2_Aan_Het_Infinitief.json"),
            Lesson("N1_C4_S3", "Om Te Infinitief", "N1_C4_S3_Om_Te_Infinitief.json"),
            Lesson("N1_C4_S4", "Herhaling", "N1_C4_S4_Herhaling.json")
        )
        "N1_C5" -> listOf(
            Lesson("N1_C5_S1", "Invuloefening", "N1_C5_S1_Invuloefening.json"),
            Lesson("N1_C5_S2", "Husselaar", "N1_C5_S2_Husselaar.json")
        )
        "N2_C1" -> listOf(
            Lesson("N2_C1_S1", "De Bijzin", "N2_C1_S1_De_Bijzin.json"),
            Lesson("N2_C1_S2", "Inversie in Bijzinnen", "N2_C1_S2_Inversie_In_Bijzinnen.json"),
            Lesson("N2_C1_S3", "Herhaling", "N2_C1_S3_Herhaling.json")
        )
        "N2_C2" -> listOf(
            Lesson("N2_C2_S1", "Verleden Tijd Vragen", "N2_C2_S1_Verleden_Tijd.json"),
            Lesson("N2_C2_S2", "Indirecte Vragen", "N2_C2_S2_Indirecte_Vragen.json"),
            Lesson("N2_C2_S3", "Herhaling", "N2_C2_S3_Herhaling.json")
        )
        "N2_C3" -> listOf(
            Lesson("N2_C3_S1", "Scheidbare Werkwoorden", "N2_C3_S1_Scheidbare_Werkwoorden.json"),
            Lesson("N2_C3_S2", "Te Infinitief", "N2_C3_S2_Te_Infinitief.json"),
            Lesson("N2_C3_S3", "Herhaling", "N2_C3_S3_Herhaling.json")
        )
        "N2_C4" -> listOf(
            Lesson("N2_C4_S1", "Onderschikkende Voegwoorden", "N2_C4_S1_Onderschikkende_Voegwoorden.json"),
            Lesson("N2_C4_S2", "Relatieve Bijzinnen", "N2_C4_S2_Relatieve_Bijzinnen.json"),
            Lesson("N2_C4_S3", "Herhaling", "N2_C4_S3_Herhaling.json")
        )
        "N2_C5" -> listOf(
            Lesson("N2_C5_S1", "Invuloefening", "N2_C5_S1_Invuloefening.json"),
            Lesson("N2_C5_S2", "Husselaar", "N2_C5_S2_Husselaar.json")
        )
        "N3_C1" -> listOf(
            Lesson("N3_C1_S1", "Lange Hoofdzinnen", "N3_C1_S1_Lange_Hoofdzinnen.json"),
            Lesson("N3_C1_S2", "Complexe Inversie", "N3_C1_S2_Complexe_Inversie.json"),
            Lesson("N3_C1_S3", "Herhaling", "N3_C1_S3_Herhaling.json")
        )
        "N3_C2" -> listOf(
            Lesson("N3_C2_S1", "Passieve Vragen", "N3_C2_S1_Passieve_Vragen.json"),
            Lesson("N3_C2_S2", "Hypothetische Vragen", "N3_C2_S2_Hypothetische_Vragen.json"),
            Lesson("N3_C2_S3", "Herhaling", "N3_C2_S3_Herhaling.json")
        )
        "N3_C3" -> listOf(
            Lesson("N3_C3_S1", "Plusquamperfectum", "N3_C3_S1_Plusquamperfectum.json"),
            Lesson("N3_C3_S2", "Dubbele Infinitief", "N3_C3_S2_Dubbele_Infinitief.json"),
            Lesson("N3_C3_S3", "Herhaling", "N3_C3_S3_Herhaling.json")
        )
        "N3_C4" -> listOf(
            Lesson("N3_C4_S1", "De Passieve Vorm", "N3_C4_S1_De_Passieve_Vorm.json"),
            Lesson("N3_C4_S2", "Officiële Voegwoorden", "N3_C4_S2_Officiele_Voegwoorden.json"),
            Lesson("N3_C4_S3", "Herhaling", "N3_C4_S3_Herhaling.json")
        )
        "N3_C5" -> listOf(
            Lesson("N3_C5_S1", "Invuloefening", "N3_C5_S1_Invuloefening.json"),
            Lesson("N3_C5_S2", "Husselaar", "N3_C5_S2_Husselaar.json")
        )
        else -> emptyList()
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

