package com.example.quanlibanhoa.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.quanlibanhoa.data.entity.Invoice

@Dao
interface InvoiceDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(invoice: Invoice): Long

    @Transaction
    @Update
    suspend fun update(invoice: Invoice)

    @Transaction
    @Delete
    suspend fun delete(invoice: Invoice)

    @Query("SELECT * FROM invoices ORDER BY createdAt DESC")
    fun getAllInvoices(): LiveData<List<Invoice>>

    @Query("SELECT * FROM invoices WHERE id = :id LIMIT 1")
    suspend fun getInvoiceById(id: Int): Invoice?
}
