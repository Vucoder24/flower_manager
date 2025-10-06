package com.example.quanlibanhoa.ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.quanlibanhoa.databinding.FragmentInvoiceBinding
import com.example.quanlibanhoa.ui.home.fragment.invoice.DailyInvoiceFragment
import com.example.quanlibanhoa.ui.home.fragment.invoice.ExceptedInvoiceFragment
import com.example.quanlibanhoa.ui.home.fragment.invoice.MonthlyInvoiceFragment
import com.example.quanlibanhoa.ui.home.fragment.invoice.WeeklyInvoiceFragment
import com.example.quanlibanhoa.ui.home.fragment.invoice.YearlyInvoiceFragment
import com.google.android.material.tabs.TabLayoutMediator


class InvoiceFragment : Fragment() {

    private var _binding: FragmentInvoiceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentInvoiceBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTabLayoutHistoryTransaction()
    }

    private fun setupTabLayoutHistoryTransaction() {
        if (binding.viewPagerHistory.adapter == null) {
            val adapter = ViewPagerHistoryAdapter(requireActivity())
            binding.viewPagerHistory.adapter = adapter
            binding.viewPagerHistory.isUserInputEnabled = false
            // nối tab layout với viewpager2
            TabLayoutMediator(
                binding.tabLayoutHistoryInvoice,
                binding.viewPagerHistory
            ) { tab, position ->
                tab.text = when (position) {
                    0 -> "Dự kiến"
                    1 -> "Hôm nay"
                    2 -> "Tuần này"
                    3 -> "Tháng này"
                    4 -> "Năm này"
                    else -> null
                }
            }.attach()
            binding.viewPagerHistory.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class ViewPagerHistoryAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> ExceptedInvoiceFragment()
                1 -> DailyInvoiceFragment()
                2 -> WeeklyInvoiceFragment()
                3 -> MonthlyInvoiceFragment()
                4 -> YearlyInvoiceFragment()
                else -> throw IllegalArgumentException("(HistoryInvoiceFragment) Invalid position")
            }
        }

        override fun getItemCount(): Int = 5
    }

}