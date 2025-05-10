package com.example.lunark.viewmodel

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.lunark.GlobalNavigation
import com.example.lunark.model.ProductModel
import com.example.lunark.network.ImgurService

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class ProductViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val productsCollection = db.collection("data/stock/products")
    private var listener: ListenerRegistration? = null

    private fun getImgurService(): ImgurService {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.imgur.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(ImgurService::class.java)
    }

    private fun getFileFromUri(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File.createTempFile("temp_image", ".jpg", context.cacheDir)
            file.outputStream().use { output ->
                inputStream?.copyTo(output)
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun uploadProductWithImage(
        uri: Uri,
        context: Context,
        title: String,
        description: String,
        price: String,
        actualPrice: String,
        category: String,
        otherDetails: Map<String, String>,
        navController: NavController,
        productId: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val file = getFileFromUri(context, uri)
                if (file == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Failed to process image", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("image", file.name, reqFile)

                val response = getImgurService().uploadImage(
                    body,
                    "Client-ID 0dcdfc2ddf61128"
                )

                if (response.isSuccessful) {
                    val imageUrl = response.body()?.data?.link ?: ""
                    val images = listOf(imageUrl)

                    val id = productsCollection.document().id

                    val product = ProductModel(
                        id = id,
                        title = title,
                        description = description,
                        price = price,
                        actualPrice = actualPrice,
                        category = category,
                        images = images,
                        otherDetails = otherDetails
                    )

                    try {
                        productsCollection.document(id).set(product).await()
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Product saved successfully", Toast.LENGTH_SHORT).show()
                            GlobalNavigation.navController.navigate("viewProduct")
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Failed to save product: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Upload error: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Exception: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun viewProducts(
        product: MutableState<ProductModel>,
        products: SnapshotStateList<ProductModel>,
        context: Context
    ): SnapshotStateList<ProductModel> {
        // Remove any existing listener to avoid duplicates
        listener?.remove()

        // Add a new listener
        listener = productsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Toast.makeText(context, "Failed to fetch products: ${error.message}", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            products.clear()
            snapshot?.documents?.forEach { document ->
                val productModel = document.toObject(ProductModel::class.java)
                productModel?.let {
                    products.add(it)
                }
            }

            if (products.isNotEmpty()) {
                product.value = products.first()
            }
        }

        return products
    }

    fun updateProduct(
        context: Context,
        navController: NavController,
        id: String,
        title: String,
        description: String,
        price: String,
        actualPrice: String,
        category: String,
        images: String,
        otherDetails: Map<String, String>
    ){
        viewModelScope.launch {
            try {
                val updatedProduct = ProductModel(
                    id = id,
                    title = title,
                    description = description,
                    price = price,
                    actualPrice = actualPrice,
                    category = category,
                    otherDetails = otherDetails
                )

                productsCollection.document(id).set(updatedProduct).await()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Product Updated Successfully", Toast.LENGTH_LONG).show()
                    GlobalNavigation.navController.navigate("viewProduct")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Product update failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun deleteProduct(
        context: Context,
        id: String,
        navController: NavController
    ){
        AlertDialog.Builder(context)
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete this product?")
            .setPositiveButton("Yes"){ _, _ ->
                viewModelScope.launch {
                    try {
                        productsCollection.document(id).delete().await()
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Product deleted Successfully", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Product not deleted: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            .setNegativeButton("No"){ dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    // Clean up listener when ViewModel is cleared
    override fun onCleared() {
        super.onCleared()
        listener?.remove()
    }
}