package com.example.studylink.data.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val major: String = "",
    val year: String = "",
    val courses: List<String> = emptyList(),
    val hobbies: List<String> = emptyList(),
    val aboutMe: String = "",
    val profilePicUrl: String = "",
    val clubs: List<String> = emptyList(),
    val association: String = ""
)