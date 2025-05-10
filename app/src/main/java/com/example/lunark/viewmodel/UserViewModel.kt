package com.example.lunark.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class UserViewModel : ViewModel() {
    var userCount by mutableStateOf(0L)
        private set

    private val firestore = FirebaseFirestore.getInstance()

    fun fetchUserCount() {
        firestore.collection("users")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    userCount = 0L
                    return@addSnapshotListener
                }
                userCount = snapshot?.size()?.toLong() ?: 0L
            }
    }
}