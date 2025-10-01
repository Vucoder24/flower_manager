package com.example.quanlibanhoa.ui.home.fragment

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
import com.example.quanlibanhoa.databinding.FragmentFlowerBinding
import com.example.quanlibanhoa.ui.edit_flower.EditFlowerActivity
import com.example.quanlibanhoa.ui.home.HomeActivity
import com.example.quanlibanhoa.ui.home.adapter.FlowerListAdapter
import com.example.quanlibanhoa.ui.home.viewmodel.FlowerViewModel
import com.example.quanlibanhoa.ui.home.viewmodel.FlowerViewModelFactory
import com.example.quanlibanhoa.ui.home.viewmodel.State


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
            onDelete = { flower ->
                AlertDialog.Builder(requireContext())
                    .setMessage("Bạn có chắc chắn muốn xóa hoa này?")
                    .setNegativeButton("Có") { dialog, _ ->
                        flowerViewModel.deleteFlower(flower)
                    }
                    .setPositiveButton("Không") { dialog, _ ->
                        dialog.dismiss()
                    }.show()
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
        flowerViewModel.flowerStateList.observe(viewLifecycleOwner){
            it?.let {
                adapter.setData(it)
            }
        }
        flowerViewModel.flowerState.observe(viewLifecycleOwner){
            when(it){
                State.DELETE_SUCCESS -> {
                    Toast.makeText(
                        requireContext(),
                        "Xóa thành công.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                State.DELETE_ERROR -> {
                    Toast.makeText(
                        requireContext(),
                        "Có lỗi khi xóa hoa, vui lòng thử lại!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}