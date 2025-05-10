package com.example.lunark.viewmodel

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lunark.mpesa.MPesaService
import com.example.lunark.mpesa.STKPushResponse

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PaymentViewModel : ViewModel() {


    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val paymentState = _paymentState.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    fun initiatePayment(
        context: Context,
        phoneNumber: String,
        amount: String,
        productId: String
    ) {
        viewModelScope.launch {
            _paymentState.value = PaymentState.Loading
            try {
                val mPesaService = MPesaService(context)
                val response = mPesaService.initiateSTKPush(
                    phoneNumber = phoneNumber,
                    amount = amount,
                    accountReference = productId,
                    transactionDesc = "Payment for product $productId"
                )

                _paymentState.value = PaymentState.Success(response)
            } catch (e: Exception) {
                _paymentState.value = PaymentState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    sealed class PaymentState {
        object Idle : PaymentState()
        object Loading : PaymentState()
        data class Success(val response: STKPushResponse) : PaymentState()
        data class Error(val message: String) : PaymentState()
    }
}