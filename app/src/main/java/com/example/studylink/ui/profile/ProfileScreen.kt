package com.example.studylink.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.studylink.data.model.User
import com.example.studylink.data.repository.SessionRepository
import com.example.studylink.data.repository.UserRepository
import com.example.studylink.navigation.Routes
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(navController: NavHostController) {
    val userRepository = UserRepository()
    val sessionRepository = SessionRepository()
    val auth = FirebaseAuth.getInstance()

    var user by remember { mutableStateOf<User?>(null) }
    var major by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var aboutMe by remember { mutableStateOf("") }
    var hobbies by remember { mutableStateOf("") }
    var courses by remember { mutableStateOf("") }
    var clubs by remember { mutableStateOf("") }
    var association by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }
    var sessionsHosted by remember { mutableStateOf(0) }
    var sessionsJoined by remember { mutableStateOf(0) }
    var studyPartners by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        userRepository.getCurrentUser { loadedUser ->
            loadedUser?.let {
                user = it
                major = it.major
                year = it.year
                aboutMe = it.aboutMe
                hobbies = it.hobbies.joinToString(", ")
                courses = it.courses.joinToString(", ")
                clubs = it.clubs.joinToString(", ")
                association = it.association
            }
        }
        sessionRepository.getSessions { sessions ->
            val uid = auth.currentUser?.uid ?: ""
            sessionsHosted = sessions.count { it.hostId == uid }
            sessionsJoined = sessions.count { it.participants.contains(uid) }
            studyPartners = sessions
                .filter { it.hostId == uid || it.participants.contains(uid) }
                .flatMap { it.participants }
                .distinct()
                .count()
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
                    IconButton(onClick = { navController.navigate(Routes.SETTINGS) }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { isEditing = !isEditing }) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    TextButton(onClick = {
                        auth.signOut()
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Text("Sign Out", color = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(
                                                MaterialTheme.colorScheme.primary,
                                                MaterialTheme.colorScheme.secondary
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = user?.name?.split(" ")
                                        ?.mapNotNull { it.firstOrNull()?.toString() }
                                        ?.take(2)
                                        ?.joinToString("") ?: "?",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = user?.name ?: "",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "✉️", fontSize = 12.sp)
                                    Text(
                                        text = user?.email ?: "",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    if (major.isNotEmpty()) {
                                        Surface(
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = "📚 $major",
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                    }
                                    if (year.isNotEmpty()) {
                                        Surface(
                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = "🎓 $year",
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (aboutMe.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Bio",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = aboutMe,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 20.sp
                            )
                        }

                        if (courses.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Study Preferences",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                courses.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                    .forEach { course ->
                                        Surface(
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            shape = RoundedCornerShape(20.dp)
                                        ) {
                                            Text(
                                                text = course,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                            }
                        }

                        if (clubs.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Clubs & Associations",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                clubs.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                    .forEach { club ->
                                        Surface(
                                            color = MaterialTheme.colorScheme.tertiaryContainer,
                                            shape = RoundedCornerShape(20.dp)
                                        ) {
                                            Text(
                                                text = club,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.tertiary
                                            )
                                        }
                                    }
                            }
                        }

                        if (hobbies.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Hobbies",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                hobbies.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                    .forEach { hobby ->
                                        Surface(
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            shape = RoundedCornerShape(20.dp)
                                        ) {
                                            Text(
                                                text = hobby,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                            }
                        }
                    }
                }
            }

            // Study Statistics
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "Study Statistics",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatBox(
                            modifier = Modifier.weight(1f),
                            value = "${sessionsHosted * 2}",
                            label = "Total Hours"
                        )
                        StatBox(
                            modifier = Modifier.weight(1f),
                            value = "$sessionsJoined",
                            label = "Sessions Joined"
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatBox(
                            modifier = Modifier.weight(1f),
                            value = "$sessionsHosted",
                            label = "Sessions Hosted"
                        )
                        StatBox(
                            modifier = Modifier.weight(1f),
                            value = "$studyPartners",
                            label = "Study Partners"
                        )
                    }
                }
            }

            // Achievements
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "Achievements",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val achievements = buildList {
                        if (sessionsJoined >= 1) add(Triple("🔥", "7-Day Streak", "Studied for 7 days in a row"))
                        if (sessionsJoined >= 10) add(Triple("🤝", "Team Player", "Joined 10+ study sessions"))
                        if (sessionsHosted >= 5) add(Triple("📚", "Knowledge Sharer", "Hosted 5 study sessions"))
                        if (studyPartners >= 10) add(Triple("⭐", "Social Butterfly", "Connected with 10+ study partners"))
                        if (sessionsJoined == 0 && sessionsHosted == 0) add(Triple("🚀", "Getting Started", "Join or host your first session!"))
                    }

                    achievements.forEach { (emoji, title, description) ->
                        AchievementCard(emoji = emoji, title = title, description = description)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // Edit profile section
            if (isEditing) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Edit Profile",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            OutlinedTextField(
                                value = major,
                                onValueChange = { major = it },
                                label = { Text("Major") },
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
                                label = { Text("Courses (comma separated)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = clubs,
                                onValueChange = { clubs = it },
                                label = { Text("Clubs (comma separated)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = association,
                                onValueChange = { association = it },
                                label = { Text("Association") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = hobbies,
                                onValueChange = { hobbies = it },
                                label = { Text("Hobbies (comma separated)") },
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
                                    else MaterialTheme.colorScheme.error,
                                    fontSize = 13.sp
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
                                            association = association.trim(),
                                            hobbies = hobbies.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                                            courses = courses.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                                            clubs = clubs.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                        )
                                        userRepository.updateUser(updatedUser,
                                            onSuccess = {
                                                isSaving = false
                                                message = "✓ Profile saved!"
                                                isEditing = false
                                                user = updatedUser
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
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatBox(
    modifier: Modifier = Modifier,
    value: String,
    label: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = value,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AchievementCard(
    emoji: String,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = emoji, fontSize = 32.sp)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}