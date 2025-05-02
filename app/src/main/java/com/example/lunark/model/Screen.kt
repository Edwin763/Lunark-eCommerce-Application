package com.example.lunark.model

sealed class Screen(val route: String) {
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    object Addresses : Screen("addresses")
    object Settings : Screen("settings")
    object Help : Screen("help")
    object AllOrders : Screen("all_orders")
    data class OrderDetails(val orderId: String) : Screen("order_details/{orderId}") {
        fun createRoute(orderId: String) = "order_details/$orderId"
    }
}
