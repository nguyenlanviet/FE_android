package com.food.order.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.food.order.data.mapper.toTableModel
import com.food.order.data.model.TableModel
import com.food.order.data.repository.OrderRepository
import com.food.order.data.repository.TableRepository
import com.food.order.data.request.OrderRequest
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    private val repository = TableRepository()
    private val orderRepository = OrderRepository()

    private val _errorFlow = MutableSharedFlow<String>(replay = 0)
    val errorFlow = _errorFlow.asSharedFlow()

    private val _loadingFlow = MutableSharedFlow<Boolean>(replay = 0)
    val loadingFlow = _loadingFlow.asSharedFlow()

    private val _tablesFlow = MutableSharedFlow<List<TableModel>>(replay = 0)
    val tablesFlow = _tablesFlow.asSharedFlow()

    private val _bookingFlow = MutableSharedFlow<String?>(replay = 0)
    val bookingFlow = _bookingFlow.asSharedFlow()

    fun getTablesFromServer(token: String) {
        viewModelScope.launch {
            _loadingFlow.emit(true)
            try {
                val response = repository.getTablesFromServer(token)
                if (response.code == 0) {
                    _tablesFlow.emit(response.data.map { it.toTableModel() })
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

    fun bookTable(tableId: String, token: String) {
        viewModelScope.launch {
            _loadingFlow.emit(true)
            try {
                val response = orderRepository.createOrder(token, OrderRequest(tableId))
                if (response.code == 0) {
                    _bookingFlow.emit(tableId)
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