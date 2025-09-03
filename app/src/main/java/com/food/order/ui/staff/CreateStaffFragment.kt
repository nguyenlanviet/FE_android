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
import com.food.order.databinding.FragmentCreateStaffBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CreateStaffFragment : Fragment() {

    private var _binding: FragmentCreateStaffBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StaffViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentCreateStaffBinding.inflate(inflater, container, false)

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
                viewModel.insertFlow.collectLatest { result ->
                    if (result) {
                        Toast.makeText(requireContext(), "Create staff successfully", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    } else {
                        Toast.makeText(requireContext(), "Create staff failed", Toast.LENGTH_SHORT).show()
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
        binding.btnCreate.setOnClickListener {
            val employeeId = binding.edtEmployeeId.text.toString()
            val password = binding.edtPassword.text.toString()
            val displayName = binding.edtDisplayName.text.toString()
            val role = if (binding.radioGroupRole.checkedRadioButtonId == R.id.rbAdmin) "ADMIN" else "WAITER"

            if (employeeId.isEmpty()) {
                binding.edtEmployeeId.error = "Please enter employee ID"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.edtPassword.error = "Please enter password"
                return@setOnClickListener
            }
            if (displayName.isEmpty()) {
                binding.edtDisplayName.error = "Please enter display name"
                return@setOnClickListener
            }

            val request = RegisterRequest(
                employeeId,
                displayName,
                role,
                AppConstants.userModel.employeeId,
                password,
                sharedPref.getString("server_address", "") ?: ""
            )
            viewModel.createStaff(
                sharedPref.getString("token", "") ?: "",
                request
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}