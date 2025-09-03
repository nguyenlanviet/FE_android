package com.food.order.ui.staff

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.food.order.R
import com.food.order.data.AppConstants
import com.food.order.data.request.RegisterRequest
import com.food.order.data.request.UpdateStaffRequest
import com.food.order.databinding.FragmentCreateStaffBinding
import com.food.order.databinding.FragmentUpdateStaffBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UpdateStaffFragment : Fragment() {

    private var _binding: FragmentUpdateStaffBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StaffViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUpdateStaffBinding.inflate(inflater, container, false)

        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                viewModel.loadingFlow.collectLatest { result ->
                    if (result) {
                        binding.loadingView.show()
                    } else {
                        binding.loadingView.hide()
                    }
                }
            }
            launch {
                viewModel.updateFlow.collectLatest { result ->
                    if (result) {
                        Toast.makeText(requireContext(), "Update staff successfully", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    } else {
                        Toast.makeText(requireContext(), "Update staff failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            launch {
                viewModel.deleteFlow.collectLatest { result ->
                    if (result) {
                        Toast.makeText(requireContext(), "Delete staff successfully", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    } else {
                        Toast.makeText(requireContext(), "Delete staff failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            launch {
                viewModel.staffFlow.collectLatest { result ->
                    result?.let {
                        binding.edtEmployeeId.setText(it.employeeId)
                        binding.edtPassword.setText(it.employeeId)
                        binding.edtDisplayName.setText(it.displayName)
                        if (it.role == "WAITER") {
                            binding.rbWaiter.isChecked = true
                        } else {
                            binding.rbAdmin.isChecked = true
                        }
                    }
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        binding.cardViewBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnUpdate.setOnClickListener {
            val employeeId = binding.edtEmployeeId.text.toString()
            val password = binding.edtPassword.text.toString()
            val displayName = binding.edtDisplayName.text.toString()
            val role = if (binding.rbWaiter.isChecked) {
                "WAITER"
            } else {
                "ADMIN"
            }
            if (password.isEmpty()) {
                binding.edtPassword.error = "Password is required"
                return@setOnClickListener
            }
            if (displayName.isEmpty()) {
                binding.edtDisplayName.error = "Display name is required"
                return@setOnClickListener
            }
            val request = UpdateStaffRequest(
                displayName = displayName,
                password = password,
                role = role,
            )
            viewModel.updateStaff(
                sharedPref.getString("token", "") ?: "",
                sharedPref.getString("server_address", "") ?: "",
                employeeId,
                request
            )
        }
        binding.btnRemove.setOnClickListener {
            val employeeId = binding.edtEmployeeId.text.toString()
            viewModel.deleteStaff(
                sharedPref.getString("token", "") ?: "",
                sharedPref.getString("server_address", "") ?: "",
                employeeId
            )
        }
        viewModel.setArgument(arguments)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}