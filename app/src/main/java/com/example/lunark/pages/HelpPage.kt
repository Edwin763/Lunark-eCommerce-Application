package com.example.lunark.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.lunark.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpPage(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: ProfileViewModel, // Provided for consistency, even if unused
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help & Support") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Frequently Asked Questions",
                style = MaterialTheme.typography.titleMedium
            )

            HelpItem(question = "How can I update my profile?", answer = "Navigate to the profile page and tap 'Edit Profile'.")
            HelpItem(question = "How do I add a new address?", answer = "Go to the Addresses section and use the 'Add Address' form.")
            HelpItem(question = "How can I view my orders?", answer = "Visit the Orders page from your profile.")
            HelpItem(question = "How do I contact support?", answer = "Send an email to support@lunark.com or use the feedback form on the website.")

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Need more help?",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Reach out to our support team anytime at:\n\nEmail: support@lunark.com\nPhone: +1 (123) 456-7890"
            )
        }
    }
}

@Composable
fun HelpItem(question: String, answer: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = question, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = answer, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
