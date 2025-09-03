package com.food.order.ui.staff

import android.os.Build
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.food.order.data.model.UserModel
import com.food.order.data.repository.UserRepository
import com.food.order.data.request.RegisterRequest
import com.food.order.data.request.UpdateStaffRequest
import com.food.order.data.response.UserResponse
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class StaffViewModel : ViewModel() {
    private val repository = UserRepository()

    private val _loadingFlow = MutableSharedFlow<Boolean>(replay = 0)
    val loadingFlow = _loadingFlow.asSharedFlow()
    private val _errorFlow = MutableSharedFlow<String>()
    val errorFlow = _errorFlow.asSharedFlow()
    private val _usersFlow = MutableSharedFlow<List<UserResponse>>()
    val usersFlow = _usersFlow.asSharedFlow()
    private val _insertFlow = MutableSharedFlow<Boolean>()
    val insertFlow = _insertFlow.asSharedFlow()

    private val _updateFlow = MutableSharedFlow<Boolean>()
    val updateFlow = _updateFlow.asSharedFlow()

    private val _deleteFlow = MutableSharedFlow<Boolean>()
    val deleteFlow = _deleteFlow.asSharedFlow()

    private val _staffFlow = MutableSharedFlow<UserModel?>()
    val staffFlow = _staffFlow.asSharedFlow()

    fun getUserFromServer(token: String, server: String) {
        viewModelScope.launch {
            _loadingFlow.emit(true)
            try {
                val response = repository.getUsersFromServer("Bearer $token", server)
                _usersFlow.emit(response.data)
                _loadingFlow.emit(false)
            } catch (e: Exception) {
                _errorFlow.emit("Failed: ${e.message}")
                _loadingFlow.emit(false)
            }
        }
    }

    fun createStaff(token: String, request: RegisterRequest) {
        viewModelScope.launch {
            _loadingFlow.emit(true)
            try {
                val response = repository.register("Bearer $token", request)
                _insertFlow.emit(response.token.isNotEmpty())
                _loadingFlow.emit(false)
            } catch (e: Exception) {
                _errorFlow.emit("Failed: ${e.message}")
                _loadingFlow.emit(false)
            }
        }
    }

    fun updateStaff(token: String, server: String, employeeId: String, request: UpdateStaffRequest) {
        viewModelScope.launch {
            _loadingFlow.emit(true)
            try {
                val response = repository.updateUserFromServer("Bearer $token", server, employeeId, request)
                _updateFlow.emit(response.code == 0)
                _loadingFlow.emit(false)
            } catch (e: Exception) {
                _errorFlow.emit("Failed: ${e.message}")
                _loadingFlow.emit(false)
            }
        }
    }

    fun deleteStaff(token: String, server: String, employeeId: String) {
        viewModelScope.launch {
            _loadingFlow.emit(true)
            try {
                val response = repository.deleteUserFromServer("Bearer $token", server, employeeId)
                _deleteFlow.emit(response.code == 0)
                _loadingFlow.emit(false)
            } catch (e: Exception) {
                _errorFlow.emit("Failed: ${e.message}")
                _loadingFlow.emit(false)
            }
        }
    }

    fun setArgument(bundle: Bundle?) {
        viewModelScope.launch {
            bundle?.let {
                if (it.containsKey("edit_staff")) {
                    val staff = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        it.getSerializable("edit_staff", UserModel::class.java)
                    } else {
                        it.getSerializable("edit_staff") as UserModel
                    }
                    _staffFlow.emit(staff)
                }
            }
        }
    }
}