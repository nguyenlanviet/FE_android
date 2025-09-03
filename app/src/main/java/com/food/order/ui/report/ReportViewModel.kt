package com.food.order.ui.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.food.order.data.model.Order
import com.food.order.data.repository.OrderRepository
import com.food.order.data.repository.UserRepository
import com.food.order.data.response.MostFavoriteFoodResponse
import com.food.order.data.response.RevenueByWeek
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

class ReportViewModel : ViewModel() {

    private val repository = UserRepository()
    private val orderRepository = OrderRepository()

    var server: String = ""

    private val _loadingFlow = MutableSharedFlow<Boolean>(replay = 0)
    val loadingFlow = _loadingFlow.asSharedFlow()

    private val _countEmployeeFlow = MutableSharedFlow<Long>(replay = 0)
    val countEmployeeFlow = _countEmployeeFlow.asSharedFlow()

    private val _errorFlow = MutableSharedFlow<String>(replay = 0)
    val errorFlow = _errorFlow.asSharedFlow()

    private val _mostFavoriteFoodFlow = MutableSharedFlow<MostFavoriteFoodResponse?>()
    val mostFavoriteFoodFlow = _mostFavoriteFoodFlow.asSharedFlow()

    private val _revenueByWeekFlow = MutableSharedFlow<List<RevenueByWeek>>()
    val revenueByWeekFlow = _revenueByWeekFlow.asSharedFlow()

    private val _listOrderInTimeFlow = MutableSharedFlow<List<Order>?>()
    val listOrderInTimeFlow = _listOrderInTimeFlow.asSharedFlow()

    private val _timeFlow = MutableSharedFlow<String>(replay = 0)
    val timeFlow = _timeFlow.asSharedFlow()

    private var currentMonth = YearMonth.now()
    private val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)
    private val formatterParams = DateTimeFormatter.ofPattern("MM-yyyy", Locale.ENGLISH)

    fun prevTime() {
        currentMonth = currentMonth.minusMonths(1)
        fetchDataInTime()
    }

    fun nextTime() {
        currentMonth = currentMonth.plusMonths(1)
        fetchDataInTime()
    }

    fun fetchDataInTime() {
        viewModelScope.launch {
            _timeFlow.emit(formatter.format(currentMonth))
        }
        getCountEmployee(server)
        getMostFavoriteFoodInTime(server)
        getListOrderInTime(server)
        getRevenueByWeek(server)
    }

    private fun getCountEmployee(server: String) {
        viewModelScope.launch {
            try {
                val result = repository.getCountEmployee(server)
                _countEmployeeFlow.emit(result.data)
            } catch (e: Exception) {
                _errorFlow.emit("Failed: ${e.message}")
            }
        }
    }

    private fun getMostFavoriteFoodInTime(server: String) {
        val time = formatterParams.format(currentMonth)
        viewModelScope.launch {
            _loadingFlow.emit(true)
            try {
                val response = orderRepository.getMostFavoriteFood(server, time)
                if (response.code == 0) {
                    _mostFavoriteFoodFlow.emit(response)
                } else {
                    _errorFlow.emit(response.message)
                }
            } catch (e: Exception) {
                _errorFlow.emit(e.message ?: "Unknown error")
            } finally {
                _loadingFlow.emit(false)
            }
        }
    }

    private fun getRevenueByWeek(server: String) {
        val time = formatterParams.format(currentMonth)
        viewModelScope.launch {
            _loadingFlow.emit(true)
            try {
                val response = orderRepository.getRevenueByWeek(server, time)
                if (response.code == 0) {
                    _revenueByWeekFlow.emit(response.data)
                } else {
                    _errorFlow.emit(response.message)
                }
            } catch (e: Exception) {
                _errorFlow.emit(e.message ?: "Unknown error")
            } finally {
                _loadingFlow.emit(false)
            }
        }
    }

    private fun getListOrderInTime(server: String) {
        val time = formatterParams.format(currentMonth)
        viewModelScope.launch {
            _loadingFlow.emit(true)
            try {
                val response = orderRepository.getListOrderInTime(server, time)
                if (response.code == 0) {
                    _listOrderInTimeFlow.emit(response.data)
                } else {
                    _errorFlow.emit(response.message)
                }
            } catch (e: Exception) {
                _errorFlow.emit(e.message ?: "Unknown error")
            } finally {
                _loadingFlow.emit(false)
            }
        }
    }
}