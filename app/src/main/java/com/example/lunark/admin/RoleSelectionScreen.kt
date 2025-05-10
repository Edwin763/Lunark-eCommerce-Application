package com.example.lunark.admin

import androidx.compose.ui.platform.LocalContext
import com.example.lunark.screens.AppColors



import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lunark.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSelectionScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    // Role selection state
    var selectedRole by remember { mutableStateOf<Role?>(null) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Get context for preferences manager
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.loginbanner),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .blur(if (isLoading) 3.dp else 0.dp)
        )

        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            AppColors.primaryColor.copy(alpha = 0.85f),
                            AppColors.secondaryColor.copy(alpha = 0.95f)
                        )
                    )
                )
        )

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Title
            Text(
                text = "Select Your Role",
                style = TextStyle(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    color = AppColors.textPrimary
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Subtitle
            Text(
                text = "Choose how you want to use the app",
                style = TextStyle(
                    fontSize = 18.sp,
                    color = AppColors.textSecondary
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Role selection card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AppColors.cardBackground.copy(alpha = 0.95f)
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 10.dp
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Role dropdown
                    ExposedDropdownMenuBox(
                        expanded = isDropdownExpanded,
                        onExpandedChange = { isDropdownExpanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedRole?.title ?: "Select Role",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown Arrow",
                                    tint = AppColors.primaryColor
                                )
                            },
                            leadingIcon = {
                                selectedRole?.icon?.let {
                                    Icon(
                                        imageVector = it,
                                        contentDescription = null,
                                        tint = AppColors.primaryColor
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AppColors.primaryColor,
                                unfocusedBorderColor = AppColors.primaryColor.copy(alpha = 0.5f),
                                focusedLabelColor = AppColors.primaryColor,
                                unfocusedLabelColor = AppColors.secondaryColor.copy(alpha = 0.7f),
                                focusedTextColor = AppColors.inputTextColor,
                                unfocusedTextColor = AppColors.inputTextColor,
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false },
                            modifier = Modifier
                                .background(Color.White)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            // User role option
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = AppColors.primaryColor
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "User",
                                            color = AppColors.inputTextColor
                                        )
                                    }
                                },
                                onClick = {
                                    selectedRole = Role.User
                                    isDropdownExpanded = false
                                }
                            )

                            // Admin role option
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.AdminPanelSettings,
                                            contentDescription = null,
                                            tint = AppColors.primaryColor
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "Admin",
                                            color = AppColors.inputTextColor
                                        )
                                    }
                                },
                                onClick = {
                                    selectedRole = Role.Admin
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Selected role description
                    selectedRole?.let {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = it.icon,
                                contentDescription = null,
                                tint = AppColors.primaryColor,
                                modifier = Modifier.size(48.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = it.description,
                                textAlign = TextAlign.Center,
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    // Continue button
                    Button(
                        onClick = {
                            selectedRole?.let { role ->
                                isLoading = true
                                // Navigate based on role
                                when (role) {
                                    Role.User -> navController.navigate("home") {
                                        popUpTo("roleSelection") { inclusive = true }
                                    }
                                    Role.Admin -> navController.navigate("adminHome") {
                                        popUpTo("roleSelection") { inclusive = true }
                                    }
                                }
                            }
                        },
                        enabled = selectedRole != null && !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.primaryColor,
                            contentColor = Color.White,
                            disabledContainerColor = AppColors.primaryColor.copy(alpha = 0.5f)
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = if (isLoading) "Loading..." else "Continue",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

// Role data class to store role information
sealed class Role(val title: String, val icon: ImageVector, val description: String) {
    object User : Role(
        "User",
        Icons.Default.Person,
        "Access user features such as viewing content, managing your profile, and interacting with other users."
    )

    object Admin : Role(
        "Admin",
        Icons.Default.AdminPanelSettings,
        "Manage users, content, and system settings with administrative privileges."
    )
}