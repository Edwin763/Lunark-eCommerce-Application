package com.example.lunark.admin



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.navigation.NavController
import com.example.lunark.GlobalNavigation
import com.example.lunark.screens.AppColors
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController? = null) {
    // Admin profile info
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("Admin") }

    // Settings state
    var darkModeEnabled by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var analyticsEnabled by remember { mutableStateOf(true) }
    var autoBackupEnabled by remember { mutableStateOf(true) }
    var twoFactorEnabled by remember { mutableStateOf(false) }

    // Dialog states
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }
    var showBackupDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    // Fetch admin info when screen is first displayed
    LaunchedEffect(Unit) {
        Firebase.firestore
            .collection("users")
            .document(Firebase.auth.currentUser!!.uid)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    name = it.result.getString("firstName") ?: ""
                    email = it.result.getString("email") ?: ""
                    role = it.result.getString("role") ?: "Admin"
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Settings",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.secondaryColor,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Profile section
            item {
                SettingsSection(title = "Profile") {
                    ProfileCard(name = name, email = email, role = role)

                    SettingsItem(
                        icon = Icons.Default.Edit,
                        title = "Edit Profile",
                        subtitle = "Update your account information",
                        onClick = { GlobalNavigation.navController.navigate(("edit-profile"))}
                    )

                    SettingsItem(
                        icon = Icons.Default.Lock,
                        title = "Change Password",
                        subtitle = "Update your security credentials",
                        onClick = { showChangePasswordDialog = true }
                    )
                }
            }

            // Appearance section
            item {
                SettingsSection(title = "Appearance") {
                    SwitchSettingsItem(
                        icon = Icons.Default.DarkMode,
                        title = "Dark Mode",
                        subtitle = "Toggle dark theme",
                        checked = darkModeEnabled,
                        onCheckedChange = { darkModeEnabled = it }
                    )
                }
            }

            // Notifications section
            item {
                SettingsSection(title = "Notifications") {
                    SwitchSettingsItem(
                        icon = Icons.Default.Notifications,
                        title = "Push Notifications",
                        subtitle = "Receive alerts about important events",
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )
                }
            }

            // Security section
            item {
                SettingsSection(title = "Security & Privacy") {
                    SwitchSettingsItem(
                        icon = Icons.Default.Security,
                        title = "Two-Factor Authentication",
                        subtitle = "Add an extra layer of security",
                        checked = twoFactorEnabled,
                        onCheckedChange = { twoFactorEnabled = it }
                    )
                }
            }

            // System section
            item {
                SettingsSection(title = "System") {
                    SwitchSettingsItem(
                        icon = Icons.Default.Analytics,
                        title = "Analytics",
                        subtitle = "Collect anonymous usage statistics",
                        checked = analyticsEnabled,
                        onCheckedChange = { analyticsEnabled = it }
                    )

                    SwitchSettingsItem(
                        icon = Icons.Default.Backup,
                        title = "Auto Backup",
                        subtitle = "Automatically backup database",
                        checked = autoBackupEnabled,
                        onCheckedChange = { autoBackupEnabled = it }
                    )

                    SettingsItem(
                        icon = Icons.Default.Save,
                        title = "Manual Backup",
                        subtitle = "Create a backup of the current database",
                        onClick = { showBackupDialog = true }
                    )

                    SettingsItem(
                        icon = Icons.Default.RestartAlt,
                        title = "Reset App Data",
                        subtitle = "Clear all app data and start fresh",
                        onClick = { showResetDialog = true },
                        textColor = Color.Red
                    )
                }
            }

            // Account section
            item {
                SettingsSection(title = "Account") {
                    SettingsItem(
                        icon = Icons.Default.Logout,
                        title = "Logout",
                        subtitle = "Sign out of your account",
                        onClick = { showLogoutConfirmDialog = true },
                        textColor = Color.Red
                    )
                }
            }

            // About section
            item {
                SettingsSection(title = "About") {
                    SettingsItem(
                        icon = Icons.Default.Info,
                        title = "App Info",
                        subtitle = "Version 1.0.0 (Build 1024)",
                        onClick = { /* Show app info */ }
                    )

                    SettingsItem(
                        icon = Icons.Default.Help,
                        title = "Help & Support",
                        subtitle = "Get assistance and read FAQs",
                        onClick = { /* Navigate to help */ }
                    )

                    SettingsItem(
                        icon = Icons.Default.Description,
                        title = "Terms & Privacy Policy",
                        subtitle = "Read our legal documents",
                        onClick = { /* Show terms */ }
                    )
                }
            }

            // Add some bottom padding
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Change Password Dialog
    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showChangePasswordDialog = false },
            onConfirm = { currentPassword, newPassword ->
                // Handle password change logic here
                showChangePasswordDialog = false
            }
        )
    }

    // Logout Confirmation Dialog
    if (showLogoutConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                Button(
                    onClick = {
                        Firebase.auth.signOut()
                        navController?.navigate("auth") {
                            popUpTo("adminHome") { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showLogoutConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Backup Dialog
    if (showBackupDialog) {
        AlertDialog(
            onDismissRequest = { showBackupDialog = false },
            title = { Text("Create Backup") },
            text = { Text("This will create a backup of all system data. Continue?") },
            confirmButton = {
                Button(
                    onClick = {
                        // Handle backup creation logic
                        showBackupDialog = false
                    }
                ) {
                    Text("Create Backup")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showBackupDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Reset Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset App Data", color = Color.Red) },
            text = {
                Text("WARNING: This will permanently delete all app data and cannot be undone. Are you absolutely sure?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Handle app reset logic
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Reset Everything")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Section title
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = AppColors.primaryColor,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Section content card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                content()
            }
        }
    }
}

@Composable
fun ProfileCard(
    name: String,
    email: String,
    role: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile avatar
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(AppColors.primaryColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.firstOrNull()?.toString() ?: "A",
                fontSize = 24.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Profile info
        Column {
            Text(
                text = name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.secondaryColor
            )

            Text(
                text = email,
                fontSize = 14.sp,
                color = Color.Gray
            )

            Text(
                text = role,
                fontSize = 12.sp,
                color = AppColors.primaryColor,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(AppColors.primaryColor.copy(alpha = 0.1f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    textColor: Color = Color.Unspecified
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AppColors.primaryColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Text content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (textColor != Color.Unspecified) textColor else Color.Black
                )

                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // Chevron icon
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }

    Divider(
        color = Color(0xFFEEEEEE),
        thickness = 1.dp,
        modifier = Modifier.padding(start = 56.dp)
    )
}

@Composable
fun SwitchSettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AppColors.primaryColor,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Text content
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        // Switch
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AppColors.primaryColor,
                checkedBorderColor = AppColors.primaryColor,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.LightGray
            )
        )
    }

    Divider(
        color = Color(0xFFEEEEEE),
        thickness = 1.dp,
        modifier = Modifier.padding(start = 56.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var currentPasswordError by remember { mutableStateOf("") }
    var newPasswordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Change Password",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Current Password
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = {
                        currentPassword = it
                        currentPasswordError = ""
                    },
                    label = { Text("Current Password") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = currentPasswordError.isNotEmpty(),
                    supportingText = {
                        if (currentPasswordError.isNotEmpty()) Text(currentPasswordError)
                    },
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                )

                // New Password
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = {
                        newPassword = it
                        newPasswordError = ""
                    },
                    label = { Text("New Password") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = newPasswordError.isNotEmpty(),
                    supportingText = {
                        if (newPasswordError.isNotEmpty()) Text(newPasswordError)
                    },
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                )

                // Confirm New Password
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        confirmPasswordError = ""
                    },
                    label = { Text("Confirm New Password") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = confirmPasswordError.isNotEmpty(),
                    supportingText = {
                        if (confirmPasswordError.isNotEmpty()) Text(confirmPasswordError)
                    },
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Validate inputs
                    var hasError = false

                    if (currentPassword.isBlank()) {
                        currentPasswordError = "Current password is required"
                        hasError = true
                    }

                    if (newPassword.isBlank()) {
                        newPasswordError = "New password is required"
                        hasError = true
                    } else if (newPassword.length < 6) {
                        newPasswordError = "Password must be at least 6 characters"
                        hasError = true
                    }

                    if (confirmPassword != newPassword) {
                        confirmPasswordError = "Passwords don't match"
                        hasError = true
                    }

                    if (!hasError) {
                        onConfirm(currentPassword, newPassword)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.primaryColor)
            ) {
                Text("Change Password")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}