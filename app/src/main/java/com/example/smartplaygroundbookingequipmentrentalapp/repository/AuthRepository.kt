package com.example.smartplaygroundbookingequipmentrentalapp.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth: FirebaseAuth?
        get() {
            return try {
                FirebaseAuth.getInstance()
            } catch (e: Exception) {
                null
            }
        }

    suspend fun signIn(email: String, password: String): Result<FirebaseUser?> {
        val firebaseAuth = auth ?: return Result.failure(Exception("Firebase Auth unavailable"))
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUser(): FirebaseUser? = auth?.currentUser

    suspend fun getEmailByPhone(phone: String): String? {
        return try {
            val snapshot = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("users")
                .whereEqualTo("phone", phone)
                .limit(1)
                .get()
                .await()
            if (!snapshot.isEmpty) {
                snapshot.documents[0].getString("email")
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUserPhone(userId: String): String? {
        return try {
            val snapshot = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .get()
                .await()
            snapshot.getString("phone")
        } catch (e: Exception) {
            null
        }
    }

    fun logout() {
        auth?.signOut()
    }
}
