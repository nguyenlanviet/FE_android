package com.food.order.ui.report

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.chart.common.listener.Event
import com.anychart.chart.common.listener.ListenersInterface
import com.anychart.enums.Align
import com.anychart.enums.LegendLayout
import com.food.order.data.response.RevenueByWeek
import com.food.order.databinding.FragmentReportBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ReportFragment : Fragment() {

    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ReportViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                viewModel.countEmployeeFlow.collectLatest {
                    binding.tvUserNumber.text = it.toString()
                }
            }
            launch {
                viewModel.timeFlow.collectLatest {
                    binding.tvMonthYear.text = it
                }
            }
            launch {
                viewModel.mostFavoriteFoodFlow.collectLatest {
                    if (it?.data != null) {
                        binding.tvMostFood.text = it.data.foodName
                    } else {
                        binding.tvMostFood.text = "No data"
                    }
                }
            }
            launch {
                viewModel.listOrderInTimeFlow.collectLatest {
                    if (it != null) {
                        binding.tvPaymentInvoiceNumber.text = it.size.toString()
                        var countOrdering = 0
                        var countCompleted = 0
                        var countCancelled = 0

                        for (order in it) {
                            when (order.status) {
                                "ORDERING" -> countOrdering++
                                "COMPLETED" -> countCompleted++
                                "CANCELLED" -> countCancelled++
                            }
                        }
                        binding.tvCountOrdering.text = countOrdering.toString()
                        binding.tvCountCompleted.text = countCompleted.toString()
                        binding.tvCountCancelled.text = countCancelled.toString()
                    } else {
                        binding.tvPaymentInvoiceNumber.text = "0"
                        binding.tvCountOrdering.text = "0"
                        binding.tvCountCompleted.text = "0"
                        binding.tvCountCancelled.text = "0"
                    }
                }
            }
            launch {
                viewModel.revenueByWeekFlow.collectLatest {
                    if (it.isNotEmpty()) {
                        setupPieChart(it)
                        binding.layoutAnyChartView.visibility = View.VISIBLE
                    } else {
                        binding.layoutAnyChartView.visibility = View.INVISIBLE
                    }
                }
            }
            launch {
                viewModel.errorFlow.collectLatest {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val savedServer = sharedPref.getString("server_address", "")

        viewModel.server = savedServer ?: ""
        viewModel.fetchDataInTime()
        binding.ivPrev.setOnClickListener {
            viewModel.prevTime()
        }

        binding.ivNext.setOnClickListener {
            viewModel.nextTime()
        }
    }

    private fun setupPieChart(result: List<RevenueByWeek>) {
        val anyChartView = AnyChartView(requireContext())
        anyChartView.setProgressBar(binding.progressBar)

        val pie = AnyChart.pie()

        pie.setOnClickListener(object : ListenersInterface.OnClickListener(arrayOf("x", "value")) {
            override fun onClick(event: Event) {
            }
        })

        val data: MutableList<DataEntry> = ArrayList()
        for (revenue in result) {
            data.add(ValueDataEntry("Week ${revenue.week}", revenue.total))
        }

        pie.data(data)

        pie.title("")

        pie.labels().position("outside")

        pie.legend().title().enabled(true)
        pie.legend().title()
            .text("Week")
            .padding(0.0, 0.0, 10.0, 0.0)

        pie.legend()
            .position("center-bottom")
            .itemsLayout(LegendLayout.HORIZONTAL)
            .align(Align.CENTER)

        anyChartView.setChart(pie)
        binding.layoutAnyChartView.addView(anyChartView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}