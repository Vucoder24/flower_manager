package com.example.quanlibanhoa.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "invoice_details")
data class InvoiceDetail(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val invoiceId: Int,
    val hinhAnh: String?,
    val tenHoa: String,
    val soLuong: Int,
    val giaNhap: Double,
    val giaBan: Double
)

