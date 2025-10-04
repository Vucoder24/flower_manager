package com.example.quanlibanhoa.data.dao

import androidx.room.*
import com.example.quanlibanhoa.data.entity.Invoice
import com.example.quanlibanhoa.data.entity.InvoiceDetail

@Dao
interface InvoiceWithDetailsDao {

    // ------------------------
    // Invoice
    // ------------------------
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: Invoice): Long

    @Transaction
    @Update
    suspend fun updateInvoice(invoice: Invoice)

    @Transaction
    @Delete
    suspend fun deleteInvoice(invoice: Invoice)

    @Query("SELECT * FROM invoices WHERE id = :invoiceId")
    suspend fun getInvoiceById(invoiceId: Int): Invoice?

    // ------------------------
    // InvoiceDetail
    // ------------------------
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoiceDetail(detail: InvoiceDetail): Long

    @Transaction
    @Update
    suspend fun updateInvoiceDetail(detail: InvoiceDetail)

    @Transaction
    @Delete
    suspend fun deleteInvoiceDetail(detail: InvoiceDetail)

    @Query("SELECT * FROM invoice_details WHERE invoiceId = :invoiceId")
    suspend fun getDetailsByInvoiceId(invoiceId: Int): List<InvoiceDetail>

    // ------------------------
    // Transaction: Update Invoice + Details
    // ------------------------
    @Transaction
    suspend fun updateInvoiceWithDetails(invoice: Invoice, newDetails: List<InvoiceDetail>) {
        // update hóa đơn
        updateInvoice(invoice)

        // lấy chi tiết cũ
        val oldDetails = getDetailsByInvoiceId(invoice.id)

        // xoá những detail không còn
        oldDetails.forEach { old ->
            if (newDetails.none { it.id == old.id }) {
                deleteInvoiceDetail(old)
            }
        }

        // thêm hoặc update detail mới
        newDetails.forEach { new ->
            if (new.id != 0) {
                updateInvoiceDetail(new)
            } else {
                insertInvoiceDetail(new.copy(invoiceId = invoice.id))
            }
        }
    }
}
