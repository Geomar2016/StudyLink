package com.example.studylink.data.model

data class Session(
        val id: String = "",
        val hostId: String = "",
        val hostName: String = "",
        val course: String = "",
        val topic: String = "",
        val location: String = "",
        val time: String = "",
        val maxSlots: Int = 4,
        val currentSlots: Int = 0,
        val participants: List<String> = emptyList()
)