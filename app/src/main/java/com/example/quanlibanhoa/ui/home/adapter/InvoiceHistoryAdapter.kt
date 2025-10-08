package com.example.quanlibanhoa.ui.home.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlibanhoa.R
import com.example.quanlibanhoa.data.entity.InvoiceWithDetails
import com.example.quanlibanhoa.databinding.ItemHistoryInvoiceBinding
import com.example.quanlibanhoa.utils.toFormattedString
import com.example.quanlibanhoa.utils.toVNOnlyK
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

class InvoiceHistoryAdapter(
    private val onEdit: (InvoiceWithDetails) -> Unit,
    private val onClick: (InvoiceWithDetails) -> Unit, // Click thường để xem chi tiết
    @Suppress("unused") private val onDeleteSelected: (List<InvoiceWithDetails>) -> Unit,
    private val onMultiSelectModeChanged: (Boolean) -> Unit,
    private val onSelectionCountChanged: (Int) -> Unit
) : ListAdapter<InvoiceWithDetails, InvoiceHistoryAdapter.ViewHolder>(DiffCallback) {
    var isMultiSelectMode = false
        private set
    val selectedInvoices = mutableSetOf<InvoiceWithDetails>()

    object DiffCallback : DiffUtil.ItemCallback<InvoiceWithDetails>() {
        override fun areItemsTheSame(oldItem: InvoiceWithDetails, newItem: InvoiceWithDetails) =
            oldItem.invoice.id == newItem.invoice.id

        override fun areContentsTheSame(oldItem: InvoiceWithDetails, newItem: InvoiceWithDetails) =
            oldItem == newItem
    }

    inner class ViewHolder(val binding: ItemHistoryInvoiceBinding) :
        RecyclerView.ViewHolder(binding.root)

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val invoiceWithDetails = getItem(position)
        val invoice = invoiceWithDetails.invoice
        val isSelected = selectedInvoices.contains(invoiceWithDetails)

        with(holder.binding) {
            tvCustomerName.text = "KH: ${invoice.tenKhach}"
            tvTotal.text = "💰 Thu: ${invoice.tongTienThu.toInt().toVNOnlyK()} " +
                    "(Lợi nhuận: ${invoice.tongLoiNhuan.toInt().toVNOnlyK()})"
            val formattedDate = invoice.date.toFormattedString()
            tvDate.text = "🕒 $formattedDate"

            val currentDate = LocalDate.now()
            val invoiceDate = convertToLocalDate(invoice.date)

            when (invoice.isCompleted) {
                true -> {
                    tvStatus.text = holder.itemView.context
                        .getString(R.string.action_complete_ship)
                    tvStatus.setTextColor(
                        ContextCompat.getColor(
                            holder.itemView.context,
                            R.color.green_600
                        )
                    )
                }
                false -> {
                    when {
                        invoiceDate.isAfter(currentDate) -> { // Chưa đến ngày
                            tvStatus.text = holder.itemView.context
                                .getString(R.string.status_not_due_yet)
                            tvStatus.setTextColor(
                                ContextCompat.getColor(
                                    holder.itemView.context,
                                    R.color.yellow_600
                                )
                            )
                        }
                        invoiceDate.isEqual(currentDate) -> { // Đến ngày
                            tvStatus.text = holder.itemView.context
                                .getString(R.string.status_due_today)
                            tvStatus.setTextColor(
                                ContextCompat.getColor(
                                    holder.itemView.context,
                                    R.color.orange
                                )
                            )
                        }
                        invoiceDate.isBefore(currentDate) -> { // Quá hạn
                            tvStatus.text = holder.itemView.context
                                .getString(R.string.status_over_due)
                            tvStatus.setTextColor(
                                ContextCompat.getColor(
                                    holder.itemView.context,
                                    R.color.red_700
                                )
                            )
                        }
                    }
                }
            }

            // 🔥 Hiển thị CheckBox nếu multi-select
            cbSelect.visibility = if (isMultiSelectMode) View.VISIBLE else View.GONE
            btnEdit.visibility = if (isMultiSelectMode) View.GONE else View.VISIBLE
            cbSelect.isChecked = isSelected

            // Long Click: Bật chế độ chọn và tự chọn item
            root.setOnLongClickListener {
                if (!isMultiSelectMode) {
                    setMultiSelectMode(true)
                    toggleSelection(invoiceWithDetails)
                }
                true
            }

            // Click thường
            root.setOnClickListener {
                if (isMultiSelectMode) {
                    toggleSelection(invoiceWithDetails)
                } else {
                    onClick(invoiceWithDetails)
                }
            }

            // Click CheckBox
            cbSelect.setOnClickListener {
                toggleSelection(invoiceWithDetails)
            }
            btnEdit.setOnClickListener {
                onEdit(invoiceWithDetails)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemHistoryInvoiceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setMultiSelectMode(enabled: Boolean) {
        if (isMultiSelectMode != enabled) {
            isMultiSelectMode = enabled
            if (!enabled) selectedInvoices.clear()
            onMultiSelectModeChanged(enabled)
            notifyDataSetChanged() // Cập nhật hiển thị CheckBox
        }
    }

    private fun toggleSelection(item: InvoiceWithDetails) {
        if (selectedInvoices.contains(item)) selectedInvoices.remove(item)
        else selectedInvoices.add(item)
        onSelectionCountChanged(selectedInvoices.size)
        val index = currentList.indexOf(item)
        if (index != -1) notifyItemChanged(index)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertToLocalDate(date: Date): LocalDate {
        return date.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun selectAllInvoices() {
        if (isMultiSelectMode) {
            selectedInvoices.clear()
            selectedInvoices.addAll(currentList)
            onSelectionCountChanged(selectedInvoices.size)
            notifyDataSetChanged()
        }
    }

    fun clearSelection() {
        selectedInvoices.clear()
        setMultiSelectMode(false)
    }
}
