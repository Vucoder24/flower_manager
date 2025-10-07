package com.example.quanlibanhoa.utils


import com.example.quanlibanhoa.data.entity.InvoiceWithDetails
import com.example.quanlibanhoa.data.entity.TopCustomer
import com.example.quanlibanhoa.data.entity.TopFlower
import java.util.*
import kotlin.collections.HashMap

object ReportUtils {

    // ----------------- 🔹 Bộ lọc thời gian -----------------

    /** 🔸 Hôm nay */
    fun filterToday(invoices: List<InvoiceWithDetails>): List<InvoiceWithDetails> {
        val cal = Calendar.getInstance()
        val start = getStartOfDay(cal.time)
        val end = getEndOfDay(cal.time)
        return filterInvoicesByRange(invoices, start, end)
    }

    /** 🔸 Tuần này */
    fun filterThisWeek(invoices: List<InvoiceWithDetails>): List<InvoiceWithDetails> {
        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.MONDAY
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val start = getStartOfDay(cal.time)

        cal.add(Calendar.DAY_OF_WEEK, 6)
        val end = getEndOfDay(cal.time)
        return filterInvoicesByRange(invoices, start, end)
    }

    /** 🔸 Tháng này */
    fun filterThisMonth(invoices: List<InvoiceWithDetails>): List<InvoiceWithDetails> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val start = getStartOfDay(cal.time)

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        val end = getEndOfDay(cal.time)
        return filterInvoicesByRange(invoices, start, end)
    }

    /** 🔸 Năm nay */
    fun filterThisYear(invoices: List<InvoiceWithDetails>): List<InvoiceWithDetails> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_YEAR, 1)
        val start = getStartOfDay(cal.time)

        cal.set(Calendar.DAY_OF_YEAR, cal.getActualMaximum(Calendar.DAY_OF_YEAR))
        val end = getEndOfDay(cal.time)
        return filterInvoicesByRange(invoices, start, end)
    }

    // ----------------- 🔹 Bộ lọc chung -----------------

    /** Lọc danh sách hóa đơn theo khoảng thời gian */
    fun filterInvoicesByRange(
        allInvoices: List<InvoiceWithDetails>,
        start: Date,
        end: Date
    ): List<InvoiceWithDetails> {
        return allInvoices.filter {
            it.invoice.date.time in start.time..end.time
        }
    }

    // ----------------- 🔹 Tính toán tổng hợp -----------------

    fun calculateSummary(invoices: List<InvoiceWithDetails>): ReportSummary {
        val totalInvoices = invoices.size
        val totalRevenue = invoices.sumOf { it.invoice.tongTienThu }
        val totalProfit = invoices.sumOf { it.invoice.tongLoiNhuan }
        val totalDiscount = invoices.sumOf { it.invoice.giamGia }
        val totalQuantity = invoices.sumOf { it.invoice.tongSoLuong }
        val totalUncompleted = invoices.count { !it.invoice.isCompleted }

        return ReportSummary(
            totalInvoices,
            totalRevenue,
            totalProfit,
            totalDiscount,
            totalQuantity,
            totalUncompleted
        )
    }

    // ----------------- 🔹 Top 10 hoa bán chạy -----------------

    fun getTopFlowers(invoices: List<InvoiceWithDetails>): List<TopFlower> {
        data class FlowerStats(
            var imageUrl: String?,
            var totalQuantity: Int,
            var totalRevenue: Double
        )

        val flowerMap = HashMap<String, FlowerStats>() // tênHoa -> thống kê

        invoices.flatMap { it.details }.forEach { detail ->
            val current = flowerMap[detail.tenHoa]
            if (current == null) {
                // Nếu chưa có thì thêm mới
                flowerMap[detail.tenHoa] = FlowerStats(
                    imageUrl = detail.hinhAnh,
                    totalQuantity = detail.soLuong,
                    totalRevenue = detail.soLuong * detail.giaBan
                )
            } else {
                // Nếu đã có thì cộng dồn
                current.totalQuantity += detail.soLuong
                current.totalRevenue += detail.soLuong * detail.giaBan
                // Nếu trước đó chưa có ảnh mà bây giờ có thì cập nhật
                if (current.imageUrl == null && detail.hinhAnh != null) {
                    current.imageUrl = detail.hinhAnh
                }
            }
        }

        return flowerMap.entries
            .sortedByDescending { it.value.totalQuantity }
            .take(10)
            .map {
                TopFlower(
                    imageUrl = it.value.imageUrl,
                    name = it.key,
                    totalSold = it.value.totalQuantity,
                    totalRevenue = it.value.totalRevenue
                )
            }
    }


    // ----------------- 🔹 Top 10 khách hàng mua nhiều nhất -----------------

    fun getTopCustomers(invoices: List<InvoiceWithDetails>): List<TopCustomer> {
        val customerMap = HashMap<String, Pair<Double, Int>>() // tênKhách -> (tổngChi, tổngSL)

        invoices.forEach { inv ->
            val current = customerMap[inv.invoice.tenKhach]
            val newSpent = (current?.first ?: 0.0) + inv.invoice.tongTienThu
            val newFlowers = (current?.second ?: 0) + inv.invoice.tongSoLuong
            customerMap[inv.invoice.tenKhach] = newSpent to newFlowers
        }

        return customerMap.entries
            .sortedByDescending { it.value.first }
            .take(10)
            .map {
                TopCustomer(
                    name = it.key,
                    totalSpent = it.value.first,
                    totalFlowers = it.value.second
                )
            }
    }

    // ----------------- 🔹 Hàm tiện ích thời gian -----------------

    private fun getStartOfDay(date: Date): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.time
    }

    private fun getEndOfDay(date: Date): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.time
    }
}

// ----------------- 🔹 Data class hỗ trợ -----------------

data class ReportSummary(
    val totalInvoices: Int,
    val totalRevenue: Double,
    val totalProfit: Double,
    val totalDiscount: Int,
    val totalQuantity: Int,
    val totalUncompleted: Int
)
