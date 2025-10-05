package com.example.quanlibanhoa.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class InvoiceWithDetails(
    @Embedded val invoice: Invoice,

    @Relation(
        parentColumn = "id",
        entityColumn = "invoiceId"
    )
    val details: List<InvoiceDetail>
)
