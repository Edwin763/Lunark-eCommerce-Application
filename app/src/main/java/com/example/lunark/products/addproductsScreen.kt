package com.example.lunark.products

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.lunark.GlobalNavigation
import com.example.lunark.R
import com.example.lunark.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(navController: NavController) {
    // Define our color scheme based on the provided purple color
    val backgroundColor = Color(0xFF927BBF)
    val lightPurple = Color(0xFFB7A4D7)
    val darkPurple = Color(0xFF6B5B99)
    val accentColor = Color(0xFFF8F7FA)
    val textColor = Color(0xFF2E2639)
    val buttonTextColor = Color.White

    val context = LocalContext.current
    val imageUri = rememberSaveable() { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent())
    { uri: Uri? -> uri?.let { imageUri.value = it } }

    var productId by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var actualPrice by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    // For other details
    val otherDetails = remember { mutableStateMapOf<String, String>() }
    var currentKey by remember { mutableStateOf("") }
    var currentValue by remember { mutableStateOf("") }
    var isAddingDetail by remember { mutableStateOf(false) }

    val categories = listOf("electronics", "fashion", "footwear", "pet supp", "groceries", "health", "kitchen")

    val productViewModel: ProductViewModel = viewModel()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(lightPurple, backgroundColor)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                color = darkPurple,
                shape = RoundedCornerShape(10.dp),
                shadowElevation = 4.dp
            ) {
                Text(
                    text = "Add New Product",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = accentColor,
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                )
            }

            // Image selector with styled card
            Card(
                shape = CircleShape,
                modifier = Modifier
                    .padding(10.dp)
                    .size(200.dp)
                    .shadow(8.dp, CircleShape),
                colors = CardDefaults.cardColors(containerColor = accentColor)
            ) {
                AsyncImage(
                    model = imageUri.value ?: R.drawable.ic_person,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape)
                        .border(4.dp, backgroundColor, CircleShape)
                        .clickable { launcher.launch("image/*") }
                )
            }

            Text(
                text = "Tap to add product image",
                modifier = Modifier.padding(bottom = 16.dp),
                color = darkPurple,
                fontWeight = FontWeight.Medium
            )

            // We'll style text fields directly with their properties

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Product Title") },
                placeholder = { Text("Enter product title", color = backgroundColor) },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(accentColor.copy(alpha = 0.7f), RoundedCornerShape(4.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = darkPurple,
                    unfocusedBorderColor = backgroundColor,
                    cursorColor = darkPurple,
                    focusedLabelColor = darkPurple,
                    unfocusedLabelColor = backgroundColor,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                placeholder = { Text("Enter product description", color = backgroundColor) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(accentColor.copy(alpha = 0.7f), RoundedCornerShape(4.dp)),
                singleLine = false,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = darkPurple,
                    unfocusedBorderColor = backgroundColor,
                    cursorColor = darkPurple,
                    focusedLabelColor = darkPurple,
                    unfocusedLabelColor = backgroundColor,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Price (before discount)") },
                placeholder = { Text("Enter product price", color = backgroundColor) },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(accentColor.copy(alpha = 0.7f), RoundedCornerShape(4.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = darkPurple,
                    unfocusedBorderColor = backgroundColor,
                    cursorColor = darkPurple,
                    focusedLabelColor = darkPurple,
                    unfocusedLabelColor = backgroundColor,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = actualPrice,
                onValueChange = { actualPrice = it },
                label = { Text("Actual Price") },
                placeholder = { Text("Enter actual price", color = backgroundColor) },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(accentColor.copy(alpha = 0.7f), RoundedCornerShape(4.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = darkPurple,
                    unfocusedBorderColor = backgroundColor,
                    cursorColor = darkPurple,
                    focusedLabelColor = darkPurple,
                    unfocusedLabelColor = backgroundColor,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category dropdown with styling
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    placeholder = { Text("Select or enter category", color = backgroundColor) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(accentColor.copy(alpha = 0.7f), RoundedCornerShape(4.dp)),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = darkPurple,
                        unfocusedBorderColor = backgroundColor,
                        cursorColor = darkPurple,
                        focusedLabelColor = darkPurple,
                        unfocusedLabelColor = backgroundColor,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    trailingIcon = {
                        Text(
                            text = "▼",
                            color = darkPurple,
                            modifier = Modifier.clickable { expanded = true }
                        )
                    }
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(accentColor)
                ) {
                    categories.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = option,
                                    color = darkPurple
                                )
                            },
                            onClick = {
                                category = option
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Other Details section
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                color = darkPurple,
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(
                    text = "Other Details",
                    fontWeight = FontWeight.Bold,
                    color = accentColor,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                )
            }

            if (otherDetails.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    otherDetails.forEach { (key, value) ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = accentColor.copy(alpha = 0.8f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "$key: $value",
                                    color = textColor,
                                    fontWeight = FontWeight.Medium
                                )
                                Button(
                                    onClick = { otherDetails.remove(key) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = darkPurple
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Remove", color = buttonTextColor)
                                }
                            }
                        }
                    }
                }
            }

            if (isAddingDetail) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = accentColor.copy(alpha = 0.8f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        OutlinedTextField(
                            value = currentKey,
                            onValueChange = { currentKey = it },
                            label = { Text("Detail Name") },
                            placeholder = { Text("e.g., Color, Size, Material", color = backgroundColor) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(accentColor.copy(alpha = 0.7f), RoundedCornerShape(4.dp)),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = darkPurple,
                                unfocusedBorderColor = backgroundColor,
                                cursorColor = darkPurple,
                                focusedLabelColor = darkPurple,
                                unfocusedLabelColor = backgroundColor,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = currentValue,
                            onValueChange = { currentValue = it },
                            label = { Text("Detail Value") },
                            placeholder = { Text("e.g., Red, Large, Cotton", color = backgroundColor) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(accentColor.copy(alpha = 0.7f), RoundedCornerShape(4.dp)),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = darkPurple,
                                unfocusedBorderColor = backgroundColor,
                                cursorColor = darkPurple,
                                focusedLabelColor = darkPurple,
                                unfocusedLabelColor = backgroundColor,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = { isAddingDetail = false },
                                modifier = Modifier.padding(end = 8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Gray
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Cancel", color = buttonTextColor)
                            }

                            Button(
                                onClick = {
                                    if (currentKey.isNotBlank() && currentValue.isNotBlank()) {
                                        otherDetails[currentKey] = currentValue
                                        currentKey = ""
                                        currentValue = ""
                                        isAddingDetail = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = darkPurple
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Add Detail", color = buttonTextColor)
                            }
                        }
                    }
                }
            } else {
                Button(
                    onClick = { isAddingDetail = true },
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = darkPurple
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("+ Add Detail", color = buttonTextColor)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { GlobalNavigation.navController.navigate("viewProduct") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Gray
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Cancel", color = buttonTextColor)
                }

                Button(
                    onClick = {
                        if (title.isBlank() || description.isBlank() || price.isBlank() ||
                            actualPrice.isBlank() || category.isBlank()
                        ) {
                            Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        imageUri.value?.let {
                            productViewModel.uploadProductWithImage(
                                uri = it,
                                context = context,
                                title = title,
                                description = description,
                                price = price,
                                actualPrice = actualPrice,
                                category = category,
                                otherDetails = otherDetails,
                                navController = navController,
                                productId = productId
                            )
                        } ?: Toast.makeText(context, "Please pick an image", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = darkPurple
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Save Product", color = buttonTextColor)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddProductScreenPreview() {
    AddProductScreen(rememberNavController())
}