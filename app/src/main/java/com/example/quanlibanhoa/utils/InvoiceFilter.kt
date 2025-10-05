package com.example.quanlibanhoa.utils

import com.example.quanlibanhoa.data.entity.InvoiceWithDetails
import java.util.Calendar
import java.util.Date

object InvoiceFilter {

    private fun getStartAndEndDates(period: String): Pair<Date, Date> {
        val calendar = Calendar.getInstance()

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
        val (start, end) = getStartAndEndDates(period)

        return invoices
            .filter { invoice ->
                val date = invoice.invoice.date
                date.after(start) && date.before(end) || date == start || date == end
            }
            .sortedByDescending { it.invoice.createdAt }
    }
}