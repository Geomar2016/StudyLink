package com.example.studylink.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.studylink.data.model.User
import com.example.studylink.data.repository.UserRepository
import com.example.studylink.navigation.Routes
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController) {
    val userRepository = UserRepository()
    val auth = FirebaseAuth.getInstance()

    var user by remember { mutableStateOf<User?>(null) }
    var major by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var aboutMe by remember { mutableStateOf("") }
    var hobbies by remember { mutableStateOf("") }
    var courses by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    // Load current user
    LaunchedEffect(Unit) {
        userRepository.getCurrentUser { loadedUser ->
            loadedUser?.let {
                user = it
                major = it.major
                year = it.year
                aboutMe = it.aboutMe
                hobbies = it.hobbies.joinToString(", ")
                courses = it.courses.joinToString(", ")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = {
                        auth.signOut()
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Text("Sign Out")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Name and email header
            user?.let {
                Text(text = it.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(text = it.email, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            HorizontalDivider()

            Text(text = "Edit Profile", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)

            OutlinedTextField(
                value = major,
                onValueChange = { major = it },
                label = { Text("Major (e.g. Computer Science)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = year,
                onValueChange = { year = it },
                label = { Text("Year (e.g. Sophomore)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = courses,
                onValueChange = { courses = it },
                label = { Text("Courses (comma separated, e.g. MA 114, CS 215)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = hobbies,
                onValueChange = { hobbies = it },
                label = { Text("Hobbies (comma separated, e.g. Gaming, Music)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = aboutMe,
                onValueChange = { aboutMe = it },
                label = { Text("About Me") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                maxLines = 4
            )

            if (message.isNotEmpty()) {
                Text(
                    text = message,
                    color = if (message.startsWith("✓"))
                        MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = {
                    user?.let { currentUser ->
                        isSaving = true
                        val updatedUser = currentUser.copy(
                            major = major.trim(),
                            year = year.trim(),
                            aboutMe = aboutMe.trim(),
                            hobbies = hobbies.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                            courses = courses.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        )
                        userRepository.updateUser(updatedUser,
                            onSuccess = {
                                isSaving = false
                                message = "✓ Profile saved!"
                            },
                            onError = {
                                isSaving = false
                                message = "Failed to save profile"
                            }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Save Profile", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}