package com.food.order.ui.login

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.food.order.R
import com.food.order.data.AppConstants
import com.food.order.data.request.LoginRequest
import com.food.order.databinding.FragmentLoginBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        // Collect dữ liệu từ ViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                viewModel.tokenFlow.collectLatest { token ->
                    sharedPref.edit().putString("token", token.token).apply()
                }
            }
            launch {
                viewModel.userFlow.collectLatest { user ->
                    AppConstants.userModel = user
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.mobile_navigation, true)
                        .build()

                    val request = NavDeepLinkRequest.Builder
                        .fromUri(Uri.parse("fo://dashboard"))
                        .build()

                    findNavController().navigate(request, navOptions)
                }
            }
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
                viewModel.errorFlow.collectLatest { error ->
                    binding.edtEmployeeId.error = error
                }
            }
        }

        binding.cardViewBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnLogin.setOnClickListener {
            val employeeId = binding.edtEmployeeId.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()
            if (employeeId.isEmpty()) {
                binding.edtEmployeeId.error = "Employee ID is required"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.edtPassword.error = "Password is required"
                return@setOnClickListener
            }
            viewModel.login(LoginRequest(employeeId, password, sharedPref.getString("server_address", "") ?: ""))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}