package com.food.order.ui.dashboard

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import com.food.order.R
import com.food.order.adapter.TableDashboardAdapter
import com.food.order.data.AppConstants
import com.food.order.databinding.FragmentDashboardBinding
import com.food.order.ui.menu.SystemMenuDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Collections

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DashboardViewModel by viewModels()
    private val userToken: String by lazy {
        ("Bearer " + requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE).getString("token", ""))
    }
    private val adapter: TableDashboardAdapter by lazy {
        TableDashboardAdapter(Collections.emptyList()) {
            if (it.currentOrderId != null) {
                val bundle = Bundle().apply {
                    putString("tableId", it.id)
                }
                findNavController().navigate(R.id.action_navigation_dashboard_to_navigation_order_table, bundle)
            } else {
                AlertDialog.Builder(requireContext())
                    .setTitle("Notification")
                    .setMessage("Are you sure you want to book this table?")
                    .setPositiveButton("OK") { dialog, _ ->
                        viewModel.bookTable(it.id, userToken)
                        dialog.dismiss()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        registerObserver()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!AppConstants.checkExistsUser()) return
        binding.recyclerView.adapter = adapter
        binding.tvEmployeeId.text = AppConstants.userModel.employeeId
        binding.tvName.text = AppConstants.userModel.displayName
        binding.tvRole.text = AppConstants.userModel.role

        binding.ivMenu.setOnClickListener {
            val dialog = SystemMenuDialog() { featureId ->
                when (featureId) {
                    "STAFF" -> {
                        val request = NavDeepLinkRequest.Builder
                            .fromUri(Uri.parse("fo://staff"))
                            .build()

                        findNavController().navigate(request)
                    }

                    "FOOD_MENU" -> {
                        val request = NavDeepLinkRequest.Builder
                            .fromUri(Uri.parse("fo://food"))
                            .build()

                        findNavController().navigate(request)
                    }

                    "TABLES" -> {
                        val request = NavDeepLinkRequest.Builder
                            .fromUri(Uri.parse("fo://table"))
                            .build()

                        findNavController().navigate(request)
                    }

                    "ORDER_MANAGEMENT" -> {
                        val request = NavDeepLinkRequest.Builder
                            .fromUri(Uri.parse("fo://order_statistic"))
                            .build()

                        findNavController().navigate(request)
                    }

                    "REPORTS" -> {
                        val request = NavDeepLinkRequest.Builder
                            .fromUri(Uri.parse("fo://report"))
                            .build()
                        findNavController().navigate(request)
                    }
                }
            }
            dialog.show(requireActivity().supportFragmentManager, "SYSTEM_MENU_DIALOG")
        }
        viewModel.getTablesFromServer(userToken)
    }

    private fun registerObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                viewModel.tablesFlow.collectLatest { tables ->
                    adapter.updateData(tables)
                    binding.apply {
                        recyclerView.isVisible = tables.isNotEmpty()
                        tvTotalTable.text = "${tables.size}"
                        tvTotalUseTable.text = "${tables.count { it.currentOrderId != null }}"
                    }
                }
            }
            launch {
                viewModel.errorFlow.collectLatest {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
            launch {
                viewModel.bookingFlow.collectLatest {
                    Toast.makeText(requireContext(), "Booking success", Toast.LENGTH_SHORT).show()
                    viewModel.getTablesFromServer(userToken)


                    val bundle = Bundle().apply {
                        putString("tableId", it)
                    }
                    findNavController().navigate(R.id.action_navigation_dashboard_to_navigation_order_table, bundle)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}