package com.food.order.ui.menu

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.food.order.databinding.DialogSystemMenuBinding

class SystemMenuDialog(
    private val onFeatureClick: (featureId: String) -> Unit = {}
) : DialogFragment() {
    private lateinit var binding: DialogSystemMenuBinding

    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        binding = DialogSystemMenuBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        initViews()

        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let { window ->
            val width = (resources.displayMetrics.widthPixels * 0.8).toInt()
            window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    private fun initViews() {
        binding.menuViewDashboard.setOnClickListener {
            dismiss()
            onFeatureClick.invoke("DASHBOARD")
        }
        binding.menuViewFoodMenu.setOnClickListener {
            dismiss()
            onFeatureClick.invoke("FOOD_MENU")
        }
        binding.menuViewOrderManagement.setOnClickListener {
            dismiss()
            onFeatureClick.invoke("ORDER_MANAGEMENT")
        }
        binding.menuViewTables.setOnClickListener {
            dismiss()
            onFeatureClick.invoke("TABLES")
        }
        binding.menuViewStaff.setOnClickListener {
            dismiss()
            onFeatureClick.invoke("STAFF")
        }
        binding.menuViewReport.setOnClickListener {
            dismiss()
            onFeatureClick.invoke("REPORTS")
        }
    }

}