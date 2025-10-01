package com.example.quanlibanhoa.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flowers")
data class Flower(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tenHoa: String,
    val hinhAnh: String?,
    val giaNhap: Double,
    val giaBan: Double
)

