package com.food.order.ui.food

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.food.order.R
import com.food.order.data.request.FoodRequest
import com.food.order.databinding.FragmentCreateFoodBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CreateFoodFragment : Fragment() {

    private var _binding: FragmentCreateFoodBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FoodViewModel by viewModels()

    private val userToken: String by lazy {
        ("Bearer " + requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE).getString("token", ""))
    }

    private var imagePickerLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null && data.data != null) {
                viewModel.setImageUri(data.data!!)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentCreateFoodBinding.inflate(inflater, container, false)

        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                viewModel.loadingFlow.collectLatest { result ->
                    binding.loadingView.isVisible = result
                }
            }
            launch {
                viewModel.imageUriFlow.collectLatest { result ->
                    binding.ivFoodImage.setImageURI(result)
                }
            }
            launch {
                viewModel.errorFlow.collectLatest { result ->
                    Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show()
                }
            }
            launch {
                viewModel.insertFlow.collectLatest { result ->
                    if (result) {
                        Toast.makeText(requireContext(), "Create food success", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    } else {
                        Toast.makeText(requireContext(), "Create food failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        return binding.root
    }

    @SuppressLint("IntentReset")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardViewBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.cardViewAvatar.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            imagePickerLauncher.launch(intent)
        }
        binding.btnCreate.setOnClickListener {
            val name = binding.edtName.text.toString()
            val price = binding.edtPrice.text.toString()
            val unit = binding.edtUnit.text.toString()
            val description = binding.edtDescription.text.toString()

            val category = when (binding.radioGroupCategory.checkedRadioButtonId) {
                R.id.rbMainCourse -> "MAIN_COURSE"
                R.id.rbAppetizer -> "APPETIZER"
                R.id.rbDessert -> "DESSERT"
                R.id.rbBeverage -> "BEVERAGE"
                else -> ""
            }
            if (userToken.isEmpty()) {
                Toast.makeText(requireContext(), "Please login first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (viewModel.imageUri == null) {
                Toast.makeText(requireContext(), "Please select image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (name.isEmpty()) {
                binding.edtName.error = "Please enter name"
                return@setOnClickListener
            }
            if (price.isEmpty()) {
                binding.edtPrice.error = "Please enter price"
                return@setOnClickListener
            }
            if (unit.isEmpty()) {
                binding.edtUnit.error = "Please enter unit"
                return@setOnClickListener
            }
            if (category.isEmpty()) {
                Toast.makeText(requireContext(), "Please select category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val request = FoodRequest(
                foodName = name,
                price = price.toDouble(),
                unit = unit,
                category = category,
                image = "",
                description = description,
            )
            viewModel.createFood(requireContext(), userToken, request)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}