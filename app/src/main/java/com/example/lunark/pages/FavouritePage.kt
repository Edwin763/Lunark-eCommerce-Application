package com.example.lunark.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lunark.AppUtil
import com.example.lunark.GlobalNavigation
import com.example.lunark.components.FavouriteItemView
import com.example.lunark.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouritePage(modifier: Modifier = Modifier, navController: NavController) {
    val userModel = remember { mutableStateOf(UserModel()) }
    val backgroundColor = Color(0xFF927BBF)
    var isLoading by remember { mutableStateOf(true) }
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    // Listen for user favorites updates
    DisposableEffect(key1 = userId) {
        if (userId == null) {
            isLoading = false
            return@DisposableEffect onDispose { }
        }

        val listener = FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                isLoading = false
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val result = snapshot.toObject(UserModel::class.java)
                    if (result != null) {
                        userModel.value = result
                    }
                }
            }

        onDispose {
            listener.remove()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Top App Bar with back button
        TopAppBar(
            title = {
                Text(
                    text = "Your Favourites",
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
            actions = {
                IconButton(onClick = { GlobalNavigation.navController.navigate("cart") }) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Shopping Cart",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = backgroundColor
            )
        )

        // Content
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = backgroundColor
                    )
                }
                userModel.value.favouriteItems.isEmpty() -> {
                    // Empty state
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(80.dp))
                        Text(
                            text = "No favorites yet",
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Text(
                            text = "Items you add to your favorites will appear here",
                            style = TextStyle(
                                fontSize = 16.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { GlobalNavigation.navController.navigate("home") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = backgroundColor
                            ),
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height(50.dp)
                        ) {
                            Text("Explore Products")
                        }
                    }
                }
                else -> {
                    // Favorites list
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        items(
                            userModel.value.favouriteItems.toList(),
                            key = { it.first }
                        ) { (productId, qty) ->
                            FavouriteItemView(
                                productId = productId,
                                qty = qty,
                                onRemoveClicked = {
                                    // Optional callback if you need to do something after removal
                                },
                                onNavigateToDetails = { id ->
                                    navController.navigate("product-details/$id")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
