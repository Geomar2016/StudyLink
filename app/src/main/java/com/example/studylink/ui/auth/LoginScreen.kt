package com.example.studylink.ui.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.studylink.BuildConfig
import com.example.studylink.R
import com.example.studylink.data.model.User
import com.example.studylink.data.repository.UserRepository
import com.example.studylink.navigation.Routes
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun LoginScreen(navController: NavHostController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val userRepository = UserRepository()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val infiniteTransition = rememberInfiniteTransition(label = "bg")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val contentAlpha = remember { Animatable(0f) }
    val contentOffset = remember { Animatable(60f) }

    LaunchedEffect(Unit) {
        contentAlpha.animateTo(1f, animationSpec = tween(800))
        contentOffset.animateTo(
            0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                isLoading = true
                auth.signInWithCredential(credential)
                    .addOnSuccessListener { authResult ->
                        val firebaseUser = authResult.user!!
                        userRepository.userExists(firebaseUser.uid) { exists ->
                            if (!exists) {
                                val newUser = User(
                                    id = firebaseUser.uid,
                                    name = firebaseUser.displayName ?: "",
                                    email = firebaseUser.email ?: "",
                                    profilePicUrl = firebaseUser.photoUrl?.toString() ?: ""
                                )
                                userRepository.createUser(newUser,
                                    onSuccess = {
                                        navController.navigate(Routes.HOME) {
                                            popUpTo(Routes.LOGIN) { inclusive = true }
                                        }
                                    },
                                    onError = { errorMessage = it.message ?: "Error" }
                                )
                            } else {
                                navController.navigate(Routes.HOME) {
                                    popUpTo(Routes.LOGIN) { inclusive = true }
                                }
                            }
                        }
                        isLoading = false
                    }
                    .addOnFailureListener {
                        errorMessage = it.message ?: "Sign in failed"
                        isLoading = false
                    }
            } catch (e: ApiException) {
                errorMessage = "Google sign in failed"
                isLoading = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF3E0),
                        Color(0xFFFFE0B2),
                        Color(0xFFFFCCBC),
                        Color(0xFFFFAB91)
                    )
                )
            )
    ) {
        // Warm geometric background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2
            val cy = size.height / 2

            drawCircle(
                color = Color(0xFFFF7043).copy(alpha = 0.07f),
                radius = size.width * 0.8f * pulse,
                center = Offset(cx, cy),
                style = Stroke(width = 1.5f)
            )
            drawCircle(
                color = Color(0xFFFFB300).copy(alpha = 0.05f),
                radius = size.width * 0.6f * pulse,
                center = Offset(cx, cy),
                style = Stroke(width = 1f)
            )
            drawCircle(
                color = Color(0xFFFF5722).copy(alpha = 0.08f),
                radius = size.width * 0.4f * pulse,
                center = Offset(cx, cy),
                style = Stroke(width = 1f)
            )

            // Warm corner blobs
            drawCircle(
                color = Color(0xFFFFD54F).copy(alpha = 0.2f),
                radius = 180f,
                center = Offset(0f, 0f)
            )
            drawCircle(
                color = Color(0xFFFF8A65).copy(alpha = 0.15f),
                radius = 220f,
                center = Offset(size.width, size.height)
            )
            drawCircle(
                color = Color(0xFFFFCC02).copy(alpha = 0.12f),
                radius = 140f,
                center = Offset(size.width, 0f)
            )
            drawCircle(
                color = Color(0xFFFF7043).copy(alpha = 0.1f),
                radius = 100f,
                center = Offset(0f, size.height)
            )

            // Floating dots
            listOf(
                Offset(cx * 0.3f, cy * 0.4f),
                Offset(cx * 1.7f, cy * 0.6f),
                Offset(cx * 0.5f, cy * 1.6f),
                Offset(cx * 1.5f, cy * 1.4f),
            ).forEach { offset ->
                drawCircle(
                    color = Color(0xFFFF7043).copy(alpha = 0.2f),
                    radius = 4f,
                    center = offset
                )
            }
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .offset(y = contentOffset.value.dp)
                .alpha(contentAlpha.value),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Real logo
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(28.dp))
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
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "StudyLink",
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4E342E)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Find your study squad.\nConnect. Learn. Grow.",
                fontSize = 16.sp,
                color = Color(0xFF6D4C41).copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(64.dp))

            if (isLoading) {
                CircularProgressIndicator(color = Color(0xFFFF7043))
            } else {
                Button(
                    onClick = {
                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(BuildConfig.WEB_CLIENT_ID)
                            .requestEmail()
                            .build()
                        val googleSignInClient = GoogleSignIn.getClient(context, gso)
                        launcher.launch(googleSignInClient.signInIntent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF7043)
                    )
                ) {
                    Text(
                        text = "Continue with Google",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "By continuing you agree to our Terms of Service",
                    fontSize = 11.sp,
                    color = Color(0xFF6D4C41).copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )
            }

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMessage,
                    color = Color(0xFFB71C1C),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}