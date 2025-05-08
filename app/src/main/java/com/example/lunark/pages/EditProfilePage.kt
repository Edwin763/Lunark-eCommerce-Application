package com.example.lunark.pages



import androidx.compose.foundation.layout.*
import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.util.Patterns
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.navigation.NavHostController
import com.example.lunark.model.UserModel
import com.example.lunark.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfilePage(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: ProfileViewModel = viewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.profileState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var firstName by remember { mutableStateOf(TextFieldValue("")) }
    var lastName by remember { mutableStateOf(TextFieldValue("")) }
    var email by remember { mutableStateOf(TextFieldValue("")) }

    var firstNameError by remember { mutableStateOf<String?>(null) }
    var lastNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }

    var lastSavedProfile by remember { mutableStateOf("") }
    var originalProfile by remember { mutableStateOf<UserModel?>(null) }


    // Load profile
    LaunchedEffect(state.user) {
        state.user?.let {
            originalProfile = it
            firstName = TextFieldValue(it.firstName)
            lastName = TextFieldValue(it.lastName)
            email = TextFieldValue(it.email)
        }
    }

    // Snackbar and back navigation after save
    LaunchedEffect(state.isLoading, state.error) {
        if (!state.isLoading && state.error == null) {
            val newProfileHash = "${firstName.text}_${lastName.text}_${email.text}"
            if (lastSavedProfile != newProfileHash) {
                lastSavedProfile = newProfileHash
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Profile updated successfully")
                    delay(1500)
                    onBack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = {navController.navigate("profile")}) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // First Name Field
                OutlinedTextField(
                    value = firstName,
                    onValueChange = {
                        firstName = it
                        firstNameError = null
                    },
                    label = { Text("First Name") },
                    isError = firstNameError != null,
                    modifier = Modifier.fillMaxWidth()
                )
                if (firstNameError != null) {
                    Text(text = firstNameError!!, color = MaterialTheme.colorScheme.error)
                }

                // Last Name Field
                OutlinedTextField(
                    value = lastName,
                    onValueChange = {
                        lastName = it
                        lastNameError = null
                    },
                    label = { Text("Last Name") },
                    isError = lastNameError != null,
                    modifier = Modifier.fillMaxWidth()
                )
                if (lastNameError != null) {
                    Text(text = lastNameError!!, color = MaterialTheme.colorScheme.error)
                }

                // Email Field
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = null
                    },
                    label = { Text("Email") },
                    isError = emailError != null,
                    modifier = Modifier.fillMaxWidth()
                )
                if (emailError != null) {
                    Text(text = emailError!!, color = MaterialTheme.colorScheme.error)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Cancel Button
                    Button(
                        onClick = {
                            // Reset to original profile data
                            originalProfile?.let {
                                firstName = TextFieldValue(it.firstName)
                                lastName = TextFieldValue(it.lastName)
                                email = TextFieldValue(it.email)
                            }
                            onBack() // Go back without saving changes
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Cancel")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cancel")
                    }

                    // Save Button
                    Button(
                        onClick = {
                            // Validate fields
                            val isValid = validateFields(
                                firstName.text,
                                lastName.text,
                                email.text,
                                onError = { field, message ->
                                    when (field) {
                                        "firstName" -> firstNameError = message
                                        "lastName" -> lastNameError = message
                                        "email" -> emailError = message
                                    }
                                }
                            )

                            if (isValid) {
                                viewModel.updateProfile(
                                    firstName.text,
                                    lastName.text,
                                    email.text
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = firstName.text != originalProfile?.firstName || lastName.text != originalProfile?.lastName || email.text != originalProfile?.email
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = "Save")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save Changes")
                    }
                }

                // Show general error
                state.error?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

fun validateFields(
    firstName: String,
    lastName: String,
    email: String,
    onError: (field: String, message: String) -> Unit
): Boolean {
    var valid = true

    if (firstName.isBlank()) {
        onError("firstName", "First name cannot be empty")
        valid = false
    }
    if (lastName.isBlank()) {
        onError("lastName", "Last name cannot be empty")
        valid = false
    }
    if (email.isBlank()) {
        onError("email", "Email cannot be empty")
        valid = false
    } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        onError("email", "Invalid email format")
        valid = false
    }

    return valid
}


