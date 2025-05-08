package com.example.lunark.model

data class UserModel(
    val firstName: String="",
    val lastName: String="",
    val email: String="",
    val uid: String="",
    val cartItems:Map<String,Long> = emptyMap(),
    val address: String="",
    val favouriteItems:Map<String,Long> = emptyMap(),


//    val password: String

)
