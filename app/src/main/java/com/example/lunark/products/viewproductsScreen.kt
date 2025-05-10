package com.example.lunark.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage

import com.example.lunark.viewmodel.ProductViewModel
import com.example.lunark.model.ProductModel


@Composable
fun ViewProductsScreen(navController: NavController) {
    val context = LocalContext.current
    val productViewModel: ProductViewModel = viewModel()

    val emptyProduct = remember { mutableStateOf(ProductModel()) }
    val productsList = remember { mutableStateListOf<ProductModel>() }

    // Get products from Firestore
    val products = productViewModel.viewProducts(emptyProduct, productsList, context)

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("addProduct") },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Product"
                )
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "All Products",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (products.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Loading products or no products found")
                        }
                    }
                } else {
                    LazyColumn {
                        items(products) { product ->
                            ProductCard(
                                product = product,
                                navController = navController,
                                productViewModel = productViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    product: ProductModel,
    navController: NavController,
    productViewModel: ProductViewModel
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Product Image
            if (product.images.isNotEmpty()) {
                AsyncImage(
                    model = product.images.first(),
                    contentDescription = product.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Product Title
            Text(
                text = product.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Category
            Text(
                text = product.category,
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Price Section
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Ksh.${product.price}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(8.dp))

                if (product.actualPrice.isNotEmpty() && product.actualPrice != product.price) {
                    Text(
                        text = "Ksh.${product.actualPrice}",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textDecoration = TextDecoration.LineThrough
                    )

                    // Calculate discount percentage
//                    try {
//                        val actualPrice = product.actualPrice.toFloat()
//                        val currentPrice = product.price.toFloat()
//                        if (actualPrice > currentPrice) {
//                            val discount = ((actualPrice - currentPrice) / actualPrice * 100).toInt()
//                            Spacer(modifier = Modifier.width(8.dp))
//                            Text(
//                                text = "$discount% off",
//                                fontSize = 14.sp,
//                                color = Color(0xFF388E3C) // Green color
//                            )
//                        }
//                    } catch (e: Exception) {
                        // Handle number format exception silently
                    }
                }
            }

            // Description Preview
            if (product.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = product.description,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.DarkGray
                )
            }

            // Other Details Preview
            if (product.otherDetails.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Additional Details:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                // Show up to 3 other details
                product.otherDetails.entries.take(3).forEach { (key, value) ->
                    Text(
                        text = "$key: $value",
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                }

                // Indicate if there are more details
                if (product.otherDetails.size > 3) {
                    Text(
                        text = "... and ${product.otherDetails.size - 3} more",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        productViewModel.deleteProduct(
                            context = context,
                            id = product.id,
                            navController = navController
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Delete")
                }

                Button(
                    onClick = {
//                        navController.navigate("$updateProduct/${product.id}")
                    }
                ) {
                    Text("Edit")
                }
            }
        }
    }


@Preview(showBackground = true)
@Composable
fun ViewProductsScreenPreview() {
    ViewProductsScreen(rememberNavController())
}