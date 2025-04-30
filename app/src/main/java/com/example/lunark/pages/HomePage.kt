package com.example.lunark.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lunark.R
import com.example.lunark.components.BannerView
import com.example.lunark.components.CategoriesView
import com.example.lunark.components.HeaderView
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun HomePage(modifier: Modifier = Modifier) {
    val backgroundColor = Color(0xFF927BBF)

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = backgroundColor,
            darkIcons = false
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Image(
            painter = painterResource(id = R.drawable.homepagebanner),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        ) {

            // Header Card with search icon
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 25.dp, start = 4.dp, end = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = backgroundColor
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    HeaderView(
                        modifier = Modifier.fillMaxWidth()
                    )

                    IconButton(onClick = { /* TODO: Handle search action */ },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(end = 16.dp)
                            .size(24.dp),
//
                        ) {

                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",


                    )}
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            BannerView(
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Categories",
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textDecoration = TextDecoration.Underline
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            CategoriesView(
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}