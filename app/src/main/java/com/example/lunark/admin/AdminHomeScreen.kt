package com.example.lunark.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lunark.products.AddProductScreen
import com.example.lunark.screens.AppColors
import com.example.lunark.viewmodel.UserViewModel

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    viewModel: UserViewModel = viewModel(),
    navController: NavController
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }



    LaunchedEffect(Unit) {
        Firebase.firestore
            .collection("users")
            .document(Firebase.auth.currentUser!!.uid!!)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    name = it.result.getString("firstName") ?: ""
                    email = it.result.getString("email") ?: ""


                }
            }
    }
    LaunchedEffect(Unit) {
        viewModel.fetchUserCount()
    }

    val userCount by remember { mutableStateOf(viewModel.userCount) }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Dashboard", "Users", "Content", "Settings")

    // Sample data for the dashboard
    val statistics = listOf(
        Statistic("Total Users","7777", Icons.Default.People),
        Statistic("Active Users", "987", Icons.Default.PersonPin),
        Statistic("New Today", "12", Icons.Default.PersonAdd),
        Statistic("Content Views", "45.6K", Icons.Default.Visibility)
    )

    // Sample data for recent users
    val recentUsers = listOf(
        User("Flo vit","vit@gmail.com","Online"),
        User("Jane Smith", "jane.smith@example.com", "Offline"),
        User("Robert Johnson", "robert.j@example.com", "Online"),
        User("Alice Williams", "alice.w@example.com", "Online")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.primaryColor,
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { /* Handle notifications */ }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = { navController.navigate("auth") }) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = AppColors.primaryColor
            ) {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = when (index) {
                                    0 -> Icons.Default.Dashboard
                                    1 -> Icons.Default.People
                                    2 -> Icons.Default.Article
                                    else -> Icons.Default.Settings
                                },
                                contentDescription = title
                            )
                        },
                        label = { Text(title) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            when (selectedTab) {
                0 -> DashboardContent(statistics, recentUsers)
                1 -> AddProductScreen(navController)
                2 -> ContentManagementScreen()
                3 -> SettingsScreen()
            }
        }
    }
}

@Composable
fun DashboardContent(statistics: List<Statistic>, recentUsers: List<User>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Overview",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.secondaryColor
            )
        }

        // Statistics cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                statistics.take(2).forEach { stat ->
                    StatisticCard(
                        statistic = stat,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                statistics.takeLast(2).forEach { stat ->
                    StatisticCard(
                        statistic = stat,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Recent Users",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.secondaryColor
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Recent users list
        items(recentUsers) { user ->
            UserListItem(user = user)
        }

        // Quick actions
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Quick Actions",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.secondaryColor
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ActionButton(
                    icon = Icons.Default.PersonAdd,
                    text = "Add User",
                    onClick = { /* Add user action */ },
                    modifier = Modifier.weight(1f)
                )

                ActionButton(
                    icon = Icons.Default.Add,
                    text = "New Content",
                    onClick = { /* Add content action */ },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun StatisticCard(statistic: Statistic, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(AppColors.accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = statistic.icon,
                    contentDescription = null,
                    tint = AppColors.primaryColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = statistic.title,
                fontSize = 14.sp,
                color = Color.Gray
            )

            Text(
                text = statistic.value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.secondaryColor
            )
        }
    }
}

@Composable
fun UserListItem(user: User) {
//    var name by remember { mutableStateOf("") }
//    var email by remember { mutableStateOf("") }
//
//
//
//    LaunchedEffect(Unit) {
//        Firebase.firestore
//            .collection("users")
//            .document(Firebase.auth.currentUser!!.uid!!)
//            .get()
//            .addOnCompleteListener {
//                if (it.isSuccessful) {
//                    name = it.result.getString("firstName") ?: ""
//                    email = it.result.getString("email") ?: ""
//
//
//                }
//            }
//    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(AppColors.primaryColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.name.first().toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // User info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = user.name,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )

                Text(
                    text = user.email,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            // Status indicator
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (user.status == "Online") Color(0xFF4CAF50)
                        else Color(0xFFBDBDBD)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = user.status,
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun ActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = AppColors.primaryColor
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(text = text)
        }
    }
}

//@Composable
//fun UsersContent() {
//    // Placeholder for Users tab
//    Box(
//        modifier = Modifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        Text(text = "Users Management (Coming Soon)")
//    }
//}
//
@Composable
fun ContentManagementScreen() {
    // Placeholder for Content tab
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Content Management (Coming Soon)")
    }
}

@Composable
fun SettingsScreen() {
    // Placeholder for Settings tab
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Admin Settings (Coming Soon)")
    }
}

data class Statistic(
    val title: String,
    val value: String,
    val icon: ImageVector
)

data class User(
    val name: String,
    val email: String,
    val status: String
)