package com.example.lunark.pages

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lunark.GlobalNavigation.navController
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


    // Show error message if any
    LaunchedEffect(profileState.error) {
        if (profileState.error != null) {
            // In a real app, you would show a snackbar or toast
            // For now, just clear the error after displaying it
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
                actions = {
                    IconButton(onClick = navigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
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
                CircularProgressIndicator()
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
                        onEditClick = navigateToEditProfile
                    )
                }

                // Recent Orders Section
                item {
                    Text(
                        text = "Recent Orders",
                        style = MaterialTheme.typography.titleLarge,
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
                                    onClick = { navigateToOrderDetails(order.id) }
                                )
                            }
                        }
                    }

                    item {
                        TextButton(
                            onClick = navigateToAllOrders,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("View All Orders")
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
                            onClick = { navController.navigate("home")} // Navigate to shop
                        )
                    }
                }

                // Saved Addresses Section
                item {
                    Text(
                        text = "Saved Addresses",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }

                // Show addresses or empty state
                if (profileState.addresses.isNotEmpty()) {
                    items(profileState.addresses) { address ->
                        AddressCard(
                            address = address,
                            onClick = navigateToAddresses
                        )
                    }
                } else {
                    item {
                        EmptyStateCard(
                            icon = Icons.Outlined.LocationOn,
                            message = "You haven't saved any addresses yet",
                            buttonText = "Add Address",
                            onClick = navigateToAddresses
                        )
                    }
                }

                // Account Settings Section
                item {
                    Text(
                        text = "Account Settings",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )

                    SettingsMenuCard(
                        navigateToEditProfile = navigateToEditProfile,
                        navigateToAddresses = navigateToAddresses,
                        navigateToSettings = navigateToSettings,
                        navigateToHelp = navigateToHelp,
                        onSignOutClick = { showSignOutDialog = true }
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
            title = { Text("Sign Out") },
            text = { Text("Are you sure you want to sign out?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.signOut()
                        onSignOut()
                        showSignOutDialog = false
                    }
                ) {
                    Text("Sign Out")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showSignOutDialog = false }
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
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${firstName.firstOrNull() ?: ""}${lastName.firstOrNull() ?: ""}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Edit Button
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Profile"
                )
            }
        }
    }
}

@Composable
fun OrderCard(order: Order, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(260.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Order Status
            val statusColor = when(order.status) {
                "Delivered" -> Color(0xFF4CAF50)
                "Shipped" -> Color(0xFF2196F3)
                "Processing" -> Color(0xFFFF9800)
                "Cancelled" -> Color(0xFFF44336)
                else -> MaterialTheme.colorScheme.primary
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
                    color = statusColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Divider()

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
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun AddressCard(address: Address, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

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
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "Default",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = address.fullAddress,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Address"
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
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(buttonText)
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
    onSignOutClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            SettingsMenuItem(
                icon = Icons.Default.Person,
                title = "Edit Profile",
                onClick = navigateToEditProfile
            )

            Divider(modifier = Modifier.padding(start = 56.dp))

            SettingsMenuItem(
                icon = Icons.Default.LocationOn,
                title = "Manage Addresses",
                onClick = navigateToAddresses
            )

            Divider(modifier = Modifier.padding(start = 56.dp))

            SettingsMenuItem(
                icon = Icons.Default.CreditCard,
                title = "Payment Methods",
                onClick = {}
            )

            Divider(modifier = Modifier.padding(start = 56.dp))

            SettingsMenuItem(
                icon = Icons.Default.Notifications,
                title = "Notification Preferences",
                onClick = navigateToSettings
            )

            Divider(modifier = Modifier.padding(start = 56.dp))

            SettingsMenuItem(
                icon = Icons.Default.Help,
                title = "Help & Support",
                onClick = navigateToHelp
            )

            Divider(modifier = Modifier.padding(start = 56.dp))

            SettingsMenuItem(
                icon = Icons.Default.ExitToApp,
                title = "Sign Out",
                onClick = onSignOutClick,
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun SettingsMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit,
    tint: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = tint,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}