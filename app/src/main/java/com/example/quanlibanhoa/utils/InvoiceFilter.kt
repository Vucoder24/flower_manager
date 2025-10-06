package com.example.quanlibanhoa.utils

import com.example.quanlibanhoa.data.entity.InvoiceWithDetails
import java.util.*

object InvoiceFilter {

    private val vietnamTimeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh")

    private fun getStartAndEndDates(period: String): Pair<Date, Date> {
        val calendar = Calendar.getInstance(vietnamTimeZone)

        return when (period) {
            "today" -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val start = calendar.time

                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                val end = calendar.time
                Pair(start, end)
            }
            "thisWeek" -> {
                calendar.firstDayOfWeek = Calendar.MONDAY
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val start = calendar.time

                calendar.add(Calendar.WEEK_OF_YEAR, 1)
                calendar.add(Calendar.MILLISECOND, -1)
                val end = calendar.time
                Pair(start, end)
            }
            "thisMonth" -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val start = calendar.time

                calendar.add(Calendar.MONTH, 1)
                calendar.add(Calendar.MILLISECOND, -1)
                val end = calendar.time
                Pair(start, end)
            }
            "thisYear" -> {
                calendar.set(Calendar.DAY_OF_YEAR, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val start = calendar.time

                calendar.add(Calendar.YEAR, 1)
                calendar.add(Calendar.MILLISECOND, -1)
                val end = calendar.time
                Pair(start, end)
            }
            else -> throw IllegalArgumentException("Invalid period: $period")
        }
    }

    fun filterInvoices(
        invoices: List<InvoiceWithDetails>,
        period: String
    ): List<InvoiceWithDetails> {

        val now = Calendar.getInstance(vietnamTimeZone).time

        // 🟢 Lọc hóa đơn đến hạn
        if (period == "due" || period == "denHan") {
            val uncompleted = invoices.filter {
                !it.invoice.isCompleted
            }

            // Đến hạn hoặc muộn so với giờ VN
            val overdueOrDue = uncompleted.filter {
                val due = convertToVietnamTime(it.invoice.date)
                due.before(now) || due == now
            }

            // Chưa đến hạn
            val notYetDue = uncompleted.filter {
                val due = convertToVietnamTime(it.invoice.date)
                due.after(now)
            }

            val sortedOverdue = overdueOrDue.sortedBy { it.invoice.date }
            val sortedUpcoming = notYetDue.sortedBy { it.invoice.date }

            return sortedOverdue + sortedUpcoming
        }

        // 🟡 Lọc theo khoảng thời gian
        val (start, end) = getStartAndEndDates(period)

        return invoices
            .filter { invoice ->
                val date = convertToVietnamTime(invoice.invoice.date)
                (date.after(start) && date.before(end)) || date == start || date == end
            }
            .sortedByDescending { it.invoice.createdAt }
    }

    // 🔧 Hàm chuyển thời gian về múi giờ Việt Nam
    private fun convertToVietnamTime(date: Date): Date {
        val calendar = Calendar.getInstance(vietnamTimeZone)
        calendar.time = date
        return calendar.time
    }
}
