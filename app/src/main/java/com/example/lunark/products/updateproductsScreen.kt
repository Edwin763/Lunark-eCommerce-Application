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
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    // Define color scheme based on the dominant color
    val backgroundColor = Color(0xFF927BBF)
    val lightPurple = Color(0xFFB29DDB)
    val darkPurple = Color(0xFF6A5687)
    val accentColor = Color(0xFFFFD54F)  // Gold accent
    val textColor = Color(0xFF333333)
    val cardBackgroundColor = Color(0xFFF8F5FF)

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
        .getReference().child("data/stock/products/$productId")

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Update Product",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = darkPurple
                )
            )
        },
        containerColor = Color(0xFFF0EAFB)  // Light purple background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Product Image Section
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .shadow(10.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(lightPurple, backgroundColor)
                        )
                    )
                    .border(4.dp, lightPurple, CircleShape)
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = imageUri.value ?: R.drawable.ic_person,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(190.dp)
                        .clip(CircleShape)
                )
            }

            Text(
                text = "Tap to update product image",
                color = darkPurple,
                modifier = Modifier.padding(vertical = 12.dp),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Main Form Content
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = cardBackgroundColor
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    CustomTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = "Product Title",
                        placeholder = "Enter product title",
                        backgroundColor = backgroundColor,
                        lightColor = lightPurple
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CustomTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = "Description",
                        placeholder = "Enter product description",
                        modifier = Modifier.height(100.dp),
                        singleLine = false,
                        backgroundColor = backgroundColor,
                        lightColor = lightPurple
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            CustomTextField(
                                value = price,
                                onValueChange = { price = it },
                                label = "Price",
                                placeholder = "Enter price",
                                backgroundColor = backgroundColor,
                                lightColor = lightPurple
                            )
                        }

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            CustomTextField(
                                value = actualPrice,
                                onValueChange = { actualPrice = it },
                                label = "Actual Price",
                                placeholder = "Enter actual price",
                                backgroundColor = backgroundColor,
                                lightColor = lightPurple
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Category dropdown
                    Column(modifier = Modifier.fillMaxWidth()) {
                        CustomTextField(
                            value = category,
                            onValueChange = { category = it },
                            label = "Category",
                            placeholder = "Select category",
                            backgroundColor = backgroundColor,
                            lightColor = lightPurple,
                            trailingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(lightPurple)
                                        .clickable { expanded = true },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "▼",
                                        color = Color.White,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        )

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .background(cardBackgroundColor)
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
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Other Details section with styled card
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = cardBackgroundColor
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Other Details",
                        fontWeight = FontWeight.Bold,
                        color = darkPurple,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    if (otherDetails.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        ) {
                            otherDetails.forEach { (key, value) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(lightPurple.copy(alpha = 0.15f))
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "$key: $value",
                                        color = darkPurple,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Button(
                                        onClick = { otherDetails.remove(key) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = darkPurple.copy(alpha = 0.7f)
                                        ),
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier.height(36.dp)
                                    ) {
                                        Text(
                                            "Remove",
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (isAddingDetail) {
                        CustomTextField(
                            value = currentKey,
                            onValueChange = { currentKey = it },
                            label = "Detail Name",
                            placeholder = "e.g., Color, Size, Material",
                            backgroundColor = backgroundColor,
                            lightColor = lightPurple
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        CustomTextField(
                            value = currentValue,
                            onValueChange = { currentValue = it },
                            label = "Detail Value",
                            placeholder = "e.g., Red, Large, Cotton",
                            backgroundColor = backgroundColor,
                            lightColor = lightPurple
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = { isAddingDetail = false },
                                modifier = Modifier.padding(end = 8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Gray
                                ),
                                shape = RoundedCornerShape(16.dp)
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
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = backgroundColor
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("Add Detail")
                            }
                        }
                    } else {
                        Button(
                            onClick = { isAddingDetail = true },
                            modifier = Modifier.align(Alignment.Start),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = accentColor
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "+ Add Detail",
                                color = textColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { navController.navigate("viewProducts") },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "CANCEL",
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {
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
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = darkPurple
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "UPDATE",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    singleLine: Boolean = true,
    backgroundColor: Color,
    lightColor: Color,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                color = backgroundColor
            )
        },
        placeholder = {
            Text(
                text = placeholder,
                color = Color.Gray.copy(alpha = 0.7f)
            )
        },
        modifier = modifier,
        singleLine = singleLine,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = backgroundColor,
            unfocusedBorderColor = lightColor,
            cursorColor = backgroundColor,
            focusedLabelColor = backgroundColor
        ),
        shape = RoundedCornerShape(12.dp),
        trailingIcon = trailingIcon
    )
}

@Preview(showBackground = true)
@Composable
fun UpdateProductScreenPreview() {
    UpdateProductScreen(rememberNavController(), "dummyId")
}