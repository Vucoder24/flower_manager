package com.example.quanlibanhoa.data.dao


import androidx.room.*
import com.example.quanlibanhoa.data.entity.InvoiceDetail

@Dao
interface InvoiceDetailDao {
    @Insert
    suspend fun insertInvoiceDetail(detail: InvoiceDetail): Long

    @Insert
    suspend fun insertInvoiceDetails(details: List<InvoiceDetail>)

    @Update
    suspend fun updateInvoiceDetail(detail: InvoiceDetail)

    @Delete
    suspend fun deleteInvoiceDetail(detail: InvoiceDetail)

    @Query("SELECT * FROM invoice_details WHERE invoiceId = :invoiceId")
    suspend fun getDetailsByInvoiceId(invoiceId: Int): List<InvoiceDetail>
}
