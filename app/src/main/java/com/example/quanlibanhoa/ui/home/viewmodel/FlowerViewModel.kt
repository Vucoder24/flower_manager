package com.example.quanlibanhoa.ui.home.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.example.quanlibanhoa.data.entity.Flower
import com.example.quanlibanhoa.data.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class FlowerViewModel(
    private val repository: AppRepository
): ViewModel() {
    private val _flowerState = MutableLiveData<State>()
    var flowerState: LiveData<State> = _flowerState

    val flowerStateList: LiveData<List<Flower>> = repository.getAllFlowers().distinctUntilChanged()

    fun addFlower(flower: Flower) {
        viewModelScope.launch(Dispatchers.IO){
            try {
                repository.insertFlower(flower)
                _flowerState.postValue(State.ADD_SUCCESS)
            }catch (e: Exception){
                _flowerState.postValue(State.ADD_ERROR)
            }
        }
    }

    fun editFlower(flower: Flower) {
        viewModelScope.launch(Dispatchers.IO){
            try {
                repository.updateFlower(flower)
                _flowerState.postValue(State.EDIT_SUCCESS)
            }catch (e: Exception){
                _flowerState.postValue(State.EDIT_ERROR)
            }
        }
    }

    fun deleteFlower(flower: Flower) {
        viewModelScope.launch(Dispatchers.IO){
            try {
                repository.deleteFlower(flower)
                // Xóa ảnh từ bộ nhớ nội bộ
                flower.hinhAnh?.let { deleteImageFile(it) }
                _flowerState.postValue(State.DELETE_SUCCESS)
            }catch (e: Exception){
                _flowerState.postValue(State.DELETE_ERROR)
            }
        }
    }
    private fun deleteImageFile(imagePath: String) {
        val file = File(imagePath)
        if (file.exists()) {
            file.delete() // Xóa tệp hình ảnh
        }
    }
}

enum class State{
    ADD_SUCCESS,
    ADD_ERROR,
    EDIT_SUCCESS,
    EDIT_ERROR,
    DELETE_SUCCESS,
    DELETE_ERROR
}

class FlowerViewModelFactory(
    private val context: Context,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FlowerViewModel::class.java)) {
            // Create dependencies inside the factory
            val localRepo = AppRepository(context)
            @Suppress("UNCHECKED_CAST")
            return FlowerViewModel(
                localRepo
            ) as T
        }
        throw IllegalArgumentException("Unknown TransactionViewModel class")
    }
}