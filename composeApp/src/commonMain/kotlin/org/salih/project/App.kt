package org.salih.project

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class Screen {
    HOME, CATEGORY_DETAIL, LESSON_PAGE
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

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedLesson by remember { mutableStateOf<Lesson?>(null) }

    val categories = remember {
        listOf(
            Category("a2", "Niveau A2", Color(0xFF58CC02), Color(0xFF46A302)),
            Category("b1", "Niveau B1", Color(0xFF58CC02), Color(0xFF46A302)),
            Category("b2", "Niveau B2", Color(0xFF58CC02), Color(0xFF46A302))
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
            text = "Welkom bij Taal",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF58CC02),
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
fun CategoryDetailScreen(category: Category, onBack: () -> Unit, onLessonClick: (Lesson) -> Unit) {
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(category.title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←", fontSize = 24.sp, fontWeight = FontWeight.Bold)
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
                (1..5).map { Lesson("${category.id}_$it", "${category.title} Les $it") }
            }

            lessons.forEach { lesson ->
                DuolingoButton(
                    text = lesson.title,
                    baseColor = Color.White,
                    shadowColor = Color(0xFFE5E5E5),
                    textColor = Color.DarkGray,
                    onClick = { onLessonClick(lesson) },
                    modifier = Modifier.widthIn(min = 280.dp, max = 400.dp).fillMaxWidth(0.9f)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(lesson: Lesson, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(lesson.title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←", fontSize = 24.sp, fontWeight = FontWeight.Bold)
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Inhoud van ${lesson.title} komt hier...",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF58CC02)
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

@Composable
fun DuolingoButton(
    text: String,
    baseColor: Color,
    shadowColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = Color.White
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val translationY by animateDpAsState(targetValue = if (isPressed) 4.dp else 0.dp)
    
    Box(
        modifier = modifier
            .height(60.dp)
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
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}