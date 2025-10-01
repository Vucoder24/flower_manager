
package com.example.quanlibanhoa.ui.home

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.quanlibanhoa.R
import com.example.quanlibanhoa.databinding.ActivityHomeBinding
import com.example.quanlibanhoa.ui.home.fragment.AddFlowerFragment
import com.example.quanlibanhoa.ui.home.fragment.AddInvoiceFragment
import com.example.quanlibanhoa.ui.home.fragment.FlowerFragment
import com.example.quanlibanhoa.ui.home.fragment.InvoiceFragment
import com.example.quanlibanhoa.ui.home.fragment.ReportFragment

class HomeActivity : AppCompatActivity() {
    private var activeFragment: Fragment? = null

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupDefaultFragment(savedInstanceState)
        setupBottomNavApp()
    }

    private fun setupDefaultFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            val dashboardFragment = FlowerFragment()
            supportFragmentManager.beginTransaction()
                .add(
                    R.id.frame_layout_home,
                    dashboardFragment,
                    FlowerFragment::class.java.simpleName
                )
                .commit()
            activeFragment = dashboardFragment
        } else {
            // lấy lại fragment đang active sau khi xoay
            activeFragment = supportFragmentManager.fragments.find { !it.isHidden }
        }
    }

    private fun setupBottomNavApp() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavApp) { view, insets ->
            view.setPadding(view.paddingLeft, view.paddingTop, view.paddingRight, 0)
            insets
        }

        binding.bottomNavApp.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.flower -> switchFragment(FlowerFragment())
                R.id.add_flower -> switchFragment(AddFlowerFragment())
                R.id.add_invoice -> switchFragment(AddInvoiceFragment())
                R.id.invoice -> switchFragment(InvoiceFragment())
                R.id.report -> switchFragment(ReportFragment())
                else -> {}
            }
            true
        }
    }

    private fun switchFragment(newFragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        // ẩn fragment hiện tại nếu có
        activeFragment?.let { fragmentTransaction.hide(it) }

        // kiểm tra fragment mới đã tồn tại chưa
        var fragment =
            fragmentManager.findFragmentByTag(newFragment::class.java.simpleName)

        // newFragment chưa có
        if (fragment == null) {
            fragment = newFragment
            fragmentTransaction.add(
                R.id.frame_layout_home,
                fragment,
                fragment::class.java.simpleName
            )
        } else {
            // đã ồn tại fragment
            fragmentTransaction.show(fragment)
        }

        fragmentTransaction.commit()
        activeFragment = fragment
    }
}