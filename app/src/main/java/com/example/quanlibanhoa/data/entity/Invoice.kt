package com.example.quanlibanhoa.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "invoices")
data class Invoice(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tenKhach: String,
    val sdt: String?,
    val diaChi: String?,
    val danhSachHoa: String,   // ví dụ: "Hồng(2), Huệ(4)"
    val tongSoLuong: Int,      // tổng bó hoa
    val tongTienThu: Double,   // tổng tiền bán
    val tongLoiNhuan: Double,  // tổng lợi nhuận
    val createdAt: Long = Date().time
)

