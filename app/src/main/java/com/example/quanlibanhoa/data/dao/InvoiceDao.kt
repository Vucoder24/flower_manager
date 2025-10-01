package com.example.quanlibanhoa.data.dao

import androidx.room.*
import com.example.quanlibanhoa.data.entity.Invoice

@Dao
interface InvoiceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(invoice: Invoice): Long

    @Update
    suspend fun update(invoice: Invoice)

    @Delete
    suspend fun delete(invoice: Invoice)

    @Query("SELECT * FROM invoices ORDER BY createdAt DESC")
    suspend fun getAllInvoices(): List<Invoice>

    @Query("SELECT * FROM invoices WHERE id = :id LIMIT 1")
    suspend fun getInvoiceById(id: Int): Invoice?
}
