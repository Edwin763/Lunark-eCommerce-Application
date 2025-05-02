package com.example.lunark

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lunark.components.ProductDetailsView
import com.example.lunark.model.Screen
import com.example.lunark.pages.CategoryProductPage
import com.example.lunark.pages.ProfilePage
import com.example.lunark.screens.AuthScreen
import com.example.lunark.screens.HomeScreen
import com.example.lunark.screens.LoginScreen
import com.example.lunark.screens.SignupScreen
import com.example.lunark.screens.SplashScreen


import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun Appnavigation(modifier :Modifier = Modifier){

    val navController = rememberNavController()
    GlobalNavigation.navController = navController
    val isLoggedIn = Firebase.auth.currentUser != null
    val firstPage = if (isLoggedIn) "home" else "auth"

    NavHost(navController = navController, startDestination = "splash") {
        composable(route = "splash") {
            SplashScreen(modifier,navController)
        }

        composable("auth") {
            AuthScreen(modifier, navController)
        }
        composable("login") {
            LoginScreen(modifier, navController)
        }
        composable("signup") {
            SignupScreen(modifier, navController)
        }
        composable("home") {
            HomeScreen(modifier, navController)
        }
        composable("category-products/{categoryId}") {
            var categoryId = it.arguments?.getString("categoryId")
            CategoryProductPage(modifier, categoryId ?: "")
        }
        composable("product-details/{productId}") {
            var productId = it.arguments?.getString("productId")
            ProductDetailsView(modifier, productId ?: "")
        }
        composable(Screen.Profile.route) {
            ProfilePage(
                navigateToOrderDetails = { orderId ->
                    navController.navigate(Screen.OrderDetails(orderId).createRoute(orderId))
                },
                navigateToEditProfile = {
                    navController.navigate(Screen.EditProfile.route)
                },
                navigateToAddresses = {
                    navController.navigate(Screen.Addresses.route)
                },
                navigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                navigateToHelp = {
                    navController.navigate(Screen.Help.route)
                },
                navigateToAllOrders = {
                    navController.navigate(Screen.AllOrders.route)
                },
                onSignOut = {
                    navController.navigate("signup")
                }
            )
        }
        composable(Screen.EditProfile.route) {
            // EditProfileScreen()
        }
        composable(Screen.Addresses.route) {
            // AddressesScreen()
        }
        composable(Screen.Settings.route) {
            // SettingsScreen()
        }
        composable(Screen.Help.route) {
            // HelpScreen()
        }
        composable(Screen.AllOrders.route) {
            // AllOrdersScreen()
        }
        composable(
            route = Screen.OrderDetails("{orderId}").route,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: return@composable
            // OrderDetailsScreen(orderId)
        }
    }
    }

object GlobalNavigation{
    lateinit var navController: NavHostController
}