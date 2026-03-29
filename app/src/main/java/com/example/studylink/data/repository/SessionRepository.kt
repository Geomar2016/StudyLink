package com.example.studylink.data.repository

import com.example.studylink.data.model.Session
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class SessionRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val sessionsCollection = db.collection("sessions")

    // CREATE - Post a new study session
    fun createSession(session: Session, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        val docRef = sessionsCollection.document()
        val newSession = session.copy(id = docRef.id, hostId = auth.currentUser?.uid ?: "")
        docRef.set(newSession)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e) }
    }

    // READ - Get all sessions live (updates in real time)
    fun getSessions(onResult: (List<Session>) -> Unit) {
        sessionsCollection.addSnapshotListener { snapshot, _ ->
            val sessions = snapshot?.toObjects(Session::class.java) ?: emptyList()
            onResult(sessions)
        }
    }

    // UPDATE - Join a session
    fun joinSession(session: Session, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val updatedParticipants = session.participants + userId
        sessionsCollection.document(session.id)
            .update(
                "participants", updatedParticipants,
                "currentSlots", session.currentSlots + 1
            )
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e) }
    }

    // DELETE - Remove a session (host only)
    fun deleteSession(sessionId: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        sessionsCollection.document(sessionId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e) }
    }
}