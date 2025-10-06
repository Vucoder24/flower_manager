package com.example.quanlibanhoa.ui.home.fragment.invoice

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlibanhoa.data.entity.InvoiceWithDetails
import com.example.quanlibanhoa.databinding.FragmentYearlyInvoiceBinding
import com.example.quanlibanhoa.ui.edit_invoice.EditInvoiceActivity
import com.example.quanlibanhoa.ui.home.HomeActivity
import com.example.quanlibanhoa.ui.home.adapter.InvoiceHistoryAdapter
import com.example.quanlibanhoa.ui.home.viewmodel.InvoiceViewModel
import com.example.quanlibanhoa.ui.home.viewmodel.InvoiceViewModelFactory
import com.example.quanlibanhoa.ui.home.viewmodel.StateInvoice
import com.example.quanlibanhoa.utils.InvoiceFilter
import kotlin.getValue


class YearlyInvoiceFragment : Fragment() {
    private var _binding: FragmentYearlyInvoiceBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: InvoiceHistoryAdapter
    private var currentInvoices = listOf<InvoiceWithDetails>()

    val invoiceViewModel: InvoiceViewModel by activityViewModels {
        InvoiceViewModelFactory(
            requireContext()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentYearlyInvoiceBinding.inflate(
            layoutInflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 🔥 KHỞI TẠO ADAPTER VỚI CÁC CALLBACK XÓA
        adapter = InvoiceHistoryAdapter(
            onEdit = { invoice ->
                val intent =
                    Intent(requireContext(), EditInvoiceActivity::class.java)
                intent.putExtra("invoice_data", invoice)
                requireContext().startActivity(intent)
                (requireContext() as HomeActivity).slideNewActivity()},
            onClick = { invoice ->
                // Xử lý sự kiện khi nhấp vào hóa đơn (Xem chi tiết)
                if (!adapter.isMultiSelectMode) {
                    showInvoiceDetailPopup(invoice)
                }
            },
            onDeleteSelected = { list -> showDeleteConfirmDialog(list) },
            onMultiSelectModeChanged = { isEnabled ->
                if (isEnabled) {
                    showDeleteToolbar()
                } else {
                    hideDeleteToolbar()
                }
            },
            onSelectionCountChanged = { count ->
                updateDeleteToolbarText(count)
            }
        )
        binding.rycYearlyInvoice.layoutManager = LinearLayoutManager(requireContext())
        binding.rycYearlyInvoice.adapter = adapter
        addEvent()
        observerData()
        setupSearchView()
    }

    private fun addEvent() {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val invoiceWithDetails = adapter.currentList[position]
                val invoice = invoiceWithDetails.invoice

                // Gọi ViewModel để toggle trạng thái
                invoiceViewModel.toggleIsCompleted(invoice.id, invoice.isCompleted)

                // Cập nhật UI tạm thời trong adapter
                adapter.toggleCompleted(invoiceWithDetails)
            }
        })

        itemTouchHelper.attachToRecyclerView(binding.rycYearlyInvoice)
    }

    private fun observerData() {
        invoiceViewModel.invoiceWithDetailsStateList.observe(viewLifecycleOwner){
            // Lọc hóa đơn theo tiêu chí tuần này
            val filteredInvoices =
                InvoiceFilter.filterInvoices(it, "thisYear")
            currentInvoices = filteredInvoices
            adapter.submitList(filteredInvoices)
        }
        // 🔥 THEO DÕI TRẠNG THÁI XÓA (Cần có StateInvoice tương ứng trong ViewModel)
        invoiceViewModel.deleteInvoiceState4.observe(viewLifecycleOwner) { result ->
            if (result == StateInvoice.IDLE) return@observe
            binding.btnConfirmDelete.isEnabled = true
            binding.btnConfirmDelete.alpha = 1f
            when (result) {
                StateInvoice.DELETE_INVOICE_SUCCESS -> {
                    Toast.makeText(
                        requireContext(),
                        "Xóa hóa đơn thành công.",
                        Toast.LENGTH_SHORT
                    ).show()
                    invoiceViewModel.resetDeleteState(4)
                }
                StateInvoice.DELETE_INVOICE_ERROR -> {
                    Toast.makeText(
                        requireContext(),
                        "Lỗi khi xóa hóa đơn, vui lòng thử lại!",
                        Toast.LENGTH_SHORT).show()
                    invoiceViewModel.resetDeleteState(4)
                }
                else -> {}
            }
        }
    }

    private fun showInvoiceDetailPopup(invoice: InvoiceWithDetails) {
        // Khởi tạo BottomSheetFragment, truyền dữ liệu hóa đơn đầy đủ
        val detailSheet = InvoiceDetailBottomSheetFragment(invoice)

        // Hiển thị BottomSheetDialogFragment
        // Lưu ý: "InvoiceDetailTag" là một chuỗi định danh bất kỳ.
        detailSheet.show(parentFragmentManager, "InvoiceDetailTag")
    }

    // HÀM XỬ LÝ TOOLBAR VÀ XÓA
    private fun showDeleteConfirmDialog(invoicesToDelete: List<InvoiceWithDetails>) {
        if (invoicesToDelete.isEmpty()) return

        AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa ${invoicesToDelete.size} hóa đơn đã chọn?")
            .setPositiveButton("Xóa") { _, _ ->
                val invoiceIds = invoicesToDelete.map { it.invoice.id }
                //GỌI HÀM XÓA TRONG VIEWMODEL
                binding.btnConfirmDelete.isEnabled = false
                binding.btnConfirmDelete.alpha = 0.8f
                invoiceViewModel.deleteInvoicesByIds(invoiceIds, 4)
                adapter.clearSelection()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun showDeleteToolbar() {
        binding.deleteToolbar.visibility = View.VISIBLE

        binding.btnClearSelection.setOnClickListener {
            adapter.clearSelection()
        }

        binding.btnConfirmDelete.setOnClickListener {
            // Chuyển Set sang List để truyền đi
            showDeleteConfirmDialog(adapter.selectedInvoices.toList())
        }
    }

    private fun hideDeleteToolbar() {
        binding.deleteToolbar.visibility = View.GONE
        updateDeleteToolbarText(0)
    }

    @SuppressLint("SetTextI18n")
    private fun updateDeleteToolbarText(count: Int) {
        binding.btnConfirmDelete.text = "Xóa ($count)"
        binding.btnConfirmDelete.isEnabled = count > 0

        if (count == 0 && adapter.isMultiSelectMode) {
            adapter.setMultiSelectMode(false)
        }
    }

    private fun setupSearchView() {
        // 1. Theo dõi thay đổi văn bản và xử lý nút Clear
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Hiển thị/Ẩn nút Clear
                binding.ivClear.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE

                // Lọc danh sách ngay khi văn bản thay đổi
                filterList(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // 2. Xử lý khi nhấn nút Clear (X)
        binding.ivClear.setOnClickListener {
            binding.etSearch.setText("") // Xóa text
            binding.etSearch.requestFocus() // Giữ focus và bàn phím để người dùng tiếp tục gõ
            // filterList sẽ được gọi trong TextWatcher
        }

        // 3. Xử lý khi người dùng nhấn "Search" trên bàn phím
        binding.etSearch.setOnEditorActionListener { v: TextView?, actionId: Int, event ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                // Ẩn bàn phím và bỏ focus
                hideKeyboard(v)
                binding.etSearch.clearFocus()
                // filterList đã được gọi trong onTextChanged
                return@setOnEditorActionListener true
            }
            false
        }

        // 4. Khôi phục danh sách đầy đủ khi EditText mất focus
        binding.etSearch.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && binding.etSearch.text.isNullOrBlank()) {
                // Nếu EditText mất focus VÀ không có nội dung tìm kiếm, hiển thị lại list đầy đủ
                adapter.submitList(currentInvoices)
            }
        }
    }

    // Hàm tiện ích để ẩn bàn phím
    private fun hideKeyboard(view: TextView?) {
        val imm =
            context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
    }


    private fun filterList(query: String?) {
        // Sử dụng currentInvoices đã được lọc theo "thisWeek"
        val listToSearch = currentInvoices

        val result = if (query.isNullOrBlank()) {
            // Khi query rỗng (ngay sau khi mở hoặc clear), bạn có thể không cần hiển thị lại
            // list đầy đủ, vì việc này được xử lý trong onTextChanged.
            // Tuy nhiên, nếu bạn muốn tìm kiếm query rỗng thì cứ để filtered.
            listToSearch
        } else {
            listToSearch.filter {
                it.invoice.tenKhach.contains(query, ignoreCase = true) ||
                        it.invoice.sdt?.contains(query, ignoreCase = true) == true
            }
        }

        adapter.submitList(result)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}