package com.example.lunark.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lunark.AppUtil
import com.example.lunark.R
import com.example.lunark.viewmodel.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.border
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput

// Define the color scheme for the app
object AppColors {
    val primaryColor = Color(0xFF927BBF)
    val secondaryColor = Color(0xFF7C67AD)
    val accentColor = Color(0xFFAB97D1)
    val textPrimary = Color.White
    val textSecondary = Color(0xFFEEEEEE)
    val inputTextColor = Color.Black
    val cardBackground = Color.White
    val errorColor = Color(0xFFFF6B6B)
}

@Composable
fun SignupScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    var firstname by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Form validation states
    var firstnameError by remember { mutableStateOf<String?>(null) }
    var lastnameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    // Animation states
    var showSuccessAnimation by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background with gradient overlay
        Image(
            painter = painterResource(id = R.drawable.signupbanner),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .blur(if (isLoading) 3.dp else 0.dp)
        )

        // Background that matches backgroundColor
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

        // Success Animation (shown when signup is successful)
        AnimatedVisibility(
            visible = showSuccessAnimation,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x99000000)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .size(200.dp)
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = AppColors.cardBackground
                    ),
                    shape = CircleShape
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = AppColors.primaryColor,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Success!",
                            color = AppColors.primaryColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                }
            }
        }

        // Main Content - Making it scrollable
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                androidx.compose.foundation.rememberScrollState().let { scrollState ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(scrollState)
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // App Logo or Brand Icon
                        Icon(
                            imageVector = Icons.Default.NightsStay, // Replace with your app icon
                            contentDescription = "App Logo",
                            tint = AppColors.textPrimary,
                            modifier = Modifier
                                .size(60.dp)
                                .padding(bottom = 8.dp)
                        )

                        // Title Text
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Join Us Today",
                                style = TextStyle(
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.SansSerif,
                                    color = AppColors.textPrimary
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Start your journey with us",
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    color = AppColors.textSecondary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Enhanced Card with styled UI
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                                .border(
                                    width = 1.dp,
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            AppColors.accentColor.copy(alpha = 0.5f),
                                            AppColors.primaryColor.copy(alpha = 0.2f)
                                        )
                                    ),
                                    shape = RoundedCornerShape(24.dp)
                                )
                                .shadow(
                                    elevation = 8.dp,
                                    shape = RoundedCornerShape(24.dp),
                                    spotColor = AppColors.primaryColor.copy(alpha = 0.5f)
                                ),
                            colors = CardDefaults.cardColors(
                                containerColor = AppColors.cardBackground.copy(alpha = 0.95f)
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 10.dp,
                                pressedElevation = 12.dp,
                                focusedElevation = 10.dp
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                // Decorative accent bar at the top of the card
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                                        .background(
                                            brush = Brush.horizontalGradient(
                                                colors = listOf(
                                                    AppColors.primaryColor,
                                                    AppColors.accentColor,
                                                    AppColors.primaryColor.copy(alpha = 0.7f)
                                                )
                                            )
                                        )
                                )

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp) // Account for the accent bar
                                        .background(
                                            brush = Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.White,
                                                    AppColors.cardBackground.copy(alpha = 0.97f),
                                                    Color.White.copy(alpha = 0.95f)
                                                )
                                            ),
                                            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                                        )
                                        .padding(horizontal = 24.dp, vertical = 32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // First Name Field
                                    OutlinedTextField(
                                        value = firstname,
                                        onValueChange = {
                                            firstname = it
                                            firstnameError = null
                                        },
                                        label = { Text("First Name") },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = AppColors.primaryColor,
                                            unfocusedBorderColor = AppColors.accentColor.copy(alpha = 0.5f),
                                            focusedLabelColor = AppColors.primaryColor,
                                            unfocusedLabelColor = AppColors.secondaryColor.copy(alpha = 0.7f),
                                            errorBorderColor = AppColors.errorColor,
                                            errorLabelColor = AppColors.errorColor,
                                            focusedTextColor = AppColors.inputTextColor,
                                            unfocusedTextColor = AppColors.inputTextColor,
                                        ),
                                        shape = RoundedCornerShape(16.dp),
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = "Person Icon",
                                                tint = AppColors.secondaryColor
                                            )
                                        },
                                        isError = firstnameError != null,
                                        supportingText = {
                                            if (firstnameError != null) {
                                                Text(
                                                    text = firstnameError ?: "",
                                                    color = AppColors.errorColor,
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                        }
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Last Name Field
                                    OutlinedTextField(
                                        value = lastname,
                                        onValueChange = {
                                            lastname = it
                                            lastnameError = null
                                        },
                                        label = { Text("Last Name") },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = AppColors.primaryColor,
                                            unfocusedBorderColor = AppColors.accentColor.copy(alpha = 0.5f),
                                            focusedLabelColor = AppColors.primaryColor,
                                            unfocusedLabelColor = AppColors.secondaryColor.copy(alpha = 0.7f),
                                            errorBorderColor = AppColors.errorColor,
                                            errorLabelColor = AppColors.errorColor,
                                            focusedTextColor = AppColors.inputTextColor,
                                            unfocusedTextColor = AppColors.inputTextColor,
                                        ),
                                        shape = RoundedCornerShape(16.dp),
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = "Person Icon",
                                                tint = AppColors.secondaryColor
                                            )
                                        },
                                        isError = lastnameError != null,
                                        supportingText = {
                                            if (lastnameError != null) {
                                                Text(
                                                    text = lastnameError ?: "",
                                                    color = AppColors.errorColor,
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                        }
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Email Field
                                    OutlinedTextField(
                                        value = email,
                                        onValueChange = {
                                            email = it
                                            emailError = null
                                        },
                                        label = { Text("Email") },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = AppColors.primaryColor,
                                            unfocusedBorderColor = AppColors.accentColor.copy(alpha = 0.5f),
                                            focusedLabelColor = AppColors.primaryColor,
                                            unfocusedLabelColor = AppColors.secondaryColor.copy(alpha = 0.7f),
                                            errorBorderColor = AppColors.errorColor,
                                            errorLabelColor = AppColors.errorColor,
                                            focusedTextColor = AppColors.inputTextColor,
                                            unfocusedTextColor = AppColors.inputTextColor,
                                        ),
                                        shape = RoundedCornerShape(16.dp),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Email,
                                                contentDescription = "Email Icon",
                                                tint = AppColors.secondaryColor
                                            )
                                        },
                                        isError = emailError != null,
                                        supportingText = {
                                            if (emailError != null) {
                                                Text(
                                                    text = emailError ?: "",
                                                    color = AppColors.errorColor,
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                        }
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Password Field
                                    OutlinedTextField(
                                        value = password,
                                        onValueChange = {
                                            password = it
                                            passwordError = null
                                        },
                                        label = { Text("Password") },
                                        modifier = Modifier.fillMaxWidth(),
                                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = AppColors.primaryColor,
                                            unfocusedBorderColor = AppColors.accentColor.copy(alpha = 0.5f),
                                            focusedLabelColor = AppColors.primaryColor,
                                            unfocusedLabelColor = AppColors.secondaryColor.copy(alpha = 0.7f),
                                            errorBorderColor = AppColors.errorColor,
                                            errorLabelColor = AppColors.errorColor,
                                            focusedTextColor = AppColors.inputTextColor,
                                            unfocusedTextColor = AppColors.inputTextColor,
                                        ),
                                        shape = RoundedCornerShape(16.dp),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = "Lock Icon",
                                                tint = AppColors.secondaryColor
                                            )
                                        },
                                        trailingIcon = {
                                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                                Icon(
                                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                    contentDescription = "Toggle Password Visibility",
                                                    tint = AppColors.secondaryColor
                                                )
                                            }
                                        },
                                        isError = passwordError != null,
                                        supportingText = {
                                            if (passwordError != null) {
                                                Text(
                                                    text = passwordError ?: "",
                                                    color = AppColors.errorColor,
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                        }
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Confirm Password Field
                                    OutlinedTextField(
                                        value = confirmPassword,
                                        onValueChange = {
                                            confirmPassword = it
                                            confirmPasswordError = null
                                        },
                                        label = { Text("Confirm Password") },
                                        modifier = Modifier.fillMaxWidth(),
                                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = AppColors.primaryColor,
                                            unfocusedBorderColor = AppColors.accentColor.copy(alpha = 0.5f),
                                            focusedLabelColor = AppColors.primaryColor,
                                            unfocusedLabelColor = AppColors.secondaryColor.copy(alpha = 0.7f),
                                            errorBorderColor = AppColors.errorColor,
                                            errorLabelColor = AppColors.errorColor,
                                            focusedTextColor = AppColors.inputTextColor,
                                            unfocusedTextColor = AppColors.inputTextColor
                                        ),
                                        shape = RoundedCornerShape(16.dp),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = "Lock Icon",
                                                tint = AppColors.secondaryColor
                                            )
                                        },
                                        trailingIcon = {
                                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                                Icon(
                                                    imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                    contentDescription = "Toggle Password Visibility",
                                                    tint = AppColors.secondaryColor
                                                )
                                            }
                                        },
                                        isError = confirmPasswordError != null,
                                        supportingText = {
                                            if (confirmPasswordError != null) {
                                                Text(
                                                    text = confirmPasswordError ?: "",
                                                    color = AppColors.errorColor,
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                        }
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Privacy Policy Text
//                                    Row(
//                                        verticalAlignment = Alignment.CenterVertically,
//                                        modifier = Modifier.fillMaxWidth()
//                                    ) {
//                                        Text(
//                                            text = "By signing up, you agree to our ",
//                                            color = Color.Gray,
//                                            fontSize = 12.sp,
//                                            textAlign = TextAlign.Center
//                                        )
//                                        TextButton(
//                                            onClick = { /* Navigate to Terms */ },
//                                            contentPadding = PaddingValues(0.dp)
//                                        ) {
//                                            Text(
//                                                text = "Terms",
//                                                color = AppColors.primaryColor,
//                                                fontSize = 12.sp,
//                                                fontWeight = FontWeight.Bold
//                                            )
//                                        }
//                                        Text(
//                                            text = " and ",
//                                            color = Color.Gray,
//                                            fontSize = 12.sp
//                                        )
//                                        TextButton(
//                                            onClick = { /* Navigate to Privacy Policy */ },
//                                            contentPadding = PaddingValues(0.dp)
//                                        ) {
//                                            Text(
//                                                text = "Privacy Policy",
//                                                color = AppColors.primaryColor,
//                                                fontSize = 12.sp,
//                                                fontWeight = FontWeight.Bold
//                                            )
//                                        }
//                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Signup Button
                                    Button(
                                        onClick = {
                                            // Validate form
                                            var hasError = false

                                            if (firstname.isBlank()) {
                                                firstnameError = "First name is required"
                                                hasError = true
                                            }

                                            if (lastname.isBlank()) {
                                                lastnameError = "Last name is required"
                                                hasError = true
                                            }

                                            if (email.isBlank()) {
                                                emailError = "Email is required"
                                                hasError = true
                                            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                                emailError = "Please enter a valid email"
                                                hasError = true
                                            }

                                            if (password.isBlank()) {
                                                passwordError = "Password is required"
                                                hasError = true
                                            } else if (password.length < 6) {
                                                passwordError = "Password must be at least 6 characters"
                                                hasError = true
                                            }

                                            if (confirmPassword.isBlank()) {
                                                confirmPasswordError = "Please confirm your password"
                                                hasError = true
                                            } else if (password != confirmPassword) {
                                                confirmPasswordError = "Passwords don't match"
                                                hasError = true
                                            }

                                            if (!hasError) {
                                                isLoading = true
                                                authViewModel.signup(firstname, lastname, email, password) { success, errorMessage ->
                                                    if (success) {
                                                        // Show success animation before navigating
                                                        showSuccessAnimation = true
                                                        scope.launch {
                                                            delay(1500) // Show animation for 1.5 seconds
                                                            isLoading = false
                                                            showSuccessAnimation = false
                                                            navController.navigate("roleSelection") {
                                                                popUpTo("auth") { inclusive = true }
                                                            }
                                                        }
                                                    } else {
                                                        isLoading = false
                                                        AppUtil.showToast(context, errorMessage ?: "Something went wrong")
                                                    }
                                                }
                                            }
                                        },
                                        enabled = !isLoading,
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
                                            text = if (isLoading) "Creating Account..." else "Create Account",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }

                                    // Social Sign-up Options
                                    Spacer(modifier = Modifier.height(24.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Divider(
                                            modifier = Modifier.weight(1f),
                                            color = Color.LightGray
                                        )
                                        Text(
                                            text = "Or continue with",
                                            color = Color.Gray,
                                            fontSize = 14.sp
                                        )
                                        Divider(
                                            modifier = Modifier.weight(1f),
                                            color = Color.LightGray
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Social Media Login Buttons
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        // Google
                                        IconButton(
                                            onClick = { /* Handle Google sign-up */ },
                                            modifier = Modifier
                                                .size(48.dp)
                                                .background(
                                                    color = Color.White,
                                                    shape = CircleShape
                                                )
                                                .border(
                                                    width = 1.dp,
                                                    color = Color.LightGray,
                                                    shape = CircleShape
                                                )
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Email, // Replace with Google icon
                                                contentDescription = "Sign up with Google",
                                                tint = Color.Red,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }

                                        // Facebook
                                        IconButton(
                                            onClick = { /* Handle Facebook sign-up */ },
                                            modifier = Modifier
                                                .size(48.dp)
                                                .background(
                                                    color = Color.White,
                                                    shape = CircleShape
                                                )
                                                .border(
                                                    width = 1.dp,
                                                    color = Color.LightGray,
                                                    shape = CircleShape
                                                )
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Facebook, // Replace with Facebook icon
                                                contentDescription = "Sign up with Facebook",
                                                tint = Color(0xFF1877F2),
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }

                                        // Apple
                                        IconButton(
                                            onClick = { /* Handle Apple sign-up */ },
                                            modifier = Modifier
                                                .size(48.dp)
                                                .background(
                                                    color = Color.White,
                                                    shape = CircleShape
                                                )
                                                .border(
                                                    width = 1.dp,
                                                    color = Color.LightGray,
                                                    shape = CircleShape
                                                )
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Phone, // Replace with Apple icon
                                                contentDescription = "Sign up with Apple",
                                                tint = Color.Black,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Sign In Text
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Already have an account? ",
                                color = AppColors.textSecondary,
                                fontSize = 16.sp
                            )
                            TextButton(onClick = { navController.popBackStack() }) {
                                Text(
                                    text = "Login",
                                    color = AppColors.textPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}