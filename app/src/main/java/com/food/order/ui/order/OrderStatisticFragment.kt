package com.food.order.ui.order

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.food.order.adapter.OrderStatisticAdapter
import com.food.order.databinding.FragmentOrderStatisticBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Collections

class OrderStatisticFragment : Fragment() {
    private var _binding: FragmentOrderStatisticBinding? = null
    private val binding get() = _binding!!
    private val viewModel: OrderTableViewModel by viewModels()
    private val userToken: String by lazy {
        ("Bearer " + requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE).getString("token", ""))
    }
    private val adapter: OrderStatisticAdapter by lazy {
        OrderStatisticAdapter(Collections.emptyList()) {}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentOrderStatisticBinding.inflate(inflater, container, false)
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                launch {
                    viewModel.listOrderFlow.collectLatest {
                        adapter.updateData(it)
                    }
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cardViewBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@OrderStatisticFragment.adapter
        }
        viewModel.listOrders(userToken)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}