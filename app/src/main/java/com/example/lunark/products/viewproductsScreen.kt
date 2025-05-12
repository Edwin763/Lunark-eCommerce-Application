package com.example.lunark.products

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewProductsScreen(navController: NavController) {
    // Define color scheme
    val backgroundColor = Color(0xFF927BBF)
    val lightPurple = Color(0xFFB29DDB)
    val darkPurple = Color(0xFF6A5687)
    val accentColor = Color(0xFFFFD54F)  // Gold accent
    val cardBackgroundColor = Color(0xFFF8F5FF)

    val context = LocalContext.current
    val productViewModel: ProductViewModel = viewModel()

    val emptyProduct = remember { mutableStateOf(ProductModel()) }
    val productsList = remember { mutableStateListOf<ProductModel>() }

    // Get products from Firestore
    val products = productViewModel.viewProducts(emptyProduct, productsList, context)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Products",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                )
            )
        },
        containerColor = Color(0xFFF0EAFB),  // Light purple background
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("addProduct") },
                containerColor = accentColor,
                contentColor = darkPurple,
                shape = CircleShape,
                modifier = Modifier.shadow(8.dp, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Product",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = Color(0xFFF0EAFB)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "All Products",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkPurple,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (products.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = backgroundColor,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(50.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Loading products or no products found",
                                color = darkPurple,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn {
                        items(products) { product ->
                            ProductCard(
                                product = product,
                                navController = navController,
                                productViewModel = productViewModel,
                                backgroundColor = backgroundColor,
                                lightPurple = lightPurple,
                                darkPurple = darkPurple,
                                accentColor = accentColor,
                                cardBackgroundColor = cardBackgroundColor
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
    productViewModel: ProductViewModel,
    backgroundColor: Color,
    lightPurple: Color,
    darkPurple: Color,
    accentColor: Color,
    cardBackgroundColor: Color
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Product Image with gradient overlay
            if (product.images.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    AsyncImage(
                        model = product.images.first(),
                        contentDescription = product.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Category badge with gradient background
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(lightPurple, backgroundColor)
                                )
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = product.category,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            } else {
                // Placeholder for missing image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(lightPurple.copy(alpha = 0.5f), lightPurple.copy(alpha = 0.2f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No Image Available",
                        color = darkPurple,
                        fontSize = 16.sp
                    )

                    // Category badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(lightPurple, backgroundColor)
                                )
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = product.category,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Product Title
            Text(
                text = product.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = darkPurple
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Price Section with styled layout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Ksh.${product.price}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = backgroundColor
                    )

                    if (product.actualPrice.isNotEmpty() && product.actualPrice != product.price) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ksh.${product.actualPrice}",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textDecoration = TextDecoration.LineThrough
                        )

                        // Calculate discount percentage
//                        try {
//                            val actualPrice = product.actualPrice.toFloat()
//                            val currentPrice = product.price.toFloat()
//                            if (actualPrice > currentPrice) {
//                                val discount = ((actualPrice - currentPrice) / actualPrice * 100).toInt()
//                                Spacer(modifier = Modifier.width(8.dp))
//                                Box(
//                                    modifier = Modifier
//                                        .clip(RoundedCornerShape(8.dp))
//                                        .background(accentColor.copy(alpha = 0.8f))
//                                        .padding(horizontal = 6.dp, vertical = 2.dp)
//                                ) {
//                                    Text(
//                                        text = "$discount% OFF",
//                                        fontSize = 12.sp,
//                                        fontWeight = FontWeight.Bold,
//                                        color = Color(0xFF333333)
//                                    )
//                                }
//                            }
//                        } catch (e: Exception) {
//                            // Handle number format exception silently
//                        }
                    }
                }
            }

            // Description Preview
            if (product.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
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
                Spacer(modifier = Modifier.height(12.dp))
                Divider(
                    color = lightPurple.copy(alpha = 0.5f),
                    thickness = 1.dp
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Additional Details:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkPurple
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Show up to 3 other details with styled look
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(lightPurple.copy(alpha = 0.1f))
                        .padding(12.dp)
                ) {
                    product.otherDetails.entries.take(3).forEach { (key, value) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = key,
                                fontSize = 14.sp,
                                color = darkPurple,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = value,
                                fontSize = 14.sp,
                                color = Color.DarkGray
                            )
                        }
                    }

                    // Indicate if there are more details
                    if (product.otherDetails.size > 3) {
                        Text(
                            text = "... and ${product.otherDetails.size - 3} more",
                            fontSize = 12.sp,
                            color = backgroundColor,
                            modifier = Modifier.padding(top = 4.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons with improved styling
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        productViewModel.deleteProduct(
                            context = context,
                            id = product.id,
                            navController = navController
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE57373).copy(alpha = 0.9f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete", fontSize = 14.sp)
                }

                Button(
                    onClick = {
                        navController.navigate("updateProduct/${product.id}")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = backgroundColor
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit", fontSize = 14.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ViewProductsScreenPreview() {
    ViewProductsScreen(rememberNavController())
}