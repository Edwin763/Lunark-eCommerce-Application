package com.example.lunark.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.lunark.AppUtil
import com.example.lunark.GlobalNavigation
import com.example.lunark.model.ProductModel
import com.example.lunark.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.tbuonomo.viewpagerdotsindicator.compose.DotsIndicator
import com.tbuonomo.viewpagerdotsindicator.compose.model.DotGraphic
import com.tbuonomo.viewpagerdotsindicator.compose.type.ShiftIndicatorType
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsView(modifier: Modifier = Modifier, productId: String, navController: NavController? = null) {
    val backgroundColor = Color(0xFF927BBF)
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            backgroundColor.copy(alpha = 0.8f),
            backgroundColor.copy(alpha = 0.5f),
            Color.Transparent
        )
    )

    var product by remember { mutableStateOf(ProductModel()) }
    val context = LocalContext.current
    var isFavorite by remember { mutableStateOf(false) }
    var favoriteCount by remember { mutableStateOf(0L) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    // Function to check favorite status
    val checkFavoriteStatus = {
        if (userId != null) {
            Firebase.firestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val user = document.toObject(UserModel::class.java)
                        if (user != null) {
                            val count = user.favouriteItems[productId] ?: 0L
                            isFavorite = count > 0
                            favoriteCount = count
                        }
                    }
                }
        }
    }

    // Load product data and favorite status
    LaunchedEffect(productId) {
        // Load product data
        Firebase.firestore.collection("data").document("stock")
            .collection("products")
            .document(productId).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result.toObject(ProductModel::class.java)?.let { result ->
                        product = result
                    }
                }
            }

        // Check favorite status
        checkFavoriteStatus()
    }

    // Set up a listener for favorite changes
    DisposableEffect(userId, productId) {
        if (userId == null) return@DisposableEffect onDispose { }

        val listener = Firebase.firestore.collection("users")
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                val user = snapshot.toObject(UserModel::class.java)
                if (user != null) {
                    val count = user.favouriteItems[productId] ?: 0L
                    isFavorite = count > 0
                    favoriteCount = count
                }
            }

        onDispose {
            listener.remove()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Product Details",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController?.popBackStack() ?: GlobalNavigation.navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Go Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { GlobalNavigation.navController.navigate("cart") }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Shopping Cart",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Background Image (Car)
            product.images.firstOrNull()?.let { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Product Image",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .align(Alignment.TopCenter)
                )

                // Add a gradient overlay on top of image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(gradientBackground)
                )
            }

            // Content Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 200.dp) // Push content down to show part of background image
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Color.White)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Product Title with shadow
                Text(
                    text = product.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp)
                )

                // Image Carousel
                Column {
                    val pagerState = rememberPagerState(pageCount = { product.images.size })

                    HorizontalPager(
                        state = pagerState,
                        pageSpacing = 24.dp
                    ) { page ->
                        Card(
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .height(220.dp)
                                .fillMaxWidth()
                        ) {
                            if (product.images.isNotEmpty() && page < product.images.size) {
                                AsyncImage(
                                    model = product.images[page],
                                    contentDescription = "Product Image ${page + 1}",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (product.images.size > 1) {
                        DotsIndicator(
                            dotCount = product.images.size,
                            type = ShiftIndicatorType(
                                DotGraphic(
                                    color = backgroundColor,
                                    size = 8.dp
                                )
                            ),
                            pagerState = pagerState
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Price and Favorite Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            if (product.price > product.actualPrice) {
                                Text(
                                    text = "$${product.price}",
                                    fontSize = 16.sp,
                                    color = Color.Gray,
                                    style = TextStyle(textDecoration = TextDecoration.LineThrough)
                                )
                            }
                            Text(
                                text = "$${product.actualPrice}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = backgroundColor
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Favorite counter
                        if (favoriteCount > 0) {
                            Text(
                                text = "$favoriteCount",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Red,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }

                        // Favorite button with colorful background
                        FloatingActionButton(
                            onClick = {
                                if (isFavorite) {
                                    AppUtil.removeFromFavourite(context, productId)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Removed from favorites")
                                    }
                                } else {
                                    AppUtil.addToFavourite(context, productId)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Added to favorites")
                                    }
                                }
                            },
                            containerColor = if (isFavorite) Color(0xFFFFEBEE) else Color(0xFFF5F5F5),
                            contentColor = if (isFavorite) Color.Red else backgroundColor
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (isFavorite) "Remove from Favorites" else "Add to Favorites"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            AppUtil.addToCart(context, productId)
                            scope.launch {
                                snackbarHostState.showSnackbar("Added to cart")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = backgroundColor
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        shape = RoundedCornerShape(28.dp),
                        border = ButtonDefaults.outlinedButtonBorder
                    ) {
                        Text(text = "Add to Cart", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            AppUtil.addToCart(context, productId)
                            GlobalNavigation.navController.navigate("cart")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = backgroundColor,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text(text = "Buy Now", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Description Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Product Description",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = product.description,
                            fontSize = 16.sp,
                            color = Color.DarkGray,
                            lineHeight = 24.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Product Details Section
                if (product.otherDetails.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Specifications",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            product.otherDetails.forEach { (key, value) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        text = "$key: ",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.Black,
                                        modifier = Modifier.width(120.dp)
                                    )
                                    Text(
                                        text = value,
                                        fontSize = 16.sp,
                                        color = Color.DarkGray
                                    )
                                }
                                Divider(color = Color(0xFFEEEEEE))
                            }
                        }
                    }
                }

                // Add space at the bottom for better scrolling experience
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}