package com.example.quanlibanhoa.data.repository

import android.content.Context
import com.example.quanlibanhoa.data.AppDatabase
import com.example.quanlibanhoa.data.dao.InvoiceDao
import com.example.quanlibanhoa.data.entity.Invoice

class InvoiceRepository(context: Context) {
    private val db = AppDatabase.Companion.getDatabase(context)

    suspend fun insertInvoice(invoice: Invoice) = db.invoiceDao().insert(invoice)
    suspend fun updateInvoice(invoice: Invoice) = db.invoiceDao().update(invoice)
    suspend fun deleteInvoice(invoice: Invoice) = db.invoiceDao().delete(invoice)
    suspend fun getAllInvoices() = db.invoiceDao().getAllInvoices()
    suspend fun getInvoiceById(id: Int) = db.invoiceDao().getInvoiceById(id)
}
