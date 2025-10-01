package com.example.quanlibanhoa.ui.home.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.example.quanlibanhoa.data.entity.Flower
import com.example.quanlibanhoa.data.entity.Invoice
import com.example.quanlibanhoa.data.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InvoiceViewModel(
    private val repository: AppRepository
): ViewModel() {
    private val _invoiceState = MutableLiveData<State>()
    var invoiceState: LiveData<State> = _invoiceState

    val invoiceStateList: LiveData<List<Invoice>> = repository.getAllInvoices().distinctUntilChanged()

    fun addFlower(flower: Flower) {
        viewModelScope.launch(Dispatchers.IO){
            try {
                repository.insertFlower(flower)
                _invoiceState.postValue(State.ADD_SUCCESS)
            }catch (e: Exception){
                _invoiceState.postValue(State.ADD_ERROR)
            }
        }
    }

    fun editFlower(flower: Flower) {
        viewModelScope.launch(Dispatchers.IO){
            try {
                repository.updateFlower(flower)
                _invoiceState.postValue(State.EDIT_SUCCESS)
            }catch (e: Exception){
                _invoiceState.postValue(State.EDIT_ERROR)
            }
        }
    }

    fun deleteFlower(flower: Flower) {
        viewModelScope.launch(Dispatchers.IO){
            try {
                repository.deleteFlower(flower)
                _invoiceState.postValue(State.DELETE_SUCCESS)
            }catch (e: Exception){
                _invoiceState.postValue(State.DELETE_ERROR)
            }
        }
    }
}

class InvoiceViewModelFactory(
    private val context: Context,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InvoiceViewModel::class.java)) {
            // Create dependencies inside the factory
            val localRepo = AppRepository(context)
            @Suppress("UNCHECKED_CAST")
            return InvoiceViewModel(
                localRepo
            ) as T
        }
        throw IllegalArgumentException("Unknown TransactionViewModel class")
    }
}