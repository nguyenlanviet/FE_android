package com.food.order.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.food.order.data.AppConstants
import com.food.order.data.model.UserModel
import com.food.order.databinding.ItemStaffBinding

class StaffAdapter(
    private var users: List<UserModel>,
    private val onItemClick: (UserModel) -> Unit
) : RecyclerView.Adapter<StaffAdapter.StaffViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newUsers: List<UserModel>) {
        users = newUsers
        notifyDataSetChanged()
    }

    inner class StaffViewHolder(
        val binding: ItemStaffBinding,
        val onItemClick: (UserModel) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(user: UserModel) {
            if (user.employeeId == AppConstants.userModel.employeeId) {
                binding.tvName.text = "You"
            } else {
                binding.tvName.text = user.displayName
            }
            binding.tvEmployeeId.text = "EmployeeID: ${user.employeeId}"
            binding.tvCreateAt.text = "Create At: ${user.createdAt}"
            binding.tvRole.text = "Role: ${user.role}"
            if (user.createdBy == null) {
                binding.tvCreateBy.isVisible = false
            } else {
                binding.tvCreateBy.isVisible = true
                binding.tvCreateBy.text = "Create by: ${user.createdBy}"
            }
            binding.root.setOnClickListener {
                if (user.employeeId == AppConstants.userModel.employeeId) {
                    Toast.makeText(binding.root.context, "You cannot edit yourself", Toast.LENGTH_SHORT).show()
                } else {
                    onItemClick(user)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaffViewHolder {
        val binding = ItemStaffBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StaffViewHolder(binding, onItemClick)
    }

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: StaffViewHolder, position: Int) {
        holder.bind(users[position])
    }
}