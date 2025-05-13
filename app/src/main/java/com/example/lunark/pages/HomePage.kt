package com.example.lunark.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lunark.components.BannerView
import com.example.lunark.components.CategoriesView
import com.example.lunark.components.HeaderView

import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun HomePage(modifier: Modifier = Modifier) {
    val backColor = Color.Black
    val primaryColor = Color(0xFF927BBF)
    val primaryVariantColor = Color(0xFF7C67A5)  // Slightly darker variant for gradient
    val scrollState = rememberScrollState()

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = primaryColor,
            darkIcons = true
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
                .verticalScroll(scrollState)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 25.dp, bottom = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(primaryColor, primaryVariantColor)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(12.dp)
                ) {
                    HeaderView(
                        modifier = Modifier.fillMaxWidth()
                    )



                }
            }


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)  // Reduced height
                    .clip(RoundedCornerShape(12.dp))
            ) {
                BannerView(
                    modifier = Modifier.fillMaxSize()
                )



            }

            Spacer(modifier = Modifier.height(16.dp))


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Categories",
                    style = TextStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )


            }

            Spacer(modifier = Modifier.height(8.dp))


            CategoriesView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )




            Spacer(modifier = Modifier.height(64.dp))
        }
    }
}