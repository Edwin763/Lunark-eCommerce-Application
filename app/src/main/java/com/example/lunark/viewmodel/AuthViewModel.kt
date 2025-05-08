package com.example.lunark.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.lunark.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import okhttp3.internal.concurrent.Task

class AuthViewModel : ViewModel() {

    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    fun login(email: String, password: String,onResult: (Boolean,String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onResult(true,"Success")
                } else {
                    onResult(false,it.exception?.localizedMessage)
                }

                }

    }

    fun signup(firstName: String, lastName: String, email: String, password: String,onResult: (Boolean,String?) -> Unit)  {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    var userId = it.result?.user?.uid
                    val userModel = UserModel(firstName, lastName, email,userId!!)
                    firestore.collection("users").document(userId)
                        .set(userModel)
                        .addOnCompleteListener{ dbTask ->
                            if(dbTask.isSuccessful){
                                onResult(true,"Success")
                            }else{
                                onResult(false,"Something went wrong")
                            }


                        }

                }else{
                    onResult(false,it.exception?.localizedMessage)
                }
            }



    }
    fun logout(navController: NavController, context: Context) {
        auth.signOut()
        Toast.makeText(context, "Successfully logged out", Toast.LENGTH_LONG).show()
        navController.navigate("login") {
            popUpTo("home") { inclusive = true }
        }
    }
}