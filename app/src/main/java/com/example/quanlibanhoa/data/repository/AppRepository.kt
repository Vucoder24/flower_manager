package com.example.quanlibanhoa.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.quanlibanhoa.data.AppDatabase
import com.example.quanlibanhoa.data.entity.Flower
import com.example.quanlibanhoa.data.entity.Invoice

class AppRepository(context: Context) {
    private val db = AppDatabase.Companion.getDatabase(context)

    suspend fun insertFlower(flower: Flower) = db.flowerDao().insert(flower)
    suspend fun updateFlower(flower: Flower) = db.flowerDao().update(flower)
    suspend fun deleteFlower(flower: Flower) = db.flowerDao().delete(flower)
    fun getAllFlowers(): LiveData<List<Flower>>{
        return db.flowerDao().getAllFlowers()
    }
    suspend fun getFlowerById(id: Int) = db.flowerDao().getFlowerById(id)

    suspend fun insertInvoice(invoice: Invoice) = db.invoiceDao().insert(invoice)
    suspend fun updateInvoice(invoice: Invoice) = db.invoiceDao().update(invoice)
    suspend fun deleteInvoice(invoice: Invoice) = db.invoiceDao().delete(invoice)
    fun getAllInvoices(): LiveData<List<Invoice>>{
        return db.invoiceDao().getAllInvoices()
    }
    suspend fun getInvoiceById(id: Int) = db.invoiceDao().getInvoiceById(id)
}
