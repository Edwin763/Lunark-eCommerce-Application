package com.example.lunark.components

import android.content.ClipData
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.credentials.webauthn.Cbor
import coil.compose.AsyncImage
import com.example.lunark.GlobalNavigation
import com.example.lunark.model.CategoryModel
import com.example.lunark.ui.theme.Purple80
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@Composable
fun CategoriesView(modifier: Modifier = Modifier){
    val categoryList = remember { mutableStateOf<List<CategoryModel>>(emptyList())}

    LaunchedEffect(key1 = Unit) {
        Firebase.firestore.collection("data")
            .document("stock")
            .collection("categories")
            .get()
            .addOnCompleteListener(){
                if(it.isSuccessful){
                    val resultList = it.result.documents.mapNotNull { doc ->
                        doc.toObject(CategoryModel::class.java)

                    }
                    categoryList.value = resultList
                }
            }
    }
    LazyRow (
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ){
        items(categoryList.value){ item->
            CategoryItem(category = item)


        }
    }
}
@Composable
fun CategoryItem(category: CategoryModel){
    val backgroundColor = Color(0xFF927BBF)

    Card(
        modifier = Modifier.size(90.dp)
            .clickable {
                GlobalNavigation.navController.navigate("category-products/"+category.id)

            }
        ,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ){
            AsyncImage(model = category.imageUrl,
                contentDescription = "category.name",
                modifier = Modifier.size(50.dp).fillMaxWidth())

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = category.name,
                textAlign = TextAlign.Center
            )

        }
    }

}