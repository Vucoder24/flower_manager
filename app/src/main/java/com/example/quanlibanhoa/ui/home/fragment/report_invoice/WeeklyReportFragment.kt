package com.example.quanlibanhoa.ui.home.fragment.report_invoice

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quanlibanhoa.databinding.FragmentWeeklyReportBinding
import com.example.quanlibanhoa.ui.home.adapter.TopCustomerAdapter
import com.example.quanlibanhoa.ui.home.adapter.TopFlowerAdapter
import com.example.quanlibanhoa.ui.home.viewmodel.InvoiceViewModel
import com.example.quanlibanhoa.ui.home.viewmodel.InvoiceViewModelFactory
import com.example.quanlibanhoa.utils.ReportUtils
import com.example.quanlibanhoa.utils.toVNOnlyK


class WeeklyReportFragment : Fragment() {
    private var _binding: FragmentWeeklyReportBinding? = null
    private val binding get() = _binding!!

    private lateinit var topFlowerAdapter: TopFlowerAdapter
    private lateinit var topCustomerAdapter: TopCustomerAdapter

    private val invoiceViewModel: InvoiceViewModel by activityViewModels {
        InvoiceViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeeklyReportBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        observeInvoices()
    }

    private fun setupRecyclerViews() {
        // setup list top flower
        topFlowerAdapter = TopFlowerAdapter(emptyList())
        binding.rvTopProducts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = topFlowerAdapter
        }

        // setup list top customer
        topCustomerAdapter = TopCustomerAdapter(emptyList())
        binding.rvTopCustomers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = topCustomerAdapter
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeInvoices() {
        invoiceViewModel.invoiceWithDetailsStateList.observe(viewLifecycleOwner) { allInvoices ->
            val thisWeekInvoices = ReportUtils.filterThisWeek(allInvoices)

            // --- cập nhật thống kê ---
            val summary = ReportUtils.calculateSummary(thisWeekInvoices)
            binding.apply {
                tvTotalInvoice.text = summary.totalInvoices.toString() + " đơn"
                tvTotalRevenue.text = summary.totalRevenue.toInt().toVNOnlyK()
                tvTotalProfit.text = summary.totalProfit.toInt().toVNOnlyK()
                tvTotalDiscount.text = summary.totalDiscount.toVNOnlyK()
                tvTotalFlower.text = summary.totalQuantity.toString() + " bó"
                tvUncompleted.text = summary.totalUncompleted.toString() + " đơn"
            }

            // --- cập nhật top hoa ---
            val topFlowers = ReportUtils.getTopFlowers(thisWeekInvoices)
            topFlowerAdapter.updateData(topFlowers)

            // --- cập nhật top khách ---
            val topCustomers = ReportUtils.getTopCustomers(thisWeekInvoices)
            topCustomerAdapter.updateData(topCustomers)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}