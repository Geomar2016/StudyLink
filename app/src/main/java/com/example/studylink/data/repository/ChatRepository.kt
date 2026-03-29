package com.example.studylink.data.repository

import com.example.studylink.data.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getChatId(userId1: String, userId2: String): String {
        return if (userId1 < userId2) "${userId1}_${userId2}"
        else "${userId2}_${userId1}"
    }

    fun sendMessage(
        toUserId: String,
        text: String,
        senderName: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val senderId = auth.currentUser?.uid ?: return
        val chatId = getChatId(senderId, toUserId)
        val message = Message(
            id = db.collection("chats").document().id,
            senderId = senderId,
            senderName = senderName,
            text = text,
            timestamp = System.currentTimeMillis()
        )
        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .document(message.id)
            .set(message)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    fun getMessages(
        toUserId: String,
        onResult: (List<Message>) -> Unit
    ) {
        val currentUserId = auth.currentUser?.uid ?: return
        val chatId = getChatId(currentUserId, toUserId)
        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                val messages = snapshot?.toObjects(Message::class.java) ?: emptyList()
                onResult(messages)
            }
    }
}