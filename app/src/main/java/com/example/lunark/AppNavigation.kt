package com.example.lunark

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lunark.components.ProductDetailsView
import com.example.lunark.pages.AddressesPage
import com.example.lunark.pages.CartPage
import com.example.lunark.pages.CategoryProductPage
import com.example.lunark.pages.CheckoutPage
import com.example.lunark.pages.EditProfilePage
import com.example.lunark.pages.FavouritePage
import com.example.lunark.pages.HelpPage
import com.example.lunark.pages.OrderPage
import com.example.lunark.pages.ProfilePage
import com.example.lunark.pages.SettingsPage
import com.example.lunark.screens.AuthScreen
import com.example.lunark.screens.HomeScreen
import com.example.lunark.screens.LoginScreen

import com.example.lunark.screens.SignupScreen
import com.example.lunark.screens.SplashScreen
import com.example.lunark.viewmodel.ProfileViewModel

import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun Appnavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    GlobalNavigation.navController = navController
    val isLoggedIn = Firebase.auth.currentUser != null

    NavHost(navController = navController, startDestination = "splash") {
        composable(route = "splash") {
            SplashScreen(modifier, navController)
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
        composable("favorites") {
            FavouritePage(modifier,navController)
        }
        composable("cart") {
            CartPage(modifier, navController)
        }
        composable("checkout") {
            CheckoutPage(modifier)
        }


        // Profile route
        composable("profile") {
            ProfilePage(
                modifier = modifier,
                navigateToOrderDetails = { orderId ->
                    navController.navigate("order-details/$orderId")
                },
                navigateToEditProfile = {
                    navController.navigate("edit-profile")
                },
                navigateToAddresses = {
                    navController.navigate("addresses")
                },
                navigateToSettings = {
                    navController.navigate("settings")
                },
                navigateToHelp = {
                    navController.navigate("help")
                },
                navigateToAllOrders = {
                    navController.navigate("orders")
                },
                onSignOut = {
                    // Navigate back to auth screen after sign out
                    navController.navigate("auth") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        // Profile-related routes
        composable("edit-profile") {
            val profileViewModel: ProfileViewModel = viewModel()
            EditProfilePage(
                modifier,navController,
                viewModel = profileViewModel,
                onBack = { navController.popBackStack() }

            )

        }

        composable("addresses") {
            val profileViewModel: ProfileViewModel = viewModel()


            AddressesPage(
                modifier, navController,
                viewModel = profileViewModel,


                onBack = { navController.popBackStack()  }
            )

        }

        composable("settings") {
            SettingsPage(
                modifier,navController,
                onBack = { navController.popBackStack() },
                onSignOut = {

                    navController.navigate("login") {
                        popUpTo("profile") { inclusive = true }
                    }
                }
            )



        }

        composable("help") {
            val profileViewModel: ProfileViewModel = viewModel()

            HelpPage(
                modifier, navController,
                viewModel = profileViewModel,
                onBack = { navController.popBackStack()  }
            )
        }

        composable("orders") {

            val profileViewModel: ProfileViewModel = viewModel()
            OrderPage(
                modifier, navController,
                viewModel = profileViewModel,


                onBack = { navController.popBackStack()  }
            )

        }

        composable(
            "order-details/{orderId}",
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) {
            val orderId = it.arguments?.getString("orderId") ?: ""
            // TODO: Implement OrderDetailsPage
            // OrderDetailsPage(modifier, orderId, navController)
        }

        // Category and Product routes with proper navArguments
        composable(
            "category-products/{categoryId}",
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) {
            val categoryId = it.arguments?.getString("categoryId") ?: ""
            CategoryProductPage(modifier, categoryId)
        }

        composable(
            "product-details/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) {
            val productId = it.arguments?.getString("productId") ?: ""
            ProductDetailsView(modifier, productId)
        }
    }
}

object GlobalNavigation{
    lateinit var navController: NavHostController
}