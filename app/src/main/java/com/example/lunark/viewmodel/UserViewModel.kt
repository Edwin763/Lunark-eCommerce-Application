package com.example.lunark.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class UserData(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val status: String = "Offline"
)

class UserViewModel : ViewModel() {
    val userCount = mutableStateOf(0)
    val users = mutableStateOf<List<UserData>>(emptyList())
    val recentUsers = mutableStateOf<List<UserData>>(emptyList())
    val isLoading = mutableStateOf(false)
    private val firestore = FirebaseFirestore.getInstance()

    fun fetchUserCount() {
        firestore.collection("users").get()
            .addOnSuccessListener { snapshot ->
                userCount.value = snapshot.size()
            }
    }

    fun fetchRecentUsers(limit: Int = 5) {
        firestore.collection("users")
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .get()
            .addOnSuccessListener { snapshot ->
                val usersList = snapshot.documents.map { doc ->
                    UserData(
                        id = doc.id,
                        firstName = doc.getString("firstName") ?: "",
                        lastName = doc.getString("lastName") ?: "",
                        email = doc.getString("email") ?: "",
                        status = if (doc.getBoolean("isOnline") == true) "Online" else "Offline"
                    )
                }
                recentUsers.value = usersList
            }
            .addOnFailureListener {
                // Handle any errors here
            }
    }

    fun fetchAllUsers() {
        isLoading.value = true
        firestore.collection("users").get()
            .addOnSuccessListener { snapshot ->
                val usersList = snapshot.documents.map { doc ->
                    UserData(
                        id = doc.id,
                        firstName = doc.getString("firstName") ?: "",
                        lastName = doc.getString("lastName") ?: "",
                        email = doc.getString("email") ?: "",
                        status = "Offline" // Default status, you can update this logic
                    )
                }
                users.value = usersList
                userCount.value = usersList.size
                isLoading.value = false
            }
            .addOnFailureListener {
                isLoading.value = false
                // Handle errors here
            }
    }

    fun deleteUser(userId: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        firestore.collection("users").document(userId)
            .delete()
            .addOnSuccessListener {
                // Remove the user from the local list
                users.value = users.value.filter { it.id != userId }
                userCount.value = users.value.size
                onSuccess()
            }
            .addOnFailureListener { e ->
                onError(e)
            }
    }

    fun addUser(userData: UserData, password: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        // This is a simplified example
        // In a real app, you'd need to create both Auth and Firestore entries
        // You might want to use Cloud Functions for this to handle it securely

        val timestamp = com.google.firebase.Timestamp.now()

        firestore.collection("users").add(
            mapOf(
                "firstName" to userData.firstName,
                "lastName" to userData.lastName,
                "email" to userData.email,
                "createdAt" to timestamp,
                "isOnline" to false
                // Add other fields as needed
            )
        )
            .addOnSuccessListener { documentRef ->

                val newUser = userData.copy(id = documentRef.id)
                users.value = users.value + newUser


                if (recentUsers.value.size < 5) {
                    recentUsers.value = listOf(newUser) + recentUsers.value
                } else {

                    recentUsers.value = listOf(newUser) + recentUsers.value.dropLast(1)
                }

                userCount.value = users.value.size
                onSuccess()
            }
            .addOnFailureListener { e ->
                onError(e)
            }
    }
}