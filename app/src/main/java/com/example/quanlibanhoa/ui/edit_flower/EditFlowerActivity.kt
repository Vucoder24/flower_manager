package com.example.quanlibanhoa.ui.edit_flower

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quanlibanhoa.R
import com.example.quanlibanhoa.data.entity.Flower
import com.example.quanlibanhoa.databinding.ActivityEditFlowerBinding
import com.example.quanlibanhoa.ui.home.viewmodel.FlowerViewModel
import com.example.quanlibanhoa.ui.home.viewmodel.FlowerViewModelFactory
import com.example.quanlibanhoa.ui.home.viewmodel.State
import java.io.File
import java.io.FileOutputStream

class EditFlowerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditFlowerBinding
    private var flowerId: String = ""
    private var selectedImageUri: Uri? = null
    private var imagePath: String? = null
    private var oldImagePath: String? = null
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    val flowerViewModel: FlowerViewModel by viewModels {
        FlowerViewModelFactory(
            this
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditFlowerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Nhận dữ liệu từ Intent
        flowerId = intent.getStringExtra("flower_id").toString()
        val flowerName = intent.getStringExtra("flower_name")
        val flowerPriceIn = intent.getDoubleExtra("flower_price_in", 0.0)
        val flowerPriceOut = intent.getDoubleExtra("flower_price", 0.0)
        oldImagePath = intent.getStringExtra("flower_image_path")

        // Thiết lập dữ liệu vào các trường
        binding.edtTenHoa.setText(flowerName)
        binding.edtGiaNhap.setText(flowerPriceIn.toInt().toString())
        binding.edtGiaBan.setText(flowerPriceOut.toInt().toString())
        // Thiết lập hình ảnh nếu có
        oldImagePath?.let {
            binding.imgHoa.setImageURI(it.toUri())
        }?: binding.imgHoa.setImageResource(R.drawable.ic_photo)

        addEvent()
        // observer
        flowerViewModel.flowerState.observe(this){ result ->
            when(result){
                State.EDIT_SUCCESS -> {
                    Toast.makeText(
                        applicationContext,
                        "Sửa thành công.",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Chỉ xóa ảnh cũ nếu ảnh mới đã được lưu thành công
                    imagePath?.let {
                        oldImagePath?.let { deleteImageFile(it) }
                    }
                    imagePath = null
                    clearForm()
                    finish()
                    slideOutActivity()
                }
                State.EDIT_ERROR -> {
                    // Xóa ảnh nếu có lỗi
                    imagePath?.let { deleteImageFile(it) }
                    Toast.makeText(
                        applicationContext,
                        "Có lỗi khi sửa hoa, vui lòng thử lại!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {}
            }
        }
    }

    private fun addEvent() {
        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                binding.imgHoa.setImageURI(it)
            }
        }

        binding.btnPickImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnBack.setOnClickListener {
            finish()
            slideOutActivity()
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
                id = flowerId.toInt(),
                tenHoa = tenHoa,
                giaNhap = giaNhap,
                giaBan = giaBan,
                hinhAnh = imagePath ?: oldImagePath
            )

            flowerViewModel.editFlower(flower)
        }
    }

    private fun saveImageToInternalStorage(uri: Uri, flowerName: String): String? {
        val inputStream = contentResolver.openInputStream(uri)?: return null
        var fileName = flowerName.replace(" ", "_")
        var file = File(filesDir, "images/$fileName.jpg")
        var count = 1

        // Kiểm tra nếu tệp đã tồn tại và tạo tên duy nhất
        while (file.exists()) {
            fileName = "${flowerName}_$count".replace(" ", "_")
            file = File(filesDir, "images/$fileName.jpg")
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

    private fun slideOutActivity(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 14 trở lên
            overrideActivityTransition(
                OVERRIDE_TRANSITION_OPEN,
                R.anim.slide_out_right,
                0
            )
        } else {
            // Android 13 trở xuống
            @Suppress("DEPRECATION")
            overridePendingTransition(
                R.anim.fade_in,
                R.anim.slide_out_right
            )
        }
    }

    fun clearForm(){
        binding.edtTenHoa.text.clear()
        binding.edtGiaNhap.text.clear()
        binding.edtGiaBan.text.clear()
        binding.imgHoa.setImageResource(R.drawable.ic_photo)
        selectedImageUri = null
    }
}