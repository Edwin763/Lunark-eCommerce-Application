package com.example.lunark.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.lunark.R
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun AuthScreen(modifier: Modifier = Modifier, navController: NavHostController) {
    // Set system bars color to match the primary theme color
    val systemUiController = rememberSystemUiController()
    val primaryColor = MaterialTheme.colorScheme.primary
    val backgroundColor = Color(0xFF927BBF)


    SideEffect {
        systemUiController.setSystemBarsColor(color = backgroundColor)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.authbanner),
            contentDescription = "Illustration showing online shopping",
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Welcome to Lunark",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = backgroundColor
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Shop Smart. Shop Lunark.",
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Cursive,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick = { navController.navigate("login") },
            colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(text = "Login", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { navController.navigate("signup") },
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = backgroundColor // Text and icon color
            ),
            border = BorderStroke(1.dp, backgroundColor),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(text = "Sign Up", fontSize = 18.sp, color = backgroundColor)
        }
    }
}