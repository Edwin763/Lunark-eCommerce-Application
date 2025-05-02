package com.example.lunark

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.example.lunark.ui.theme.LunarkTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    private var isAppReady by mutableStateOf(false)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { !isAppReady }
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        isAppReady = true
        setContent {
            LunarkTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Appnavigation(Modifier.padding(innerPadding))

                }
            }
        }
    }
}

