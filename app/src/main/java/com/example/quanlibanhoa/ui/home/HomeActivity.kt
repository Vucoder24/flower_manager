
package com.example.quanlibanhoa.ui.home

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.quanlibanhoa.R
import com.example.quanlibanhoa.databinding.ActivityHomeBinding
import com.example.quanlibanhoa.ui.home.fragment.AddFlowerFragment
import com.example.quanlibanhoa.ui.home.fragment.AddInvoiceFragment
import com.example.quanlibanhoa.ui.home.fragment.ExceptedInvoiceFragment
import com.example.quanlibanhoa.ui.home.fragment.FlowerFragment
import com.example.quanlibanhoa.ui.home.fragment.InvoiceFragment
import com.example.quanlibanhoa.ui.home.fragment.ReportFragment
import com.example.quanlibanhoa.ui.home.viewmodel.FlowerViewModel
import com.example.quanlibanhoa.ui.home.viewmodel.FlowerViewModelFactory
import com.example.quanlibanhoa.ui.home.viewmodel.InvoiceViewModel
import com.example.quanlibanhoa.ui.home.viewmodel.InvoiceViewModelFactory
import kotlin.getValue

class HomeActivity : AppCompatActivity() {
    private var activeFragment: Fragment? = null

    private lateinit var binding: ActivityHomeBinding

    @Suppress("unused")
    val flowerViewModel: FlowerViewModel by viewModels {
        FlowerViewModelFactory(
            this
        )
    }

    @Suppress("unused")
    val invoiceViewModel: InvoiceViewModel by viewModels {
        InvoiceViewModelFactory(
            this
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
                R.id.excepted_invoice -> switchFragment(ExceptedInvoiceFragment())
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
    fun slideNewActivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 14 trở lên
            overrideActivityTransition(
                OVERRIDE_TRANSITION_OPEN,
                R.anim.slide_in_right,
                0
            )
        } else {
            // Android 13 trở xuống
            @Suppress("DEPRECATION")
            overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.fade_out
            )
        }
    }
}