package com.example.lunark.mpesa

import android.content.Context
import android.os.Build
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
import retrofit2.HttpException

class MPesaService(private val context: Context) {
    private val BASE_URL = "https://sandbox.safaricom.co.ke/" // Use production URL in production
    private val CONSUMER_KEY = "3z8JLAbvVO1AJQdOFJ1SyiJJhcGbKR13LPCPPlXLD8JiQi5l"
    private val CONSUMER_SECRET = "uA05hIMUKgZsRgoInhUzwsHG91LX4juFUwwy70by2PMsfW17qYmd7I2pIL9qFhOn"
    private val BUSINESS_SHORT_CODE = "174379" // Test shortcode for sandbox
    private val PASSKEY = "uA05hIMUKgZsRgoInhUzwsHG91LX4juFUwwy70by2PMsfW17qYmd7I2pIL9qFhOn" // Get from Daraja portal
    private val CALLBACK_URL = "https://your-callback-url.com/callback"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .build()
        )
        .build()

    private val api = retrofit.create(MpesaAPI::class.java)

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getAccessToken(): String {
        val credentials = "$CONSUMER_KEY:$CONSUMER_SECRET"
        val encodedCredentials = "Basic " + Base64.getEncoder().encodeToString(credentials.toByteArray())

        return try {
            val response = api.getAccessToken(encodedCredentials)
            "Bearer " + response.access_token
        } catch (e: HttpException) {
            // Handle HTTP-specific errors
            throw e
        } catch (e: IOException) {
            // Handle network errors
            throw e
        } catch (e: Exception) {
            // Handle other exceptions
            throw e
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun initiateSTKPush(
        phoneNumber: String,
        amount: String,
        accountReference: String,
        transactionDesc: String
    ): STKPushResponse {
        val timestamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(Date())
        val password = Base64.getEncoder().encodeToString(
            "$BUSINESS_SHORT_CODE$PASSKEY$timestamp".toByteArray()
        )

        val formattedPhone = when {
            phoneNumber.startsWith("0") -> "254" + phoneNumber.substring(1)
            phoneNumber.startsWith("+254") -> phoneNumber.substring(1)
            phoneNumber.startsWith("254") -> phoneNumber
            else -> throw IllegalArgumentException("Invalid phone number format")
        }

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

        val accessToken = getAccessToken()

        return try {
            api.performSTKPush(accessToken, stkPushRequest)
        } catch (e: HttpException) {
            // Handle HTTP-specific errors
            throw e
        } catch (e: IOException) {
            // Handle network errors
            throw e
        } catch (e: Exception) {
            // Handle other exceptions
            throw e
        }
    }
}
