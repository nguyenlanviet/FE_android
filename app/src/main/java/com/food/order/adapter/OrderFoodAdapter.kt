package com.food.order.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.food.order.R
import com.food.order.data.RetrofitClient
import com.food.order.data.model.FoodModel
import com.food.order.data.model.OrderItem
import com.food.order.databinding.ItemFoodBinding
import com.food.order.databinding.ItemOrderFoodBinding

class OrderFoodAdapter(
    private var data: List<OrderItem>,
    private val onItemClick: (OrderItem) -> Unit,
) : RecyclerView.Adapter<OrderFoodAdapter.OrderFoodViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: List<OrderItem>) {
        data = newData
        notifyDataSetChanged()
    }

    inner class OrderFoodViewHolder(
        val binding: ItemOrderFoodBinding,
        val onItemClick: (OrderItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(food: OrderItem) {
            binding.tvFoodName.text = food.foodName
            binding.tvAmount.text = "${food.price}VNĐ"
            binding.tvQuantity.text = "${food.quantity}"
            try {
                Glide.with(binding.root)
                    .load(RetrofitClient.BASE_URL.plus(food.foodImage.substring(1)))
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(binding.ivFood)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            binding.root.setOnLongClickListener {
                onItemClick(food)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderFoodViewHolder {
        val binding = ItemOrderFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderFoodViewHolder(binding, onItemClick)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: OrderFoodViewHolder, position: Int) {
        holder.bind(data[position])
    }
}