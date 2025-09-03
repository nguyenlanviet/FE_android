package com.food.order.ui.table

import android.os.Build
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.food.order.data.mapper.toTableModel
import com.food.order.data.model.TableModel
import com.food.order.data.repository.TableRepository
import com.food.order.data.request.TableRequest
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class TableViewModel : ViewModel() {

    private val repository = TableRepository()

    private val _loadingFlow = MutableSharedFlow<Boolean>(replay = 0)
    val loadingFlow = _loadingFlow.asSharedFlow()

    private val _errorFlow = MutableSharedFlow<String>(replay = 0)
    val errorFlow = _errorFlow.asSharedFlow()

    private val _tablesFlow = MutableSharedFlow<List<TableModel>>(replay = 0)
    val tablesFlow = _tablesFlow.asSharedFlow()

    private val _insertFlow = MutableSharedFlow<Boolean>()
    val insertFlow = _insertFlow.asSharedFlow()

    private val _tableFlow = MutableSharedFlow<TableModel?>()
    val tableFlow = _tableFlow.asSharedFlow()

    private val _updateFlow = MutableSharedFlow<Boolean>()
    val updateFlow = _updateFlow.asSharedFlow()

    private val _deleteFlow = MutableSharedFlow<Boolean>()
    val deleteFlow = _deleteFlow.asSharedFlow()

    private var editTable: TableModel? = null

    fun createTable(token: String, request: TableRequest) {
        viewModelScope.launch {
            _loadingFlow.emit(true)
            try {
                val response = repository.createTable(token, request)
                if (response.code == 0) {
                    _insertFlow.emit(true)
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

    fun updateTable(token: String, request: TableRequest) {
        viewModelScope.launch {
            if (editTable == null) {
                _errorFlow.emit("Table is null")
                return@launch
            }
            _loadingFlow.emit(true)
            try {
                val response = repository.updateTable(token, editTable!!.id, request)
                if (response.code == 0) {
                    _updateFlow.emit(true)
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

    @Suppress("DEPRECATION")
    fun setArgument(bundle: Bundle?) {
        viewModelScope.launch {
            bundle?.let {
                if (it.containsKey("edit_table")) {
                    editTable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        it.getSerializable("edit_table", TableModel::class.java)
                    } else {
                        it.getSerializable("edit_table") as TableModel
                    }
                    _tableFlow.emit(editTable!!)
                }
            }
        }
    }

    fun deleteTable(token: String) {
        viewModelScope.launch {
            if (token.isEmpty() || editTable == null) {
                _errorFlow.emit("Token is empty")
            }
            _loadingFlow.emit(true)
            try {
                val response = repository.deleteTable(token, editTable!!.id)
                _deleteFlow.emit(response.code == 0)
            } catch (e: Exception) {
                _errorFlow.emit("Failed: ${e.message}")
            } finally {
                _loadingFlow.emit(false)
            }
        }
    }
}