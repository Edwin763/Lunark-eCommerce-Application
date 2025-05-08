package com.example.lunark.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lunark.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel


class ProfileViewModel : ViewModel() {

    // State classes for profile screen
    data class ProfileState(
        val isLoading: Boolean = false,
        val user: UserModel? = null,
        val orders: List<Order> = emptyList(),
        val addresses: List<Address> = emptyList(),
        val error: String? = null
    )

    data class Order(
        val id: String = "",
        val date: String = "",
        val status: String = "",
        val totalAmount: String = "",
        val itemCount: Int = 0
    )

    data class Address(
        val id: String = "",
        val type: String = "", // "Home", "Work", etc.
        val fullAddress: String = "",
        val isDefault: Boolean = false
    )

    private val _profileState = MutableStateFlow(ProfileState(isLoading = true))
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _profileState.value = _profileState.value.copy(isLoading = true)
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val uid = currentUser.uid
                val userDocRef = firestore.collection("users").document(uid)

                userDocRef.get()
                    .addOnSuccessListener { document ->
                        if (document != null && document.exists()) {
                            val user = document.toObject(UserModel::class.java)
                            _profileState.value = _profileState.value.copy(
                                user = user,
                                isLoading = false
                            )
                            loadOrders(uid)
                            loadAddresses(uid)
                        } else {
                            _profileState.value = _profileState.value.copy(
                                isLoading = false,
                                error = "User data not found."
                            )
                        }
                    }
                    .addOnFailureListener { exception ->
                        _profileState.value = _profileState.value.copy(
                            isLoading = false,
                            error = exception.message
                        )
                    }
            } else {
                _profileState.value = _profileState.value.copy(
                    isLoading = false,
                    error = "User not authenticated."
                )
            }
        }
    }

    private fun loadOrders(uid: String) {
        firestore.collection("users").document(uid).collection("orders")
            .get()
            .addOnSuccessListener { result ->
                val orders = result.map { it.toObject(Order::class.java) }
                _profileState.value = _profileState.value.copy(orders = orders)
            }
            .addOnFailureListener { exception ->
                _profileState.value = _profileState.value.copy(error = exception.message)
            }
    }

    private fun loadAddresses(uid: String) {
        firestore.collection("users").document(uid).collection("addresses")
            .get()
            .addOnSuccessListener { result ->
                val addresses = result.map { it.toObject(Address::class.java) }
                _profileState.value = _profileState.value.copy(addresses = addresses)
            }
            .addOnFailureListener { exception ->
                _profileState.value = _profileState.value.copy(error = exception.message)
            }
    }

    fun updateProfile(firstName: String, lastName: String, email: String) {
        viewModelScope.launch {
            _profileState.value = _profileState.value.copy(isLoading = true)
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val uid = currentUser.uid
                val userUpdates = mapOf(
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "email" to email
                )
                firestore.collection("users").document(uid)
                    .update(userUpdates)
                    .addOnSuccessListener {
                        val updatedUser = _profileState.value.user?.copy(
                            firstName = firstName,
                            lastName = lastName,
                            email = email
                        )
                        _profileState.value = _profileState.value.copy(
                            user = updatedUser,
                            isLoading = false
                        )
                    }
                    .addOnFailureListener { exception ->
                        _profileState.value = _profileState.value.copy(
                            isLoading = false,
                            error = exception.message
                        )
                    }
            } else {
                _profileState.value = _profileState.value.copy(
                    isLoading = false,
                    error = "User not authenticated."
                )
            }
        }
    }

    fun addNewAddress(type: String, fullAddress: String, isDefault: Boolean) {
        viewModelScope.launch {
            _profileState.value = _profileState.value.copy(isLoading = true)
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val uid = currentUser.uid
                val addressId = firestore.collection("users").document(uid)
                    .collection("addresses").document().id
                val newAddress = Address(
                    id = addressId,
                    type = type,
                    fullAddress = fullAddress,
                    isDefault = isDefault
                )
                val addressesRef = firestore.collection("users").document(uid).collection("addresses")
                if (isDefault) {
                    // Set all other addresses to isDefault = false
                    addressesRef.get()
                        .addOnSuccessListener { result ->
                            val batch = firestore.batch()
                            for (doc in result) {
                                batch.update(doc.reference, "isDefault", false)
                            }
                            batch.commit()
                                .addOnSuccessListener {
                                    // Add the new address
                                    addressesRef.document(addressId).set(newAddress)
                                        .addOnSuccessListener {
                                            loadAddresses(uid)
                                            _profileState.value = _profileState.value.copy(isLoading = false)
                                        }
                                        .addOnFailureListener { exception ->
                                            _profileState.value = _profileState.value.copy(
                                                isLoading = false,
                                                error = exception.message
                                            )
                                        }
                                }
                                .addOnFailureListener { exception ->
                                    _profileState.value = _profileState.value.copy(
                                        isLoading = false,
                                        error = exception.message
                                    )
                                }
                        }
                        .addOnFailureListener { exception ->
                            _profileState.value = _profileState.value.copy(
                                isLoading = false,
                                error = exception.message
                            )
                        }
                } else {
                    // Add the new address without changing others
                    addressesRef.document(addressId).set(newAddress)
                        .addOnSuccessListener {
                            loadAddresses(uid)
                            _profileState.value = _profileState.value.copy(isLoading = false)
                        }
                        .addOnFailureListener { exception ->
                            _profileState.value = _profileState.value.copy(
                                isLoading = false,
                                error = exception.message
                            )
                        }
                }
            } else {
                _profileState.value = _profileState.value.copy(
                    isLoading = false,
                    error = "User not authenticated."
                )
            }
        }
    }

    fun removeAddress(addressId: String) {
        viewModelScope.launch {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val uid = currentUser.uid
                firestore.collection("users").document(uid).collection("addresses")
                    .document(addressId)
                    .delete()
                    .addOnSuccessListener {
                        loadAddresses(uid) // Refresh the list after deleting
                    }
                    .addOnFailureListener { exception ->
                        _profileState.value = _profileState.value.copy(error = exception.message)
                    }
            } else {
                _profileState.value = _profileState.value.copy(
                    error = "User not authenticated."
                )
            }
        }
    }



    fun signOut() {
        auth.signOut()
        _profileState.value = ProfileState()
    }

    fun clearError() {
        _profileState.value = _profileState.value.copy(error = null)
    }


}
