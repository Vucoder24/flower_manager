package com.example.quanlibanhoa.ui.home.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.quanlibanhoa.R
import com.example.quanlibanhoa.data.entity.Flower
import com.example.quanlibanhoa.databinding.FragmentAddFlowerBinding
import com.example.quanlibanhoa.ui.home.viewmodel.FlowerViewModel
import com.example.quanlibanhoa.ui.home.viewmodel.FlowerViewModelFactory
import com.example.quanlibanhoa.ui.home.viewmodel.State
import java.io.File
import java.io.FileOutputStream


class AddFlowerFragment : Fragment() {

    private var _binding: FragmentAddFlowerBinding? = null
    private val binding get() = _binding!!

    private var selectedImageUri: Uri? = null
    private var imagePath: String? = null
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
        _binding = FragmentAddFlowerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addEvent()
        // observer
        flowerViewModel.flowerState.observe(viewLifecycleOwner){ result ->
            when(result){
                State.ADD_SUCCESS -> {
                    Toast.makeText(
                        requireContext(),
                        "Thêm thành công.",
                        Toast.LENGTH_SHORT
                    ).show()
                    imagePath = null
                    clearForm()
                }
                State.ADD_ERROR -> {
                    // Xóa ảnh nếu có lỗi
                    imagePath?.let { deleteImageFile(it) }
                    Toast.makeText(
                        requireContext(),
                        "Có lỗi khi thêm hoa, vui lòng thử lại!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {}
            }
        }
    }

    private fun addEvent() {
        val pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                binding.imgHoa.setImageURI(it)
            }
        }

        binding.btnPickImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnSave.setOnClickListener {
            val tenHoa = binding.edtTenHoa.text.toString().trim()
            val giaNhap = binding.edtGiaNhap.text.toString().toDoubleOrNull() ?: 0.0
            val giaBan = binding.edtGiaBan.text.toString().toDoubleOrNull() ?: 0.0

            if (tenHoa.isEmpty()) {
                binding.edtTenHoa.error = "Bạn chưa nhập tên hoa!"
                binding.edtTenHoa.requestFocus()
                return@setOnClickListener
            }
            if (giaNhap == 0.0) {
                binding.edtGiaNhap.error = "Bạn chưa nhập giá nhập!"
                binding.edtGiaNhap.requestFocus()
                return@setOnClickListener
            }
            if (giaBan == 0.0) {
                binding.edtGiaBan.error = "Bạn chưa nhập giá bán!"
                binding.edtGiaBan.requestFocus()
                return@setOnClickListener
            }
            if(giaBan < giaNhap){
                binding.edtGiaBan.error = "Giá bán không thể nhỏ hơn giá nhập!"
                binding.edtGiaBan.requestFocus()
                return@setOnClickListener
            }

            // Lưu ảnh vào bộ nhớ nội bộ trước
            imagePath = selectedImageUri?.let { saveImageToInternalStorage(it, tenHoa) }

            val flower = Flower(
                tenHoa = tenHoa,
                giaNhap = giaNhap,
                giaBan = giaBan,
                hinhAnh = imagePath
            )

            flowerViewModel.addFlower(flower)
        }
    }

    private fun saveImageToInternalStorage(uri: Uri, flowerName: String): String? {
        val inputStream = requireContext().contentResolver.openInputStream(uri)?: return null
        var fileName = flowerName.replace(" ", "_") // Thay thế khoảng trắng bằng dấu gạch dưới
        var file = File(requireContext().filesDir, "images/$fileName.jpg")
        var count = 1

        // Kiểm tra nếu tệp đã tồn tại và tạo tên duy nhất
        while (file.exists()) {
            fileName = "${flowerName}_$count".replace(" ", "_")
            file = File(requireContext().filesDir, "images/$fileName.jpg")
            count++
        }

        file.parentFile?.mkdirs() // Tạo thư mục nếu chưa tồn tại
        val outputStream = FileOutputStream(file)

        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        return file.absolutePath // Trả về đường dẫn ảnh
    }

    private fun deleteImageFile(imagePath: String) {
        val file = File(imagePath)
        if (file.exists()) {
            file.delete() // Xóa tệp hình ảnh
        }
    }

    fun clearForm(){
        binding.edtTenHoa.text.clear()
        binding.edtGiaNhap.text.clear()
        binding.edtGiaBan.text.clear()
        binding.imgHoa.setImageResource(R.drawable.ic_photo)
        selectedImageUri = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}