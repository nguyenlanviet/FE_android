package com.food.order.ui.food

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.food.order.R
import com.food.order.adapter.FoodAdapter
import com.food.order.data.mapper.toFoodModel
import com.food.order.databinding.FragmentFoodBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Collections

class FoodFragment : Fragment() {

    private var _binding: FragmentFoodBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FoodViewModel by viewModels()

    private val userToken: String by lazy {
        ("Bearer " + requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE).getString("token", ""))
    }
    private val adapter: FoodAdapter by lazy {
        FoodAdapter(Collections.emptyList(), { item ->
            val bundle = Bundle()
            bundle.putSerializable("edit_food", item)
            findNavController().navigate(R.id.action_navigation_food_to_navigation_update_food, bundle)
        }, false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFoodBinding.inflate(inflater, container, false)

        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                viewModel.loadingFlow.collectLatest {
                    binding.loadingView.isVisible = it
                }
            }
            launch {
                viewModel.foodsFlow.collect { foods ->
                    binding.recyclerView.isVisible = foods.isNotEmpty()
                    binding.ivEmpty.isVisible = foods.isEmpty()
                    adapter.updateData(foods.map { it.toFoodModel() })
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = this@FoodFragment.adapter
        }
        binding.cardViewBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.tvCreateFood.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_food_to_navigation_create_food)
        }

        viewModel.getFoodsFromServer(userToken)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}