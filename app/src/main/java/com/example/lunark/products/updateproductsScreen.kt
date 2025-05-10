package com.example.lunark.products

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.lunark.R
import com.example.lunark.model.ProductModel
import com.example.lunark.viewmodel.ProductViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProductScreen(navController: NavController, productId: String) {
    val context = LocalContext.current
    val imageUri = rememberSaveable { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent())
    { uri: Uri? -> uri?.let { imageUri.value = it } }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var actualPrice by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var images by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }

    // For other details
    val otherDetails = remember { mutableStateMapOf<String, String>() }
    var currentKey by remember { mutableStateOf("") }
    var currentValue by remember { mutableStateOf("") }
    var isAddingDetail by remember { mutableStateOf(false) }

    val categories = listOf("electronics", "fashion", "footwear", "groceries", "health", "kitchen", "pet supp")

    val productViewModel: ProductViewModel = viewModel()

    // Load existing product data
    val currentDataRef = FirebaseDatabase.getInstance()
        .getReference().child("Products/$productId")

    DisposableEffect(Unit) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val product = snapshot.getValue(ProductModel::class.java)
                product?.let {
                    title = it.title
                    description = it.description
                    price = it.price
                    actualPrice = it.actualPrice
                    category = it.category

                    // Load other details
                    otherDetails.clear()
                    otherDetails.putAll(it.otherDetails)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
            }
        }
        currentDataRef.addValueEventListener(listener)
        onDispose { currentDataRef.removeEventListener(listener) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Update Product",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            shape = CircleShape,
            modifier = Modifier
                .padding(10.dp)
                .size(200.dp)
        ) {
            AsyncImage(
                model = imageUri.value ?: R.drawable.ic_person,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(200.dp)
                    .clickable { launcher.launch("image/*") }
            )
        }
        Text(
            text = "Tap to update product image",
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Product Title") },
            placeholder = { Text("Enter product title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            placeholder = { Text("Enter product description") },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            singleLine = false
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Price") },
            placeholder = { Text("Enter product price") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = actualPrice,
            onValueChange = { actualPrice = it },
            label = { Text("Actual Price") },
            placeholder = { Text("Enter actual price") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Category dropdown
        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category") },
                placeholder = { Text("Select or enter category") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Text(
                        text = "▼",
                        modifier = Modifier.clickable { expanded = true }
                    )
                }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                categories.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(text = option) },
                        onClick = {
                            category = option
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Other Details section
        Text(
            text = "Other Details",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )

        if (otherDetails.isNotEmpty()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                otherDetails.forEach { (key, value) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("$key: $value")
                        Button(onClick = { otherDetails.remove(key) }) {
                            Text("Remove")
                        }
                    }
                }
            }
        }

        if (isAddingDetail) {
            OutlinedTextField(
                value = currentKey,
                onValueChange = { currentKey = it },
                label = { Text("Detail Name") },
                placeholder = { Text("e.g., Color, Size, Material") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = currentValue,
                onValueChange = { currentValue = it },
                label = { Text("Detail Value") },
                placeholder = { Text("e.g., Red, Large, Cotton") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { isAddingDetail = false },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = {
                        if (currentKey.isNotBlank() && currentValue.isNotBlank()) {
                            otherDetails[currentKey] = currentValue
                            currentKey = ""
                            currentValue = ""
                            isAddingDetail = false
                        }
                    }
                ) {
                    Text("Add Detail")
                }
            }
        } else {
            Button(
                onClick = { isAddingDetail = true },
                modifier = Modifier.align(Alignment.Start)
            ) {
                Text("+ Add Detail")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { navController.navigate("viewProducts") }) {
                Text(text = "Cancel")
            }

            Button(onClick = {
                if (title.isBlank() || description.isBlank() || price.isBlank() ||
                    actualPrice.isBlank() || category.isBlank()) {
                    Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                // Update the product
                productViewModel.updateProduct(
                    context = context,
                    navController = navController,
                    id = productId,
                    title = title,
                    description = description,
                    price = price,
                    actualPrice = actualPrice,
                    category = category,
                    otherDetails = otherDetails,
                    images = images
                )

                // If there's a new image, upload it
                imageUri.value?.let {
                    productViewModel.uploadProductWithImage(
                        uri = it,
                        context = context,
                        productId = productId,
                        title = title,
                        description = description,
                        price = price,
                        actualPrice = actualPrice,
                        category = category,
                        otherDetails = otherDetails,
                        navController = navController
                    )
                }
            }) {
                Text(text = "UPDATE")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UpdateProductScreenPreview() {
    UpdateProductScreen(rememberNavController(), "dummyId")
}