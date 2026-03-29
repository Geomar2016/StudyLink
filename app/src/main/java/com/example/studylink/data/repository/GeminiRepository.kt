package com.example.studylink.data.repository

import com.example.studylink.BuildConfig
import com.example.studylink.data.model.Session
import com.example.studylink.data.model.User
import com.google.ai.client.generativeai.GenerativeModel

class GeminiRepository {

    private val model = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    // Feature 1 - Smart Session Recommender
    suspend fun recommendSessions(user: User, sessions: List<Session>): String {
        if (sessions.isEmpty()) return "No sessions available to recommend."

        val sessionList = sessions.joinToString("\n") { session ->
            "- ${session.course}: ${session.topic} at ${session.location} (${session.time}) — ${session.currentSlots}/${session.maxSlots} spots filled. Club: ${session.club.ifEmpty { "Open to all" }}"
        }

        val prompt = """
            You are a helpful study assistant for a university app called StudyLink.
            
            Student profile:
            - Name: ${user.name}
            - Major: ${user.major}
            - Year: ${user.year}
            - Courses: ${user.courses.joinToString(", ")}
            - Clubs: ${user.clubs.joinToString(", ")}
            - Hobbies: ${user.hobbies.joinToString(", ")}
            
            Available study sessions:
            $sessionList
            
            Recommend the TOP 3 most relevant sessions for this student and explain why each one is a good fit in 1-2 sentences. Be friendly and encouraging. Format each recommendation clearly.
        """.trimIndent()

        return try {
            val response = model.generateContent(prompt)
            response.text ?: "Unable to generate recommendations."
        } catch (e: Exception) {
            "Error getting recommendations: ${e.message}"
        }
    }

    // Feature 2 - Auto Session Description Generator
    suspend fun generateSessionDescription(course: String, topic: String): String {
        val prompt = """
            You are helping a university student create a study session on StudyLink.
            
            Course: $course
            Topic: $topic
            
            Generate a SHORT, friendly session description (2-3 sentences max) that:
            - Explains what will be covered
            - Mentions what participants should prepare
            - Sounds welcoming and collaborative
            
            Keep it casual and encouraging, not formal.
        """.trimIndent()

        return try {
            val response = model.generateContent(prompt)
            response.text ?: "Come join us to study $topic together!"
        } catch (e: Exception) {
            "Come join us to study $topic in $course!"
        }
    }

    // Feature 3 - Smart Search
    suspend fun smartSearch(query: String, sessions: List<Session>): List<Session> {
        if (sessions.isEmpty() || query.isBlank()) return sessions

        val sessionList = sessions.mapIndexed { index, session ->
            "$index: ${session.course} - ${session.topic} at ${session.location} (${session.time})"
        }.joinToString("\n")

        val prompt = """
            You are a search assistant for a university study app.
            
            User search query: "$query"
            
            Available sessions (format: index: details):
            $sessionList
            
            Return ONLY a comma-separated list of the index numbers of sessions that match the query.
            Consider course names, topics, locations, and times.
            Be generous with matches — if it could be relevant, include it.
            If none match, return "none".
            Example response: "0,2,4" or "none"
        """.trimIndent()

        return try {
            val response = model.generateContent(prompt)
            val text = response.text?.trim() ?: return sessions
            if (text == "none") return emptyList()
            val indices = text.split(",").mapNotNull { it.trim().toIntOrNull() }
            indices.mapNotNull { sessions.getOrNull(it) }
        } catch (e: Exception) {
            // Fall back to basic search if Gemini fails
            sessions.filter { session ->
                session.course.contains(query, ignoreCase = true) ||
                        session.topic.contains(query, ignoreCase = true) ||
                        session.location.contains(query, ignoreCase = true)
            }
        }
    }

    // Feature 4 - Club Session Matcher
    suspend fun matchClubSessions(user: User, sessions: List<Session>): List<Session> {
        if (user.clubs.isEmpty()) return sessions
        return sessions.filter { session ->
            session.club.isEmpty() ||
                    user.clubs.any { club ->
                        club.equals(session.club, ignoreCase = true)
                    }
        }
    }
}