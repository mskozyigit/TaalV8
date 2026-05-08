package org.salih.project

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

fun String.adjustCase(isFirst: Boolean): String {
    if (this.isEmpty()) return this
    val natural = this.lowercase()
    return if (isFirst) {
        natural.replaceFirstChar { it.uppercase() }
    } else {
        natural
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

        FlowRow(horizontalArrangement = Arrangement.Center) {
            exercise.shuffledWords.forEachIndexed { index, word ->
                val countInUser = userWords.count { it == word }
                val countBeforeInShuffled = exercise.shuffledWords.take(index).count { it == word }
                val isUsed = countInUser > countBeforeInShuffled
                
                Box(modifier = Modifier.padding(4.dp)) {
                    if (!isUsed) {
                        QuizWordButton(
                            text = word.lowercase(),
                            baseColor = lightColors.first,
                            shadowColor = lightColors.second,
                            textColor = categoryColor,
                            onClick = { onWordClick(word, true) },
                            height = 40.dp,
                            fontSize = 14.sp
                        )
                    } else {
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
