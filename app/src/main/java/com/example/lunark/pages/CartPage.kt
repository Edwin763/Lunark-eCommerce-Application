package com.example.lunark.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.example.lunark.AppUtil
import com.example.lunark.GlobalNavigation
import com.example.lunark.components.CartItemView
import com.example.lunark.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.graphics.vector.ImageVector


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartPage(modifier: Modifier = Modifier, navController: NavController) {
    val userModel = remember { mutableStateOf(UserModel()) }
    val isLoading = remember { mutableStateOf(true) }
    val backgroundColor = Color(0xFF927BBF)

    // Firebase real-time updates for user data
    DisposableEffect(key1 = Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            val listener = FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .addSnapshotListener { snapshot, _ ->
                    snapshot?.toObject(UserModel::class.java)?.let {
                        userModel.value = it
                        isLoading.value = false
                    }
                }
            onDispose {
                listener.remove()
            }
        } else {
            isLoading.value = false
            onDispose {}
        }
    }

    // Show loading spinner if the data is being fetched
    if (isLoading.value) {
        CircularProgressIndicator(modifier = Modifier.fillMaxSize(), color = backgroundColor)
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp)  // Padding to provide space for the checkout button
        ) {
            // Top App Bar with back button
            item {
                TopAppBar(
                    title = {
                        Text(
                            text = "Your Cart",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack, // Back arrow icon
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = backgroundColor
                    )
                )
            }

            // Show empty cart message if there are no items
            if (userModel.value.cartItems.isEmpty()) {
                item {
                    Text(
                        text = "Your cart is empty",
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        fontSize = 18.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Cart items
            items(userModel.value.cartItems.toList(), key = { it.first }) { (productId, qty) ->
                CartItemView(productId = productId, qty = qty)
            }

            // Checkout button
            item {
                Button(
                    onClick = { navController.navigate("checkout") },
                    colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(28.dp),
                ) {
                    Text(text = "Checkout", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

