package com.example.lunark.mpesa

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Base64
import java.io.IOException
import java.util.concurrent.TimeUnit
import retrofit2.HttpException

class MPesaService(private val context: Context) {
    private val BASE_URL = "https://sandbox.safaricom.co.ke/"
    private val CONSUMER_KEY = "3z8JLAbvVO1AJQdOFJ1SyiJJhcGbKR13LPCPPlXLD8JiQi5l"
    private val CONSUMER_SECRET = "uA05hIMUKgZsRgoInhUzwsHG91LX4juFUwwy70by2PMsfW17qYmd7I2pIL9qFhOn"
    private val BUSINESS_SHORT_CODE = "174379" // Standard sandbox shortcode
    // Correct sandbox passkey
    private val PASSKEY = "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919"
    // Get a unique URL from webhook.site
    private val CALLBACK_URL = "https://webhook.site/f016077a-87ee-49a0-abfd-f2134a61400d"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    private val api = retrofit.create(MpesaAPI::class.java)

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getAccessToken(): String {
        val credentials = "$CONSUMER_KEY:$CONSUMER_SECRET"
        val encodedCredentials = "Basic " + Base64.getEncoder().encodeToString(credentials.toByteArray())

        try {
            Log.d("MPesa", "Attempting to get access token")
            val response = api.getAccessToken(encodedCredentials)
            Log.d("MPesa", "Access token received: ${response.access_token}")
            return "Bearer " + response.access_token
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("MPesa", "HTTP Error ${e.code()}: $errorBody", e)
            throw Exception("Failed to get access token: ${e.code()} - $errorBody")
        } catch (e: IOException) {
            Log.e("MPesa", "Network Error getting access token", e)
            throw Exception("Network error: ${e.message}")
        } catch (e: Exception) {
            Log.e("MPesa", "Unexpected error getting access token", e)
            throw Exception("Unexpected error: ${e.message}")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun initiateSTKPush(
        phoneNumber: String,
        amount: String,
        accountReference: String,
        transactionDesc: String
    ): STKPushResponse {
        try {
            // Get access token first and log it
            val accessToken = getAccessToken()
            Log.d("MPesa", "Using access token: $accessToken")

            // Format timestamp
            val timestamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(Date())
            Log.d("MPesa", "Timestamp: $timestamp")

            // Generate password - log each part to ensure it's correct
            val passwordString = BUSINESS_SHORT_CODE + PASSKEY + timestamp
            Log.d("MPesa", "Password string: $passwordString")

            val password = Base64.getEncoder().encodeToString(passwordString.toByteArray())
            Log.d("MPesa", "Base64 Password: $password")

            // Format phone number
            val formattedPhone = when {
                phoneNumber.startsWith("0") -> "254" + phoneNumber.substring(1)
                phoneNumber.startsWith("+254") -> phoneNumber.substring(1)
                phoneNumber.startsWith("254") -> phoneNumber
                else -> {
                    Log.e("MPesa", "Invalid phone number format: $phoneNumber")
                    throw IllegalArgumentException("Invalid phone number format")
                }
            }
            Log.d("MPesa", "Formatted phone: $formattedPhone")

            // Create request
            val stkPushRequest = STKPushRequest(
                BusinessShortCode = BUSINESS_SHORT_CODE,
                Password = password,
                Timestamp = timestamp,
                TransactionType = "CustomerPayBillOnline",
                Amount = amount,
                PartyA = formattedPhone,
                PartyB = BUSINESS_SHORT_CODE,
                PhoneNumber = formattedPhone,
                CallBackURL = CALLBACK_URL,
                AccountReference = accountReference,
                TransactionDesc = transactionDesc
            )

            // Log the complete request
            Log.d("MPesa", "STK Push Request: $stkPushRequest")

            // Make the API call
            val response = api.performSTKPush(accessToken, stkPushRequest)
            Log.d("MPesa", "STK Push Response: $response")
            return response

        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("MPesa", "HTTP Error ${e.code()}: $errorBody", e)
            throw Exception("STK Push failed: ${e.code()} - $errorBody")
        } catch (e: IOException) {
            Log.e("MPesa", "Network Error during STK push", e)
            throw Exception("Network error: ${e.message}")
        } catch (e: Exception) {
            Log.e("MPesa", "Unexpected error during STK push", e)
            throw Exception("Unexpected error: ${e.message}")
        }
    }

    // Test connection function
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun testSafaricomConnection() {
        try {
            // First, test basic connection
            val request = okhttp3.Request.Builder()
                .url(BASE_URL)
                .build()

            client.newCall(request).execute().use { response ->
                Log.d("MPesa", "Connection test result: ${response.code}")
            }

            // Then try a simple authentication
            val token = getAccessToken()
            Log.d("MPesa", "Authentication test - received token: $token")
        } catch (e: Exception) {
            Log.e("MPesa", "Connection test failed", e)
        }
    }
}