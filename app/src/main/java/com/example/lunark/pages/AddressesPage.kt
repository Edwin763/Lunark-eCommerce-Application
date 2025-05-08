package com.example.lunark.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.lunark.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressesPage(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: ProfileViewModel = viewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.profileState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var addressType by remember { mutableStateOf(TextFieldValue("")) }
    var fullAddress by remember { mutableStateOf(TextFieldValue("")) }
    var isDefault by remember { mutableStateOf(false) }

    fun addAddress() {
        if (addressType.text.isNotBlank() && fullAddress.text.isNotBlank()) {
            viewModel.addNewAddress(
                addressType.text,
                fullAddress.text,
                isDefault
            )
            addressType = TextFieldValue("")
            fullAddress = TextFieldValue("")
            isDefault = false
        } else {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Please fill in all fields.")
            }
        }
    }

    fun deleteAddress(addressId: String) {
        viewModel.removeAddress(addressId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Addresses") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
                OutlinedTextField(
                    value = addressType,
                    onValueChange = { addressType = it },
                    label = { Text("Address Type") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = fullAddress,
                    onValueChange = { fullAddress = it },
                    label = { Text("Full Address") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Default Address")
                    Checkbox(
                        checked = isDefault,
                        onCheckedChange = { isDefault = it }
                    )
                }

                Button(
                    onClick = { addAddress() },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Add Address")
                }

                if (state.addresses.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.addresses) { address ->
                            AddressItem(
                                address = address,
                                onDelete = { deleteAddress(address.id) }
                            )
                        }
                    }
                } else {
                    Text("No addresses found.")
                }
            }
        }
    }
}

@Composable
fun AddressItem(
    address: ProfileViewModel.Address,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Type: ${address.type}")
            Text("Address: ${address.fullAddress}")
            Text("Default: ${if (address.isDefault) "Yes" else "No"}")

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}
