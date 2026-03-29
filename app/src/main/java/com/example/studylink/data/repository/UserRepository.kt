package com.example.studylink.data.repository

import com.example.studylink.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val usersCollection = db.collection("users")

    // CREATE - Save a new user to Firestore after sign in
    fun createUser(user: User, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        usersCollection.document(user.id)
            .set(user)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e) }
    }

    // READ - Get current logged in user's profile
    fun getCurrentUser(onResult: (User?) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onResult(null)
        usersCollection.document(uid)
            .get()
            .addOnSuccessListener { doc ->
                val user = doc.toObject(User::class.java)
                onResult(user)
            }
            .addOnFailureListener { onResult(null) }
    }

    // UPDATE - Edit user profile info
    fun updateUser(user: User, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        usersCollection.document(user.id)
            .set(user)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e) }
    }

    // CHECK - See if user already exists in Firestore
    fun userExists(uid: String, onResult: (Boolean) -> Unit) {
        usersCollection.document(uid)
            .get()
            .addOnSuccessListener { doc -> onResult(doc.exists()) }
            .addOnFailureListener { onResult(false) }
    }
}