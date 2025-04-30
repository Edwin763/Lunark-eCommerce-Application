package com.example.lunark

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lunark.components.ProductDetailsView
import com.example.lunark.pages.CategoryProductPage
import com.example.lunark.screens.AuthScreen
import com.example.lunark.screens.HomeScreen
import com.example.lunark.screens.LoginScreen
import com.example.lunark.screens.SignupScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun Appnavigation(modifier :Modifier = Modifier){

    val navController = rememberNavController()
    GlobalNavigation.navController = navController
    val isLoggedIn = Firebase.auth.currentUser != null
    val firstPage = if (isLoggedIn) "home" else "auth"

    NavHost(navController = navController, startDestination = "auth") {
        composable("auth") {
            AuthScreen(modifier,navController)
        }
        composable("login") {
            LoginScreen(modifier,navController)
        }
        composable("signup") {
            SignupScreen(modifier,navController)
        }
        composable("home") {
            HomeScreen(modifier,navController)
        }
        composable("category-products/{categoryId}") {
            var categoryId = it.arguments?.getString("categoryId")
            CategoryProductPage(modifier,categoryId?:"")
        }
        composable("product-details/{productId}") {
            var productId = it.arguments?.getString("productId")
            ProductDetailsView(modifier,productId?:"")
        }
    }
}

object GlobalNavigation{
    lateinit var navController: NavHostController
}