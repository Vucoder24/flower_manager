package com.example.quanlibanhoa.ui.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlibanhoa.data.entity.InvoiceWithDetails
import com.example.quanlibanhoa.databinding.ItemHistoryInvoiceBinding
import com.example.quanlibanhoa.utils.toVNOnlyK
import java.text.SimpleDateFormat
import java.util.Locale

class InvoiceHistoryAdapter(
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

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val invoiceWithDetails = getItem(position)
        val invoice = invoiceWithDetails.invoice
        val isSelected = selectedInvoices.contains(invoiceWithDetails)

        with(holder.binding) {
            tvCustomerName.text = "KH: ${invoice.tenKhach}"
            tvTotal.text = "💰 Thu: ${invoice.tongTienThu.toInt().toVNOnlyK()} (Lợi nhuận: ${invoice.tongLoiNhuan.toInt().toVNOnlyK()})"
            val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(invoice.date)
            tvDate.text = "🕒 Ngày: $formattedDate"

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
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryInvoiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    fun clearSelection() {
        selectedInvoices.clear()
        setMultiSelectMode(false)
    }
}
