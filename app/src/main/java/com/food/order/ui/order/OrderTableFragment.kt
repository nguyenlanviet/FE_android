package com.food.order.ui.order

import android.annotation.SuppressLint
import android.app.AlertDialog
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
import com.food.order.R
import com.food.order.adapter.OrderFoodAdapter
import com.food.order.data.AppConstants
import com.food.order.databinding.FragmentOrderTableBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Collections

class OrderTableFragment : Fragment() {
    private var _binding: FragmentOrderTableBinding? = null
    private val binding get() = _binding!!
    private val viewModel: OrderTableViewModel by viewModels()
    private val userToken: String by lazy {
        ("Bearer " + requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE).getString("token", ""))
    }
    private val adapter: OrderFoodAdapter by lazy {
        OrderFoodAdapter(Collections.emptyList()) { item ->
            AlertDialog.Builder(requireContext())
                .setTitle("Notification")
                .setMessage("Do you want to remove this item from your bill?")
                .setPositiveButton("OK") { dialog, _ ->
                    viewModel.removeItemFromOrder(userToken, item.foodId)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOrderTableBinding.inflate(inflater, container, false)
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                viewModel.tableFlow.collectLatest {
                    binding.tvTableName.text = it?.name
                }
            }
            launch {
                viewModel.employeeOrderFlow.collectLatest {
                    binding.tvWaiter.text = it?.createdBy
                }
            }
            launch {
                viewModel.copyOrderFlow.collectLatest {
                    Toast.makeText(requireContext(), "Copy order successfully", Toast.LENGTH_SHORT).show()
                }
            }
            launch {
                viewModel.orderFlow.collectLatest {
                    adapter.updateData(it.data.items)
                    binding.tvCreateAt.text = it.data.createdAt
                    binding.tvTotalAmount.text = it.data.totalAmount.toString()
                }
            }
            launch {
                viewModel.cancelOrderFlow.collectLatest {
                    findNavController().popBackStack()
                }
            }
            launch {
                viewModel.completedOrderFlow.collectLatest {
                    Toast.makeText(requireContext(), "Completed order successfully", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            }
            launch {
                viewModel.removeItemFromOrderFlow.collectLatest {
                    Toast.makeText(requireContext(), "Remove item from order successfully", Toast.LENGTH_SHORT).show()
                    viewModel.getDetailTable(userToken)
                }
            }
            launch {
                viewModel.tablesFreeFlow.collectLatest {
                    if (it.isEmpty()) {
                        Toast.makeText(requireContext(), "There are no free tables", Toast.LENGTH_SHORT).show()
                    } else {
                        val tableNames = arrayOfNulls<String>(it.size)
                        for (i in it.indices) {
                            tableNames[i] = it[i].name
                        }

                        AlertDialog.Builder(context)
                            .setTitle("Choose a free table")
                            .setItems(tableNames) { dialog, which ->
                                dialog.dismiss()
                                viewModel.copyTableOrder(userToken, it[which].id)
                            }
                            .setNegativeButton("Cancel", null)
                            .show()
                    }
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
            adapter = this@OrderTableFragment.adapter
        }
        binding.cardViewBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.cardCopyOrder.setOnClickListener {
            viewModel.getTablesFromServer(userToken)
        }
        binding.btnCompleted.setOnClickListener {
            if (AppConstants.userModel.employeeId == binding.tvWaiter.text) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Notification")
                    .setMessage("Confirm payment of this invoice?")
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                        viewModel.completeOrder(userToken)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            } else {
                AlertDialog.Builder(requireContext())
                    .setTitle("Notification")
                    .setMessage("You are not the waiter")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }
        binding.cardOrder.setOnClickListener {
            if (AppConstants.userModel.employeeId == binding.tvWaiter.text) {
                val bundle = Bundle()
                bundle.putString("orderId", viewModel.orderId)
                findNavController().navigate(R.id.action_navigation_order_table_to_navigation_order_food, bundle)
            } else {
                AlertDialog.Builder(requireContext())
                    .setTitle("Notification")
                    .setMessage("You are not the waiter")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }
        binding.btnCancel.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Notification")
                .setMessage("Are you sure you want to cancel your order?")
                .setPositiveButton("OK") { dialog, _ ->
                    viewModel.cancelOrder(userToken)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
        viewModel.apply {
            setArguments(arguments)
            getDetailTable(userToken)
            getCreateByOrder(userToken)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}