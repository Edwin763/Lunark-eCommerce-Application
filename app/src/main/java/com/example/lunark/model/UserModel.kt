package com.example.lunark.model

data class UserModel(
    val firstName: String="",
    val lastName: String="",
    val email: String="",
    val uid: String="",
    val cartItems:Map<String,Long> = emptyMap(),
    val profileImageUrl: String = ""
//    val password: String

)
