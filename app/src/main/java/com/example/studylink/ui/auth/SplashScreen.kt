package com.example.studylink.ui.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.studylink.R
import com.example.studylink.navigation.Routes
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    val logoScale = remember { Animatable(0f) }
    val logoAlpha = remember { Animatable(0f) }
    val titleAlpha = remember { Animatable(0f) }
    val titleOffset = remember { Animatable(30f) }
    val taglineAlpha = remember { Animatable(0f) }
    val word1Alpha = remember { Animatable(0f) }
    val word2Alpha = remember { Animatable(0f) }
    val word3Alpha = remember { Animatable(0f) }
    val word4Alpha = remember { Animatable(0f) }
    val backgroundAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        backgroundAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(800, easing = FastOutSlowInEasing)
        )
        logoAlpha.animateTo(1f, animationSpec = tween(400))
        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
        delay(200)
        titleAlpha.animateTo(1f, animationSpec = tween(500))
        titleOffset.animateTo(
            targetValue = 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        delay(300)
        word1Alpha.animateTo(1f, animationSpec = tween(350))
        delay(150)
        word2Alpha.animateTo(1f, animationSpec = tween(350))
        delay(150)
        word3Alpha.animateTo(1f, animationSpec = tween(350))
        delay(150)
        word4Alpha.animateTo(1f, animationSpec = tween(350))
        delay(200)
        taglineAlpha.animateTo(1f, animationSpec = tween(600))
        delay(1200)

        navController.navigate(Routes.LOGIN) {
            popUpTo(Routes.SPLASH) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(backgroundAlpha.value)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF3E0),
                        Color(0xFFFFE0B2),
                        Color(0xFFFFCCBC),
                        Color(0xFFFFAB91)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Background blobs
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-80).dp, y = (-120).dp)
                .background(
                    Color(0xFFFFD54F).copy(alpha = 0.25f),
                    shape = RoundedCornerShape(50)
                )
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 60.dp, y = 60.dp)
                .background(
                    Color(0xFFFF8A65).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(50)
                )
        )
        Box(
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = 80.dp)
                .background(
                    Color(0xFFFFCC02).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(50)
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            // Real logo
            Box(
                modifier = Modifier
                    .scale(logoScale.value)
                    .alpha(logoAlpha.value)
                    .size(110.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFF7043),
                                Color(0xFFFF5722)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_studylink_logo),
                    contentDescription = "StudyLink Logo",
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // App name
            Box(
                modifier = Modifier
                    .alpha(titleAlpha.value)
                    .offset(y = titleOffset.value.dp)
            ) {
                Text(
                    text = "StudyLink",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4E342E)
                )
            }

            // Word by word tagline
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Connect.",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFBF360C),
                    modifier = Modifier.alpha(word1Alpha.value)
                )
                Text(
                    text = "Learn.",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFE64A19),
                    modifier = Modifier.alpha(word2Alpha.value)
                )
                Text(
                    text = "Grow.",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFF5722),
                    modifier = Modifier.alpha(word3Alpha.value)
                )
                Text(
                    text = "🌅",
                    fontSize = 18.sp,
                    modifier = Modifier.alpha(word4Alpha.value)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Inspiring quote
            Text(
                text = "\"The beautiful thing about\nlearning is nobody can take it away.\"",
                fontSize = 13.sp,
                color = Color(0xFF6D4C41).copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.alpha(taglineAlpha.value)
            )
        }
    }
}