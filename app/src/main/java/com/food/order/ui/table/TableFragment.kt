package com.food.order.ui.table

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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.food.order.R
import com.food.order.adapter.FoodAdapter
import com.food.order.adapter.TableAdapter
import com.food.order.data.mapper.toFoodModel
import com.food.order.databinding.FragmentFoodBinding
import com.food.order.databinding.FragmentTableBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Collections

class TableFragment : Fragment() {

    private var _binding: FragmentTableBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TableViewModel by viewModels()

    private val userToken: String by lazy {
        ("Bearer " + requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE).getString("token", ""))
    }
    private val adapter: TableAdapter by lazy {
        TableAdapter(Collections.emptyList()) { item ->
            val bundle = Bundle()
            bundle.putSerializable("edit_table", item)
            findNavController().navigate(R.id.action_navigation_table_to_navigation_update_table, bundle)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTableBinding.inflate(inflater, container, false)

        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                viewModel.loadingFlow.collectLatest {
                    binding.loadingView.isVisible = it
                }
            }
            launch {
                viewModel.tablesFlow.collect { tables ->
                    binding.recyclerView.isVisible = tables.isNotEmpty()
                    binding.ivEmpty.isVisible = tables.isEmpty()
                    adapter.updateData(tables)
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = this@TableFragment.adapter
        }
        binding.cardViewBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.tvCreateTable.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_table_to_navigation_create_table)
        }

        viewModel.getTablesFromServer(userToken)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}