package com.ainsln.rhythmicabc.ui.alphabet.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ainsln.rhythmicabc.data.source.DefaultRhythmicAlphabet
import com.ainsln.rhythmicabc.data.source.RhythmicLetter
import com.ainsln.rhythmicabc.ui.theme.RhythmicABCTheme

@Composable
fun RhythmicLetterBox(
    letter: RhythmicLetter,
    onLetterClick: (RhythmicLetter) -> Unit,
    isPlaying: Boolean,
    currentElementIndex: Int
){
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.padding(8.dp).drawBehind {
            if (isPlaying){
                drawRoundRect(
                    color = Color.Yellow.copy(alpha = 0.15f),
                    cornerRadius = CornerRadius(8.dp.toPx())
                )
            }
        }
            .clickable { onLetterClick(letter) }
            .border(width = 2.dp, color = Color.Black, shape = RoundedCornerShape(8.dp))
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            Text(
                text = letter.name,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                letter.pattern.forEachIndexed { index, element ->
                    PatternElement(
                       sound =  element,
                       isPlaying = isPlaying && index == currentElementIndex
                    )
                }
            }
        }
    }
}

@Composable
fun PatternElement(
    sound: Boolean,
    isPlaying: Boolean,
    beatColor: Color = Color.Red,
    quietColor: Color = MaterialTheme.colorScheme.onSurface
){
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(24.dp).drawBehind {
            if (isPlaying){
                drawCircle(
                    Brush.radialGradient(
                        listOf(Color.Cyan, Color.White),
                        radius = 16.dp.toPx()
                    )
                )
            }
    }
    ) {
        if (sound){
            Spacer(modifier = Modifier.size(16.dp).drawBehind {
                drawCircle(color = beatColor)
            })
        } else {
            Spacer(modifier = Modifier.size(width = 16.dp, height = 4.dp).drawBehind {
                drawRect(color = quietColor)
            })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RhythmicLetterPreview(){
    RhythmicABCTheme {
        RhythmicLetterBox(
            letter = DefaultRhythmicAlphabet.getLetter(name = "A"),
            onLetterClick = {},
            isPlaying = false,
            currentElementIndex = 1
        )
    }
}
