package com.example.lunark.admin

import androidx.compose.ui.text.input.PasswordVisualTransformation



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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lunark.screens.AppColors
import com.example.lunark.viewmodel.UserData
import com.example.lunark.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen(
    viewModel: UserViewModel = viewModel(),
    navController: NavController
) {
    var showAddUserDialog by remember { mutableStateOf(false) }
    var userToDelete by remember { mutableStateOf<UserData?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    val users by remember { viewModel.users }
    val isLoading by remember { viewModel.isLoading }

    // Fetch users when the screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.fetchAllUsers()
    }

    // Filter users based on search query
    val filteredUsers = remember(users, searchQuery) {
        if (searchQuery.isEmpty()) {
            users
        } else {
            users.filter {
                it.firstName.contains(searchQuery, ignoreCase = true) ||
                        it.lastName.contains(searchQuery, ignoreCase = true) ||
                        it.email.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top section with search and add button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "User Management",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.secondaryColor
            )

            Button(
                onClick = { showAddUserDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.primaryColor)
            ) {
                Icon(imageVector = Icons.Default.PersonAdd, contentDescription = "Add User")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add User")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search users...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = AppColors.primaryColor
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = AppColors.primaryColor
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppColors.primaryColor,
                cursorColor = AppColors.primaryColor
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Users list
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AppColors.primaryColor)
            }
        } else if (filteredUsers.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (searchQuery.isEmpty()) "No users found" else "No matching users",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredUsers) { user ->
                    UserCard(
                        user = user,
                        onDeleteClick = { userToDelete = user }
                    )
                }
            }
        }
    }

    // Add User Dialog
    if (showAddUserDialog) {
        AddUserDialog(
            onDismiss = { showAddUserDialog = false },
            onAddUser = { userData, password ->
                viewModel.addUser(
                    userData = userData,
                    password = password,
                    onSuccess = { showAddUserDialog = false },
                    onError = { /* Handle error */ }
                )
            }
        )
    }

    // Delete Confirmation Dialog
    if (userToDelete != null) {
        AlertDialog(
            onDismissRequest = { userToDelete = null },
            title = { Text("Delete User") },
            text = { Text("Are you sure you want to delete ${userToDelete?.firstName} ${userToDelete?.lastName}?") },
            confirmButton = {
                Button(
                    onClick = {
                        userToDelete?.id?.let { userId ->
                            viewModel.deleteUser(
                                userId = userId,
                                onSuccess = { userToDelete = null },
                                onError = { /* Handle error */ }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { userToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun UserCard(
    user: UserData,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(AppColors.primaryColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (user.firstName.firstOrNull() ?: "").toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // User details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${user.firstName} ${user.lastName}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = user.email,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Actions
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.Red
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserDialog(
    onDismiss: () -> Unit,
    onAddUser: (UserData, String) -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var firstNameError by remember { mutableStateOf("") }
    var lastNameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add New User",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // First Name
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it; firstNameError = "" },
                    label = { Text("First Name") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = firstNameError.isNotEmpty(),
                    supportingText = { if (firstNameError.isNotEmpty()) Text(firstNameError) }
                )

                // Last Name
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it; lastNameError = "" },
                    label = { Text("Last Name") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = lastNameError.isNotEmpty(),
                    supportingText = { if (lastNameError.isNotEmpty()) Text(lastNameError) }
                )

                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; emailError = "" },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = emailError.isNotEmpty(),
                    supportingText = { if (emailError.isNotEmpty()) Text(emailError) }
                )

                // Password
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; passwordError = "" },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = passwordError.isNotEmpty(),
                    supportingText = { if (passwordError.isNotEmpty()) Text(passwordError) },
                    visualTransformation = PasswordVisualTransformation()
                )

                // Confirm Password
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it; confirmPasswordError = "" },
                    label = { Text("Confirm Password") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = confirmPasswordError.isNotEmpty(),
                    supportingText = { if (confirmPasswordError.isNotEmpty()) Text(confirmPasswordError) },
                    visualTransformation = PasswordVisualTransformation()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Validate inputs
                    var hasError = false

                    if (firstName.isBlank()) {
                        firstNameError = "First name is required"
                        hasError = true
                    }

                    if (lastName.isBlank()) {
                        lastNameError = "Last name is required"
                        hasError = true
                    }

                    if (email.isBlank()) {
                        emailError = "Email is required"
                        hasError = true
                    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        emailError = "Invalid email format"
                        hasError = true
                    }

                    if (password.isBlank()) {
                        passwordError = "Password is required"
                        hasError = true
                    } else if (password.length < 6) {
                        passwordError = "Password must be at least 6 characters"
                        hasError = true
                    }

                    if (confirmPassword != password) {
                        confirmPasswordError = "Passwords don't match"
                        hasError = true
                    }

                    if (!hasError) {
                        val userData = UserData(
                            firstName = firstName,
                            lastName = lastName,
                            email = email
                        )
                        onAddUser(userData, password)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.primaryColor)
            ) {
                Text("Add User")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}