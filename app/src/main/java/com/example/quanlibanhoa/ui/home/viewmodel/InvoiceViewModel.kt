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
import com.example.quanlibanhoa.data.entity.InvoiceDetail
import com.example.quanlibanhoa.data.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InvoiceViewModel(
    private val repository: AppRepository
): ViewModel() {
    private val _addInvoiceState = MutableLiveData<StateInvoice>().apply {
        value = StateInvoice.IDLE // init về IDLE
    }
    val addInvoiceState: LiveData<StateInvoice> = _addInvoiceState.distinctUntilChanged()

    private val _editInvoiceState = MutableLiveData<StateInvoice>().apply {
        value = StateInvoice.IDLE // init về IDLE
    }
    val editInvoiceState: LiveData<StateInvoice> = _editInvoiceState.distinctUntilChanged()

    private val _deleteInvoiceState = MutableLiveData<StateInvoice>().apply {
        value = StateInvoice.IDLE // init về IDLE
    }
    val deleteInvoiceState: LiveData<StateInvoice> = _deleteInvoiceState.distinctUntilChanged()

    val invoiceStateList: LiveData<List<Invoice>> = repository.getAllInvoices().distinctUntilChanged()

    fun addInvoiceWithDetail(invoice: Invoice, details: List<InvoiceDetail>) {
        viewModelScope.launch(Dispatchers.IO){
            try {
                repository.insertInvoiceWithDetails(invoice, details)
                _addInvoiceState.postValue(StateInvoice.ADD_INVOICE_SUCCESS)
            }catch (e: Exception){
                _addInvoiceState.postValue(StateInvoice.ADD_INVOICE_ERROR)
            }
        }
    }

    fun editFlower(flower: Flower) {
        viewModelScope.launch(Dispatchers.IO){
            try {
                repository.updateFlower(flower)
                _editInvoiceState.postValue(StateInvoice.EDIT_INVOICE_SUCCESS)
            }catch (e: Exception){
                _editInvoiceState.postValue(StateInvoice.EDIT_INVOICE_ERROR)
            }
        }
    }

    fun deleteFlower(flower: Flower) {
        viewModelScope.launch(Dispatchers.IO){
            try {
                repository.deleteFlower(flower)
                _deleteInvoiceState.postValue(StateInvoice.DELETE_INVOICE_SUCCESS)
            }catch (e: Exception){
                _deleteInvoiceState.postValue(StateInvoice.DELETE_INVOICE_ERROR)
            }
        }
    }

    fun resetAddState() {
        _addInvoiceState.value = StateInvoice.IDLE
    }

    fun resetEditState() {
        _editInvoiceState.value = StateInvoice.IDLE
    }

    fun resetDeleteState() {
        _deleteInvoiceState.value = StateInvoice.IDLE
    }

}

enum class StateInvoice {
    IDLE,
    ADD_INVOICE_SUCCESS,
    ADD_INVOICE_ERROR,
    EDIT_INVOICE_SUCCESS,
    EDIT_INVOICE_ERROR,
    DELETE_INVOICE_SUCCESS,
    DELETE_INVOICE_ERROR
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