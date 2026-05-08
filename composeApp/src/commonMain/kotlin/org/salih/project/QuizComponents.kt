package org.salih.project

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

fun getLightColors(base: Color): Pair<Color, Color> {
    return when (base.value.toLong()) {
        Color(0xFF58CC02).value.toLong() -> Color(0xFFD7FFB8) to Color(0xFFB8E695)
        Color(0xFF1CB0F6).value.toLong() -> Color(0xFFD1F1FF) to Color(0xFFAADCF5)
        Color(0xFFCE82FF).value.toLong() -> Color(0xFFF0D9FF) to Color(0xFFD4AFFF)
        else -> Color(0xFFF7F7F7) to Color(0xFFE5E5E5)
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
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(top = 3.dp)
                .background(shadowColor, RoundedCornerShape(12.dp))
        )
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
