package com.example.quanlibanhoa.data.repository

import android.content.Context
import com.example.quanlibanhoa.data.AppDatabase
import com.example.quanlibanhoa.data.dao.FlowerDao
import com.example.quanlibanhoa.data.entity.Flower

class FlowerRepository(context: Context) {
    private val db = AppDatabase.Companion.getDatabase(context)

    suspend fun insertFlower(flower: Flower) = db.flowerDao().insert(flower)
    suspend fun updateFlower(flower: Flower) = db.flowerDao().update(flower)
    suspend fun deleteFlower(flower: Flower) = db.flowerDao().delete(flower)
    suspend fun getAllFlowers() = db.flowerDao().getAllFlowers()
    suspend fun getFlowerById(id: Int) = db.flowerDao().getFlowerById(id)
}
