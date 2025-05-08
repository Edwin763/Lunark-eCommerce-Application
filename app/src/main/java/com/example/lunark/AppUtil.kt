package com.example.lunark

import android.content.Context
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

object AppUtil {
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun addToCart(context: Context,productId: String){
        val userDoc = Firebase.firestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)

        userDoc.get().addOnCompleteListener{
            if(it.isSuccessful){
                val currentCart = it.result.get("cartItems") as? Map<String,Long> ?: emptyMap()
                val currentQuantity = currentCart[productId]?:0
                val updatedQuantity = currentQuantity +1;
                val updatedCart = mapOf("cartItems.$productId" to updatedQuantity)

                userDoc.update(updatedCart)
                    .addOnCompleteListener{
                        if (it.isSuccessful){
                            showToast(context,"Item Added To Cart")

                        }else{
                            showToast(context,"Failed Adding Item To Cart")
                        }
                    }
            }
        }
    }

    fun removeFromCart(context: Context,productId: String,removeAll : Boolean = false){
        val userDoc = Firebase.firestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)

        userDoc.get().addOnCompleteListener{
            if(it.isSuccessful){
                val currentCart = it.result.get("cartItems") as? Map<String,Long> ?: emptyMap()
                val currentQuantity = currentCart[productId]?:0
                val updatedQuantity = currentQuantity -1;


                val updatedCart =
                    if (updatedQuantity<=0 || removeAll)
                        mapOf("cartItems.$productId" to FieldValue.delete())
                    else
                        mapOf("cartItems.$productId" to updatedQuantity)

                userDoc.update(updatedCart)
                    .addOnCompleteListener{
                        if (it.isSuccessful){
                            showToast(context,"Item Removed From The Cart")

                        }else{
                            showToast(context,"Failed Removing Item From The Cart")
                        }
                    }
            }
        }
    }

    fun getCurrentUserId(context: Context): String? {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser?.uid
    }

    fun getDiscountPercentage():Float{
        return 10.0f
    }

    fun getTaxPercentage():Float{
        return 13.0f
    }

    // FIX: Updated to correctly update favouriteItems instead of cartItems
    fun addToFavourite(context: Context,productId: String){
        val userDoc = Firebase.firestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)

        userDoc.get().addOnCompleteListener{
            if(it.isSuccessful){
                val currentFavourite = it.result.get("favouriteItems") as? Map<String,Long> ?: emptyMap()
                val currentQuantity = currentFavourite[productId]?:0
                val updatedQuantity = currentQuantity +1;
                // FIX: Changed cartItems to favouriteItems
                val updatedFavourite = mapOf("favouriteItems.$productId" to updatedQuantity)

                userDoc.update(updatedFavourite)
                    .addOnCompleteListener{
                        if (it.isSuccessful){
                            showToast(context,"Item Added To Favourites")
                        }else{
                            showToast(context,"Failed Adding Item To Favourites")
                        }
                    }
            }
        }
    }

    fun removeFromFavourite(context: Context,productId: String,removeAll : Boolean = false){
        val userDoc = Firebase.firestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)

        userDoc.get().addOnCompleteListener{
            if(it.isSuccessful){
                val currentFavourite = it.result.get("favouriteItems") as? Map<String,Long> ?: emptyMap()
                val currentQuantity = currentFavourite[productId]?:0
                val updatedQuantity = currentQuantity -1;

                val updatedFavourite =
                    if (updatedQuantity<=0 || removeAll)
                        mapOf("favouriteItems.$productId" to FieldValue.delete())
                    else
                        mapOf("favouriteItems.$productId" to updatedQuantity)

                userDoc.update(updatedFavourite)
                    .addOnCompleteListener{
                        if (it.isSuccessful){
                            showToast(context,"Item Removed From Favourites")
                        }else{
                            showToast(context,"Failed Removing Item From Favourites")
                        }
                    }
            }
        }
    }
}