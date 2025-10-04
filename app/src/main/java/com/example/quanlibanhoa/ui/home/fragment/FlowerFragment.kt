package com.example.quanlibanhoa.ui.home.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quanlibanhoa.data.entity.Flower
import com.example.quanlibanhoa.databinding.FragmentFlowerBinding
import com.example.quanlibanhoa.ui.edit_flower.EditFlowerActivity
import com.example.quanlibanhoa.ui.home.HomeActivity
import com.example.quanlibanhoa.ui.home.adapter.FlowerListAdapter
import com.example.quanlibanhoa.ui.home.viewmodel.FlowerViewModel
import com.example.quanlibanhoa.ui.home.viewmodel.FlowerViewModelFactory
import com.example.quanlibanhoa.ui.home.viewmodel.StateFlower


class FlowerFragment : Fragment() {
    private var _binding: FragmentFlowerBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: FlowerListAdapter

    val flowerViewModel: FlowerViewModel by activityViewModels {
        FlowerViewModelFactory(
            requireContext()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFlowerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // setup ryc view
        adapter = FlowerListAdapter(
            onEdit = { flower ->
                val intent = Intent(context, EditFlowerActivity::class.java).apply {
                    putExtra("flower_id", flower.id.toString())
                    putExtra("flower_name", flower.tenHoa)
                    putExtra("flower_price_in", flower.giaNhap)
                    putExtra("flower_price", flower.giaBan)
                    putExtra("flower_image_path", flower.hinhAnh)
                }
                requireContext().startActivity(intent)
                (requireContext() as HomeActivity).slideNewActivity()
            },
            onDeleteSelected = { list -> showDeleteConfirmDialog(list) },
            onMultiSelectModeChanged = { isEnabled ->
                if (isEnabled) {
                    showDeleteToolbar() // Tự tạo hàm hiển thị Toolbar xóa
                } else {
                    hideDeleteToolbar() // Tự tạo hàm ẩn Toolbar xóa
                }
            },
            onSelectionCountChanged = { count ->
                updateDeleteToolbarText(count) // Cập nhật text "Xóa (N)"
            }
        )
        // Gán adapter cho RecyclerView
        val dividerItemDecoration =
            DividerItemDecoration(binding.rvFlowerList.context, LinearLayoutManager.VERTICAL)
        binding.rvFlowerList.addItemDecoration(dividerItemDecoration)
        binding.rvFlowerList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFlowerList.adapter = adapter

        observerData()
    }

    private fun observerData() {
        flowerViewModel.flowerStateList.observe(viewLifecycleOwner) {
            it?.let {
                adapter.setData(it)
            }
        }
        flowerViewModel.deleteFlowerState.observe(viewLifecycleOwner) { result ->
            if (result == StateFlower.IDLE) return@observe

            when (result) {
                StateFlower.DELETE_FLOWER_SUCCESS -> {
                    Toast.makeText(
                        requireContext(),
                        "Xóa thành công.",
                        Toast.LENGTH_SHORT
                    ).show()
                    flowerViewModel.resetDeleteState()
                }

                StateFlower.DELETE_FLOWER_ERROR -> {
                    Toast.makeText(
                        requireContext(),
                        "Có lỗi khi xóa hoa, vui lòng thử lại!",
                        Toast.LENGTH_SHORT
                    ).show()
                    flowerViewModel.resetDeleteState()
                }

                else -> {}
            }
        }
    }

    private fun showDeleteConfirmDialog(flowersToDelete: List<Flower>) {
        if (flowersToDelete.isEmpty()) return

        AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa ${flowersToDelete.size} mục đã chọn?")
            .setPositiveButton("Xóa") { _, _ ->
                // xóa list flower
                flowerViewModel.deleteFlowers(flowersToDelete)
                // Thoát chế độ chọn sau khi gửi lệnh xóa
                adapter.clearSelection()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun showDeleteToolbar() {
        // Hiển thị thanh công cụ xóa
        binding.deleteToolbar.visibility = View.VISIBLE

        // Đảm bảo nút "Bỏ chọn tất cả" gọi hàm clearSelection của Adapter
        binding.btnClearSelection.setOnClickListener {
            adapter.clearSelection()
        }

        // Đảm bảo nút "Xóa" gọi hàm hiển thị hộp thoại xác nhận xóa
        binding.btnConfirmDelete.setOnClickListener {
            showDeleteConfirmDialog(adapter.selectedFlowers.toList())
        }
    }

    // Hàm ẩn thanh công cụ xóa
    private fun hideDeleteToolbar() {

        // Ẩn thanh công cụ xóa
        binding.deleteToolbar.visibility = View.GONE

        // Đảm bảo không còn lựa chọn nào
        updateDeleteToolbarText(0)
    }

    @SuppressLint("SetTextI18n")
    private fun updateDeleteToolbarText(count: Int) {
        // Cập nhật Text của nút Xóa (ví dụ: "Xóa (3)")
        binding.btnConfirmDelete.text = "Xóa ($count)"

        // Đảm bảo nút Xóa bị vô hiệu hóa nếu không có mục nào được chọn
        binding.btnConfirmDelete.isEnabled = count > 0

        // *TÙY CHỌN*: Nếu count bằng 0, tự động thoát chế độ chọn
        // (Lưu ý: Logic này đã được xử lý trong Adapter, nhưng bạn có thể dùng nó để đảm bảo)
        if (count == 0 && adapter.isMultiSelectMode) {
            adapter.setMultiSelectMode(false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}