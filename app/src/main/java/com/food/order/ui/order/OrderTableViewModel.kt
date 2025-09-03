package com.food.order.ui.order

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.food.order.data.mapper.toTableModel
import com.food.order.data.model.Order
import com.food.order.data.model.TableModel
import com.food.order.data.repository.OrderRepository
import com.food.order.data.repository.TableRepository
import com.food.order.data.request.CopyItemsRequest
import com.food.order.data.response.EmployeeOrderResponse
import com.food.order.data.response.OrderResponse
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class OrderTableViewModel : ViewModel() {

    private val tableRepository = TableRepository()
    private val orderRepository = OrderRepository()

    private val _loadingFlow = MutableSharedFlow<Boolean>(replay = 0)
    val loadingFlow = _loadingFlow.asSharedFlow()

    private val _errorFlow = MutableSharedFlow<String>(replay = 0)
    val errorFlow = _errorFlow.asSharedFlow()

    private val _tableFlow = MutableSharedFlow<TableModel?>()
    val tableFlow = _tableFlow.asSharedFlow()

    private val _employeeOrderFlow = MutableSharedFlow<EmployeeOrderResponse?>()
    val employeeOrderFlow = _employeeOrderFlow.asSharedFlow()

    private val _copyOrderFlow = MutableSharedFlow<Boolean?>()
    val copyOrderFlow = _copyOrderFlow.asSharedFlow()

    private val _orderFlow = MutableSharedFlow<OrderResponse>()
    val orderFlow = _orderFlow.asSharedFlow()

    private val _cancelOrderFlow = MutableSharedFlow<Boolean>()
    val cancelOrderFlow = _cancelOrderFlow.asSharedFlow()

    private val _completedOrderFlow = MutableSharedFlow<String>()
    val completedOrderFlow = _completedOrderFlow.asSharedFlow()

    private val _listOrderFlow = MutableSharedFlow<List<Order>>()
    val listOrderFlow = _listOrderFlow.asSharedFlow()

    private val _removeItemFromOrderFlow = MutableSharedFlow<Boolean>()
    val removeItemFromOrderFlow = _removeItemFromOrderFlow.asSharedFlow()

    private val _tablesFreeFlow = MutableSharedFlow<List<TableModel>>(replay = 0)
    val tablesFreeFlow = _tablesFreeFlow.asSharedFlow()

    private var tableId: String? = null
    var orderId: String? = null
        private set

    fun setArguments(bundle: Bundle?) {
        tableId = bundle?.getString("tableId")
    }

    fun getDetailTable(token: String) {
        if (tableId == null) return
        viewModelScope.launch {
            _loadingFlow.emit(true)
            try {
                val response = tableRepository.getTableByIdAndServer(token, tableId!!)
                if (response.code == 0) {
                    orderId = response.data.currentOrderId
                    _tableFlow.emit(response.data.toTableModel())
                    getOrderInfo(token)
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

    fun getCreateByOrder(token: String) {
        if (tableId == null) return
        viewModelScope.launch {
            _loadingFlow.emit(true)
            try {
                val response = tableRepository.getCreateByOrder(token, tableId!!)
                if (response.code == 0) {
                    _employeeOrderFlow.emit(response)
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

    private fun getOrderInfo(token: String) {
        if (orderId == null) return
        viewModelScope.launch {
            _loadingFlow.emit(true)
            try {
                val response = orderRepository.getOrder(token, orderId!!)
                if (response.code == 0) {
                    _orderFlow.emit(response)
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

    fun cancelOrder(token: String) {
        if (orderId.isNullOrEmpty()) return
        viewModelScope.launch {
            _loadingFlow.emit(true)
            try {
                val response = orderRepository.cancelOrder(token, orderId!!)
                if (response.code == 0) {
                    _cancelOrderFlow.emit(true)
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

    fun completeOrder(token: String) {
        if (orderId.isNullOrEmpty()) return
        viewModelScope.launch {
            _loadingFlow.emit(true)
            try {
                val response = orderRepository.complete(token, orderId!!)
                if (response.code == 0) {
                    _completedOrderFlow.emit(orderId!!)
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

    fun listOrders(token: String) {
        viewModelScope.launch {
            _loadingFlow.emit(true)
            try {
                val response = orderRepository.listOrders(token)
                if (response.code == 0) {
                    _listOrderFlow.emit(response.data)
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

    fun removeItemFromOrder(token: String, foodId: String) {
        if (orderId.isNullOrEmpty()) return
        viewModelScope.launch {
            _loadingFlow.emit(true)
            try {
                val response = orderRepository.removeItemFromOrder(token, orderId!!, foodId)
                if (response.code == 0) {
                    _removeItemFromOrderFlow.emit(true)
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

    fun getTablesFromServer(token: String) {
        viewModelScope.launch {
            _loadingFlow.emit(true)
            try {
                val response = tableRepository.getTablesFreeFromServer(token)
                if (response.code == 0) {
                    _tablesFreeFlow.emit(response.data.map { it.toTableModel() })
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

    fun copyTableOrder(token: String, targetTableId: String) {
        if (tableId == null) return
        viewModelScope.launch {
            _loadingFlow.emit(true)
            try {
                val request = CopyItemsRequest(sourceTableId = tableId!!, targetTableId = targetTableId)
                val response = tableRepository.copyTableOrder(token, request)
                if (response.code == 0) {
                    _copyOrderFlow.emit(true)
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