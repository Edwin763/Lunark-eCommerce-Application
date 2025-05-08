package com.example.lunark.pages



import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.lunark.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(

    modifier: Modifier = Modifier,
    navController: NavHostController,

    onBack: () -> Unit,
    onSignOut: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val state by viewModel.profileState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = {navController.navigate("profile")}) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Hello, ${state.user?.firstName ?: "User"}!",
                style = MaterialTheme.typography.titleLarge
            )

            Divider()

            // Example setting: Dark Mode toggle (optional, can be wired with real state)
            var isDarkMode by remember { mutableStateOf(false) }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isDarkMode = !isDarkMode },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Dark Mode", modifier = Modifier.weight(1f))
                Switch(checked = isDarkMode, onCheckedChange = { isDarkMode = it })
            }

            Divider()

            // Sign Out Option
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.signOut()
                        onSignOut()
                        navController.navigate("login")
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Sign Out",
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text("Sign Out")
            }
        }
    }
}
