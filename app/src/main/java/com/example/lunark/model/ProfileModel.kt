package com.example.lunark.model



data class User(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = ""
)

data class Order(
    val id: String = "",
    val date: String = "",
    val status: String = "",
    val itemCount: Int = 0,
    val totalAmount: String = ""
)

data class Address(
    val id: String = "",
    val type: String = "",
    val fullAddress: String = "",
    val isDefault: Boolean = false
)