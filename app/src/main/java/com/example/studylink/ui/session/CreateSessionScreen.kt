package com.example.studylink.ui.session

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.studylink.data.model.Session
import com.example.studylink.data.repository.GeminiRepository
import com.example.studylink.data.repository.SessionRepository
import com.example.studylink.data.repository.UserRepository
import com.example.studylink.navigation.Routes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSessionScreen(navController: NavHostController) {
    val sessionRepository = SessionRepository()
    val userRepository = UserRepository()
    val geminiRepository = GeminiRepository()
    val scope = rememberCoroutineScope()

    var sessionTitle by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("1 hour") }
    var maxSlots by remember { mutableStateOf("5") }
    var location by remember { mutableStateOf("") }
    var isVirtual by remember { mutableStateOf(false) }
    var club by remember { mutableStateOf("") }
    var isClubOnly by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var isGeneratingDesc by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showSubjectDropdown by remember { mutableStateOf(false) }
    var showDurationDropdown by remember { mutableStateOf(false) }

    val subjects = listOf(
        "Mathematics", "Computer Science", "Physics", "Chemistry",
        "Biology", "Engineering", "English", "History",
        "Economics", "Psychology", "Other"
    )

    val durations = listOf(
        "30 minutes", "1 hour", "1.5 hours", "2 hours", "2.5 hours", "3 hours"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
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
                            Text("SL", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Text("Study Link", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = "Create Study Session",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Set up a new study session and invite others to join",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Basic Information Card
            SectionCard(title = "Basic Information") {
                OutlinedTextField(
                    value = sessionTitle,
                    onValueChange = { sessionTitle = it },
                    label = { Text("Session Title *") },
                    placeholder = { Text("e.g., Calculus II Study Group") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Subject dropdown
                Box {
                    OutlinedTextField(
                        value = subject,
                        onValueChange = {},
                        label = { Text("Subject *") },
                        placeholder = { Text("Select a subject") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showSubjectDropdown = true },
                        shape = RoundedCornerShape(12.dp),
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.clickable { showSubjectDropdown = true }
                            )
                        }
                    )
                    DropdownMenu(
                        expanded = showSubjectDropdown,
                        onDismissRequest = { showSubjectDropdown = false }
                    ) {
                        subjects.forEach { s ->
                            DropdownMenuItem(
                                text = { Text(s) },
                                onClick = {
                                    subject = s
                                    showSubjectDropdown = false
                                }
                            )
                        }
                    }
                }

                // Description with AI generate button
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        placeholder = { Text("What topics will you cover? What should participants prepare?") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 4
                    )
                    Button(
                        onClick = {
                            if (subject.isBlank() && sessionTitle.isBlank()) {
                                errorMessage = "Enter a subject and title first"
                                return@Button
                            }
                            isGeneratingDesc = true
                            scope.launch {
                                val generated = geminiRepository.generateSessionDescription(
                                    course = subject.ifBlank { "General" },
                                    topic = sessionTitle.ifBlank { "Study session" }
                                )
                                description = generated
                                isGeneratingDesc = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isGeneratingDesc,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        if (isGeneratingDesc) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    "Generating with AI...",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            Text(
                                "✨ Generate description with AI",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Date & Time Card
            SectionCard(title = "Date & Time", icon = Icons.Default.DateRange) {
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date *") },
                    placeholder = { Text("e.g., Today, Tomorrow, 3/30/2026") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Time *") },
                    placeholder = { Text("e.g., 3:00 PM") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Duration dropdown
                Box {
                    OutlinedTextField(
                        value = duration,
                        onValueChange = {},
                        label = { Text("Duration") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDurationDropdown = true },
                        shape = RoundedCornerShape(12.dp),
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.clickable { showDurationDropdown = true }
                            )
                        }
                    )
                    DropdownMenu(
                        expanded = showDurationDropdown,
                        onDismissRequest = { showDurationDropdown = false }
                    ) {
                        durations.forEach { d ->
                            DropdownMenuItem(
                                text = { Text(d) },
                                onClick = {
                                    duration = d
                                    showDurationDropdown = false
                                }
                            )
                        }
                    }
                }

                // Max participants
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Max Participants",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    OutlinedTextField(
                        value = maxSlots,
                        onValueChange = { maxSlots = it },
                        modifier = Modifier.width(80.dp),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }

            // Location Card
            SectionCard(title = "Location", icon = Icons.Default.LocationOn) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "This is a virtual session",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Switch(
                        checked = isVirtual,
                        onCheckedChange = { isVirtual = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text(if (isVirtual) "Meeting Link (optional)" else "Location *") },
                    placeholder = { Text(if (isVirtual) "e.g., Zoom link" else "e.g., Library Room 301") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                )
            }

            // Club Session Card
            SectionCard(title = "Club Session (Optional)") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Club members only",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Switch(
                        checked = isClubOnly,
                        onCheckedChange = { isClubOnly = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }

                if (isClubOnly) {
                    OutlinedTextField(
                        value = club,
                        onValueChange = { club = it },
                        label = { Text("Club Name") },
                        placeholder = { Text("e.g., ACM, Math Club") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
            }

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Action buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = {
                        if (sessionTitle.isBlank() || subject.isBlank() ||
                            date.isBlank() || time.isBlank() ||
                            (!isVirtual && location.isBlank())
                        ) {
                            errorMessage = "Please fill in all required fields"
                            return@Button
                        }
                        isLoading = true
                        userRepository.getCurrentUser { user ->
                            val session = Session(
                                course = subject,
                                topic = sessionTitle,
                                location = if (isVirtual) "Virtual${if (location.isNotEmpty()) " — $location" else ""}" else location,
                                time = "$date $time",
                                maxSlots = maxSlots.toIntOrNull() ?: 5,
                                hostName = user?.name ?: "Anonymous",
                                club = if (isClubOnly) club else "",
                                clubOnly = isClubOnly
                            )
                            sessionRepository.createSession(session,
                                onSuccess = {
                                    isLoading = false
                                    navController.navigate(Routes.HOME) {
                                        popUpTo(Routes.HOME) { inclusive = true }
                                    }
                                },
                                onError = {
                                    isLoading = false
                                    errorMessage = it.message ?: "Error creating session"
                                }
                            )
                        }
                    },
                    modifier = Modifier
                        .weight(2f)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("+ Create Session", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    content: @Composable ColumnScope.() -> Unit
) {
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
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            content()
        }
    }
}