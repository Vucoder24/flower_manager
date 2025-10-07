package com.example.quanlibanhoa.ui.home.fragment.report_invoice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.quanlibanhoa.databinding.FragmentTodayReportBinding
import com.example.quanlibanhoa.ui.home.viewmodel.InvoiceViewModel
import com.example.quanlibanhoa.ui.home.viewmodel.InvoiceViewModelFactory

class TodayReportFragment : Fragment() {
    private var _binding: FragmentTodayReportBinding? = null
    private val binding get() = _binding!!


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
        _binding = FragmentTodayReportBinding.inflate(
            layoutInflater,
            container,
            false
        )
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}