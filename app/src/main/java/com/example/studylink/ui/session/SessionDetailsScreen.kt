package com.example.studylink.ui.session

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.studylink.data.model.Session
import com.example.studylink.data.repository.SessionRepository
import com.example.studylink.navigation.Routes
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailScreen(navController: NavHostController, sessionId: String) {
    val sessionRepository = SessionRepository()
    val auth = FirebaseAuth.getInstance()
    val currentUserId = auth.currentUser?.uid ?: ""

    var session by remember { mutableStateOf<Session?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    // Load session in real time
    LaunchedEffect(sessionId) {
        sessionRepository.getSessions { sessions ->
            session = sessions.find { it.id == sessionId }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Session Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Show delete button only for the host
                    if (session?.hostId == currentUserId) {
                        IconButton(onClick = {
                            sessionRepository.deleteSession(sessionId,
                                onSuccess = { navController.navigate(Routes.HOME) },
                                onError = { message = "Failed to delete session" }
                            )
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            )
        }
    ) { padding ->
        session?.let { s ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Course + topic header
                Text(text = s.course, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text(text = s.topic, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                HorizontalDivider()

                // Session info
                InfoRow(label = "📍 Location", value = s.location)
                InfoRow(label = "🕐 Time", value = s.time)
                InfoRow(label = "👤 Host", value = s.hostName)
                InfoRow(label = "👥 Spots", value = "${s.currentSlots}/${s.maxSlots} filled")

                HorizontalDivider()

                // Participants section
                Text(text = "Participants (${s.participants.size})", fontWeight = FontWeight.SemiBold)
                if (s.participants.isEmpty()) {
                    Text(
                        text = "No one has joined yet — be the first!",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                } else {
                    s.participants.forEach { participantId ->
                        Text(text = "• $participantId", fontSize = 14.sp)
                    }
                }

                if (message.isNotEmpty()) {
                    Text(text = message, color = MaterialTheme.colorScheme.error)
                }

                Spacer(modifier = Modifier.weight(1f))

                // Join / Already joined / Full button
                val alreadyJoined = s.participants.contains(currentUserId)
                val isFull = s.currentSlots >= s.maxSlots
                val isHost = s.hostId == currentUserId

                Button(
                    onClick = {
                        if (!alreadyJoined && !isFull) {
                            isLoading = true
                            sessionRepository.joinSession(s,
                                onSuccess = { isLoading = false },
                                onError = {
                                    isLoading = false
                                    message = "Failed to join session"
                                }
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !alreadyJoined && !isFull && !isHost && !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when {
                            isHost -> MaterialTheme.colorScheme.secondary
                            alreadyJoined -> MaterialTheme.colorScheme.primaryContainer
                            isFull -> MaterialTheme.colorScheme.errorContainer
                            else -> MaterialTheme.colorScheme.primary
                        }
                    )
                ) {
                    Text(
                        text = when {
                            isHost -> "You're hosting this session"
                            alreadyJoined -> "✓ You've joined"
                            isFull -> "Session is full"
                            else -> "Join Session"
                        },
                        fontSize = 16.sp
                    )
                }
            }
        } ?: Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.Medium)
        Text(text = value, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}