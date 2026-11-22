package com.example.e_commercevr.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_commercevr.ui.components.GlassmorphismCard
import com.example.e_commercevr.ui.theme.AuraColors
import com.example.e_commercevr.ui.theme.AuraShapes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginBottomSheet(
    onDismissRequest: () -> Unit,
    onLoginSuccess: (email: String) -> Unit,
    onContinueAsGuest: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Color.Transparent,
        dragHandle = null,
        modifier = modifier
    ) {
        GlassmorphismCard(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            fillColor = AuraColors.GlassFillHeavy,
            strokeColor = Color.White.copy(alpha = 0.95f)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Handle Bar
                Box(
                    modifier = Modifier
                        .width(44.dp)
                        .height(5.dp)
                        .clip(AuraShapes.Pill)
                        .background(AuraColors.Primary.copy(alpha = 0.30f))
                )
                Spacer(modifier = Modifier.height(20.dp))

                // 2. Heading & Description
                Text(
                    text = "Nikmati Pengalaman Lengkap",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AuraColors.Primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Masuk untuk menyimpan keranjang, checkout, dan riwayat AR try-on kamu",
                    fontSize = 13.sp,
                    color = AuraColors.Primary.copy(alpha = 0.75f),
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(24.dp))

                // 3. Form Email
                GlassmorphismCard(
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = AuraShapes.Medium,
                    fillColor = Color.White.copy(alpha = 0.30f),
                    strokeColor = AuraColors.GlassStroke
                ) {
                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("Alamat Email", color = AuraColors.Primary.copy(alpha = 0.5f), fontSize = 14.sp) },
                        modifier = Modifier.fillMaxSize(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            errorContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 4. Form Password
                GlassmorphismCard(
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = AuraShapes.Medium,
                    fillColor = Color.White.copy(alpha = 0.30f),
                    strokeColor = AuraColors.GlassStroke
                ) {
                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Kata Sandi", color = AuraColors.Primary.copy(alpha = 0.5f), fontSize = 14.sp) },
                        modifier = Modifier.fillMaxSize(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            errorContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 5. Tombol Masuk (Primary CTA)
                GlassmorphismCard(
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = AuraShapes.Medium,
                    fillColor = AuraColors.Accent,
                    strokeColor = Color.Transparent,
                    onClick = { onLoginSuccess(email) }
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Masuk",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 6. Tombol Buat Akun
                Text(
                    text = "Buat Akun",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AuraColors.Primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLoginSuccess(email) }
                        .padding(vertical = 12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
