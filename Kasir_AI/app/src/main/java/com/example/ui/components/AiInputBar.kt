package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.ElectricBlue

@Composable
fun AiInputBar(
    isListening: Boolean,
    onVoiceClick: () -> Unit,
    onSaveOrder: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var textValue by remember { mutableStateOf("") }
    val shape = RoundedCornerShape(32.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = shape,
                ambientColor = Color(0x601F2687),
                spotColor = Color(0x7006B6D4)
            )
            .clip(shape)
            .background(Color.White.copy(alpha = 0.12f))
            .border(
                1.dp,
                Brush.horizontalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.35f),
                        Color.White.copy(alpha = 0.15f)
                    )
                ),
                shape
            )
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag("ai_input_bar")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Text Input Field (transparent background, white text, 50% placeholder)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (textValue.isEmpty() && !isListening) {
                    Text(
                        text = "Ketik pesanan...",
                        color = Color.White.copy(alpha = 0.50f),
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Normal
                    )
                } else if (isListening) {
                    Text(
                        text = "🎙️ Mendengarkan suara pesanan...",
                        color = CyanGlow,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                BasicTextField(
                    value = textValue,
                    onValueChange = { textValue = it },
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    cursorBrush = SolidColor(Color.White),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (textValue.isNotBlank()) {
                                onSaveOrder(textValue)
                                textValue = ""
                            }
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("order_text_input")
                )
            }

            // Vibrant Glowing Circular Microphone Button with Pulsing Animation
            PulsingMicButton(
                isListening = isListening,
                onClick = onVoiceClick
            )

            Spacer(modifier = Modifier.width(6.dp))

            // Small Compact "Simpan" Button
            Box(
                modifier = Modifier
                    .height(34.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.22f))
                    .border(1.dp, Color.White.copy(alpha = 0.35f), CircleShape)
                    .clickable {
                        if (textValue.isNotBlank()) {
                            onSaveOrder(textValue)
                            textValue = ""
                        }
                    }
                    .padding(horizontal = 14.dp)
                    .testTag("save_order_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Simpan",
                    color = Color.White,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
