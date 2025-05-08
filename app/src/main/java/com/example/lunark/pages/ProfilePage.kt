package com.example.lunark.pages

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lunark.GlobalNavigation
import com.example.lunark.GlobalNavigation.navController
import com.example.lunark.viewmodel.AuthViewModel
import com.example.lunark.viewmodel.ProfileViewModel
import com.example.lunark.viewmodel.ProfileViewModel.Order
import com.example.lunark.viewmodel.ProfileViewModel.Address

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage(
    modifier: Modifier = Modifier,
    navigateToOrderDetails: (String) -> Unit ={},
    navigateToEditProfile: () -> Unit={},
    navigateToAddresses: () -> Unit={},
    navigateToSettings: () -> Unit={},
    navigateToHelp: () -> Unit={},
    navigateToAllOrders: () -> Unit={},
    onSignOut: () -> Unit={},
    viewModel: ProfileViewModel = viewModel()
) {
    val profileState by viewModel.profileState.collectAsState()
    var showSignOutDialog by remember { mutableStateOf(false) }
    var orderId by remember { mutableStateOf(value = "") }
    val backgroundColor = Color(0xFF927BBF)
    val surfaceColor = Color(0xFFF8F5FF)

    // Show error message if any
    LaunchedEffect(profileState.error) {
        if (profileState.error != null) {
            // In a real app, you would show a snackbar or toast
            // For now, just clear the error after displaying it
            viewModel.clearError()
        }
    }

    Scaffold(
        containerColor = surfaceColor,
        topBar = {
            TopAppBar(
                title = { Text("My Profile", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {navController.navigate("settings")}) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White
                        )
                    }
                }
            )
        }

    ) { paddingValues ->
        if (profileState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = backgroundColor)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Profile Header
                item {
                    ProfileHeader(
                        firstName = profileState.user?.firstName ?: "",
                        lastName = profileState.user?.lastName ?: "",
                        email = profileState.user?.email ?: "",
                        onEditClick = {navController.navigate("edit-profile")},
                        backgroundColor = backgroundColor
                    )
                }

                // Recent Orders Section
                item {
                    Text(
                        text = "Recent Orders",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }

                // Show orders or empty state
                if (profileState.orders.isNotEmpty()) {
                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(profileState.orders) { order ->
                                OrderCard(
                                    order = order,
                                    onClick = {
                                        navController.navigate("order-details/$orderId")
                                    },
                                    backgroundColor = backgroundColor
                                )
                            }
                        }
                    }

                    item {
                        TextButton(
                            onClick = {navController.navigate("orders")},
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = backgroundColor
                            )
                        ) {
                            Text("View All Orders", fontWeight = FontWeight.SemiBold)
                            Icon(
                                Icons.Default.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                } else {
                    item {
                        EmptyStateCard(
                            icon = Icons.Outlined.ShoppingBag,
                            message = "You haven't placed any orders yet",
                            buttonText = "Start Shopping",
                            onClick = { navController.navigate("home")}, // Navigate to shop
                            backgroundColor = backgroundColor
                        )
                    }
                }

                // Saved Addresses Section
                item {
                    Text(
                        text = "Saved Addresses",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }

                // Show addresses or empty state
                if (profileState.addresses.isNotEmpty()) {
                    items(profileState.addresses) { address ->
                        AddressCard(
                            address = address,
                            onClick = {
                                navController.navigate("addresses")
                            },
                            backgroundColor = backgroundColor
                        )
                    }
                } else {
                    item {
                        EmptyStateCard(
                            icon = Icons.Outlined.LocationOn,
                            message = "You haven't saved any addresses yet",
                            buttonText = "Add Address",
                            onClick = {
                                navController.navigate("addresses")
                            },
                            backgroundColor = backgroundColor
                        )
                    }
                }

                // Account Settings Section
                item {
                    Text(
                        text = "Account Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )

                    SettingsMenuCard(
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
                        onSignOutClick = { showSignOutDialog = true },
                        backgroundColor = backgroundColor
                    )
                }

                // Add padding at the bottom
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }

    // Sign Out Confirmation Dialog
    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = { Text("Sign Out", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to sign out?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.signOut()
                        onSignOut()
                        showSignOutDialog = false
                        navController.navigate("login")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = backgroundColor
                    )
                ) {
                    Text("Sign Out")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showSignOutDialog = false
                        navController.navigate("profile")
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = backgroundColor
                    ),
                    border = BorderStroke(1.dp, backgroundColor)
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ProfileHeader(
    firstName: String,
    lastName: String,
    email: String,
    onEditClick: () -> Unit,
    backgroundColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Image
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(backgroundColor.copy(alpha = 0.8f))
                    .border(width = 2.dp, color = backgroundColor, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${firstName.firstOrNull() ?: ""}${lastName.firstOrNull() ?: ""}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Profile Information
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "$firstName $lastName",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            // Edit Button
            IconButton(
                onClick = {navController.navigate("edit-profile")},
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(backgroundColor.copy(alpha = 0.1f))
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Profile",
                    tint = backgroundColor
                )
            }
        }
    }
}

@Composable
fun OrderCard(order: Order, onClick: () -> Unit, backgroundColor: Color) {
    Card(
        modifier = Modifier
            .width(260.dp)
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(12.dp))
            .clickable(onClick = {
                navController.navigate("OrderPage")
            }),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Order #${order.id}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = order.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Order Status
            val statusColor = when(order.status) {
                "Delivered" -> backgroundColor
                "Shipped" -> backgroundColor
                "Processing" -> backgroundColor
                "Cancelled" -> Color(0xFFF44336)
                else -> backgroundColor
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = order.status,
                    style = MaterialTheme.typography.bodyMedium,
                    color = statusColor,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Divider(color = Color.LightGray.copy(alpha = 0.5f))

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${order.itemCount} item${if (order.itemCount > 1) "s" else ""}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = order.totalAmount,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = backgroundColor
                )
            }
        }
    }
}

@Composable
fun AddressCard(address: Address, onClick: () -> Unit, backgroundColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp))
            .clickable(onClick = {
                navController.navigate("addresses")
            }),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(backgroundColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = backgroundColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = address.type,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    if (address.isDefault) {
                        Spacer(modifier = Modifier.width(8.dp))

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = backgroundColor.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "Default",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = backgroundColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = address.fullAddress,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            IconButton(
                onClick = {
                    navController.navigate("addresses")
                },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(backgroundColor.copy(alpha = 0.1f))
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Address",
                    tint = backgroundColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyStateCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    message: String,
    buttonText: String,
    onClick: () -> Unit,
    backgroundColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(backgroundColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = backgroundColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = backgroundColor
                )
            ) {
                Text(
                    text = buttonText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SettingsMenuCard(
    navigateToEditProfile: () -> Unit,
    navigateToAddresses: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToHelp: () -> Unit,
    onSignOutClick: () -> Unit,
    backgroundColor: Color,
    authViewModel: AuthViewModel = viewModel()

) {
    var context= LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            SettingsMenuItem(
                icon = Icons.Default.Call,
                title = "Call us",
                onClick =
                    {
                        val intent = Intent(Intent.ACTION_DIAL).apply{
                            data = Uri.parse("tel:0745658725")
                        }
                        context.startActivity(intent)
                    },
                iconBackgroundColor = backgroundColor.copy(alpha = 0.1f),
                iconTint = backgroundColor,
                alwaysShowLabel = true
            )

            Divider(modifier = Modifier.padding(start = 56.dp), color = Color.LightGray.copy(alpha = 0.5f))


            SettingsMenuItem(
                icon = Icons.Default.Email,
                title = "Email Us",
                onClick =
                    {
                        val intent = Intent(Intent.ACTION_SENDTO).apply{
                            data = Uri.parse("mailto:edutush001@gmail.com")
                            putExtra(Intent.EXTRA_SUBJECT,"Inquiry")
                            putExtra(Intent.EXTRA_TEXT,"Hello, I am interested in your products!")

                        }
                        context.startActivity(intent)
                    }
                ,
                iconBackgroundColor = backgroundColor.copy(alpha = 0.1f),
                iconTint = backgroundColor,
                alwaysShowLabel = true
            )

            Divider(modifier = Modifier.padding(start = 56.dp), color = Color.LightGray.copy(alpha = 0.5f))



            SettingsMenuItem(
                icon = Icons.Default.Facebook,
                title = "Follow Us On Facebook",
                onClick = {

                    val facebookPageId = "100088486878700"
                    val facebookUrl = "https://www.facebook.com/profile.php?id=100088486878700"

                    val intent = try {
                        context.packageManager.getPackageInfo("com.facebook.katana", 0)
                        Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/$facebookPageId"))
                    } catch (e: PackageManager.NameNotFoundException) {
                        Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl))
                    }

                    context.startActivity(intent)

                },
                iconBackgroundColor = backgroundColor.copy(alpha = 0.1f),
                iconTint = backgroundColor,
                alwaysShowLabel = true
            )

            Divider(modifier = Modifier.padding(start = 56.dp), color = Color.LightGray.copy(alpha = 0.5f))


            SettingsMenuItem(
                icon = Icons.Default.Share,
                title = "Share Our App",
                onClick = {

                    val sendIntent = Intent().apply{
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT,"Download app here: https://play.google.com/store/apps/details?id=com.Lunark.package")
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent,null)
                    context.startActivity(shareIntent)
                },
                iconBackgroundColor = backgroundColor.copy(alpha = 0.1f),
                iconTint = backgroundColor,
                alwaysShowLabel = true
            )

            Divider(modifier = Modifier.padding(start = 56.dp), color = Color.LightGray.copy(alpha = 0.5f))

            SettingsMenuItem(
                icon = Icons.Default.CreditCard,
                title = "Payment Methods",
                onClick = {},
                iconBackgroundColor = backgroundColor.copy(alpha = 0.1f),
                iconTint = backgroundColor,
                alwaysShowLabel = true
            )

            Divider(modifier = Modifier.padding(start = 56.dp), color = Color.LightGray.copy(alpha = 0.5f))


            SettingsMenuItem(
                icon = Icons.Default.Help,
                title = "Help & Support",
                onClick = {GlobalNavigation.navController.navigate("help")},
                iconBackgroundColor = backgroundColor.copy(alpha = 0.1f),
                iconTint = backgroundColor,
                alwaysShowLabel = true
            )

            Divider(modifier = Modifier.padding(start = 56.dp), color = Color.LightGray.copy(alpha = 0.5f))

            SettingsMenuItem(
                icon = Icons.Default.ExitToApp,
                title = "Sign Out",
                onClick = {authViewModel.logout(navController, context)},
                iconBackgroundColor = Color(0xFFFFECEC),
                iconTint = Color(0xFFE53935),
                alwaysShowLabel = true
            )
        }
    }
}

@Composable
fun SettingsMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    iconBackgroundColor: Color,
    iconTint: Color,
    alwaysShowLabel: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(iconBackgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = Color.Black.copy(alpha = 0.8f),
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.Gray
        )
    }
}