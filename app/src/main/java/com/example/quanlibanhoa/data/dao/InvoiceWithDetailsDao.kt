package com.example.quanlibanhoa.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.quanlibanhoa.data.entity.Invoice
import com.example.quanlibanhoa.data.entity.InvoiceDetail
import com.example.quanlibanhoa.data.entity.InvoiceWithDetails

@Dao
interface InvoiceWithDetailsDao {

    // Invoice
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: Invoice): Long

    @Transaction
    @Update
    suspend fun updateInvoice(invoice: Invoice)

    // InvoiceDetail
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoiceDetail(detail: InvoiceDetail): Long

    @Transaction
    @Update
    suspend fun updateInvoiceDetail(detail: InvoiceDetail)


    @Query("SELECT * FROM invoice_details WHERE invoiceId = :invoiceId")
    suspend fun getDetailsByInvoiceId(invoiceId: Int): List<InvoiceDetail>

    // ✅ Lấy hóa đơn kèm chi tiết
    @Transaction
    @Query("SELECT * FROM invoices ORDER BY createdAt DESC")
    fun getAllInvoicesWithDetails(): LiveData<List<InvoiceWithDetails>>

    @Transaction
    @Query("SELECT * FROM invoices WHERE id = :id LIMIT 1")
    suspend fun getInvoiceWithDetailsById(id: Int): InvoiceWithDetails?

}
