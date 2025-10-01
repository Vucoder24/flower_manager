package com.example.quanlibanhoa.ui.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlibanhoa.R
import com.example.quanlibanhoa.data.entity.Flower

class FlowerListAdapter(
    private val onEdit: (Flower) -> Unit,
    private val onDelete: (Flower) -> Unit
) : RecyclerView.Adapter<FlowerListAdapter.ViewHolder>() {

    private val flowers = mutableListOf<Flower>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<Flower>) {
        flowers.clear()
        flowers.addAll(data)
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgFlower: ImageView = view.findViewById(R.id.imgFlower)
        val txtName: TextView = view.findViewById(R.id.txtName)
        val txtPrice: TextView = view.findViewById(R.id.txtPrice)
        val txtPriceIn: TextView = view.findViewById(R.id.txtPriceIn)
        val btnEdit: ImageButton = view.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_flower, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = flowers.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val flower = flowers[position]
        holder.txtName.text = flower.tenHoa
        holder.txtPriceIn.text = "Giá nhập: ${flower.giaNhap.toInt()}"
        holder.txtPrice.text = "Giá bán: ${flower.giaBan.toInt()}"

        if (flower.hinhAnh != null) {
            holder.imgFlower.setImageURI(flower.hinhAnh.toUri())
        } else {
            holder.imgFlower.setImageResource(R.drawable.ic_photo)
        }

        holder.btnEdit.setOnLongClickListener {
            onEdit(flower)
            true
        }
        holder.btnDelete.setOnClickListener { onDelete(flower) }
    }
}
