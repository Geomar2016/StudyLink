package com.example.studylink.data.repository

import com.example.studylink.BuildConfig
import com.example.studylink.data.model.Session
import com.example.studylink.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class GeminiRepository {

    private val apiKey = BuildConfig.GEMINI_API_KEY
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3-flash-preview:generateContent"

    private suspend fun callGemini(prompt: String): String = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("GeminiAPI", "Key length: ${apiKey.length}, URL: $baseUrl")
            val url = URL(baseUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("x-goog-api-key", apiKey)
            connection.connectTimeout = 15000
            connection.readTimeout = 15000
            connection.doOutput = true

            val body = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                })
            }.toString()

            connection.outputStream.use { it.write(body.toByteArray()) }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().readText()
                val json = JSONObject(response)
                json.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")
            } else {
                val errorResponse = connection.errorStream?.bufferedReader()?.readText() ?: "No error body"
                android.util.Log.e("GeminiAPI", "Error code: $responseCode, Body: $errorResponse")
                "Unable to get recommendations right now. Try again later."
            }
        } catch (e: Exception) {
            "Unable to get recommendations right now. Try again later."
        }
    }

    suspend fun recommendSessions(user: User, sessions: List<Session>): String {
        if (sessions.isEmpty()) return "No sessions available yet — be the first to post one!"

        val sessionList = sessions.joinToString("\n") { session ->
            "- ${session.course}: ${session.topic} at ${session.location} (${session.time}) — ${session.currentSlots}/${session.maxSlots} spots"
        }

        val prompt = """
            You are a helpful study assistant for a university app called StudyLink.
            
            Student profile:
            - Name: ${user.name}
            - Major: ${user.major}
            - Year: ${user.year}
            - Courses: ${user.courses.joinToString(", ").ifEmpty { "Not specified" }}
            - Clubs: ${user.clubs.joinToString(", ").ifEmpty { "None" }}
            - Hobbies: ${user.hobbies.joinToString(", ").ifEmpty { "Not specified" }}
            
            Available study sessions:
            $sessionList
            
            Recommend the TOP 3 most relevant sessions for this student and explain why each is a good fit in 1-2 sentences. Be friendly and encouraging. Keep the response concise.
        """.trimIndent()

        return callGemini(prompt)
    }

    suspend fun generateSessionDescription(course: String, topic: String): String {
        val prompt = """
            Generate a SHORT, friendly study session description (2-3 sentences max) for:
            Course: $course
            Topic: $topic
            
            Make it welcoming and collaborative. Mention what participants should prepare.
            Keep it casual and encouraging, not formal.
        """.trimIndent()

        return callGemini(prompt)
    }

    suspend fun smartSearch(query: String, sessions: List<Session>): List<Session> {
        if (sessions.isEmpty() || query.isBlank()) return sessions

        val sessionList = sessions.mapIndexed { index, session ->
            "$index: ${session.course} - ${session.topic} at ${session.location} (${session.time})"
        }.joinToString("\n")

        val prompt = """
            User search query: "$query"
            
            Available sessions (format: index: details):
            $sessionList
            
            Return ONLY a comma-separated list of index numbers of sessions that match the query.
            Consider course names, topics, locations, and times.
            If none match return "none".
            Example response: "0,2,4" or "none"
        """.trimIndent()

        return try {
            val response = callGemini(prompt)
            if (response.contains("none", ignoreCase = true) ||
                response.contains("Unable", ignoreCase = true)) {
                sessions.filter { session ->
                    session.course.contains(query, ignoreCase = true) ||
                            session.topic.contains(query, ignoreCase = true) ||
                            session.location.contains(query, ignoreCase = true)
                }
            } else {
                val indices = response.trim()
                    .split(",")
                    .mapNotNull { it.trim().toIntOrNull() }
                if (indices.isEmpty()) {
                    sessions.filter { session ->
                        session.course.contains(query, ignoreCase = true) ||
                                session.topic.contains(query, ignoreCase = true)
                    }
                } else {
                    indices.mapNotNull { sessions.getOrNull(it) }
                }
            }
        } catch (e: Exception) {
            sessions.filter { session ->
                session.course.contains(query, ignoreCase = true) ||
                        session.topic.contains(query, ignoreCase = true)
            }
        }
    }

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